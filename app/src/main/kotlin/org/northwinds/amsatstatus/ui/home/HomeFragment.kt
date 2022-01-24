/**********************************************************************************
 * Copyright (c) 2022 Loren M. Lang                                               *
 *                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy   *
 * of this software and associated documentation files (the "Software"), to deal  *
 * in the Software without restriction, including without limitation the rights   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      *
 * copies of the Software, and to permit persons to whom the Software is          *
 * furnished to do so, subject to the following conditions:                       *
 *                                                                                *
 * The above copyright notice and this permission notice shall be included in all *
 * copies or substantial portions of the Software.                                *
 *                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  *
 * SOFTWARE.                                                                      *
 **********************************************************************************/

/**********************************************************************************
 * Copyright (c) 2020 Loren M. Lang                                               *
 *                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy   *
 * of this software and associated documentation files (the "Software"), to deal  *
 * in the Software without restriction, including without limitation the rights   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell      *
 * copies of the Software, and to permit persons to whom the Software is          *
 * furnished to do so, subject to the following conditions:                       *
 *                                                                                *
 * The above copyright notice and this permission notice shall be included in all *
 * copies or substantial portions of the Software.                                *
 *                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  *
 * SOFTWARE.                                                                      *
 **********************************************************************************/

package org.northwinds.amsatstatus.ui.home

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.amsatstatus.*
import org.northwinds.amsatstatus.databinding.FragmentHomeBinding
import org.northwinds.amsatstatus.util.Clock
import org.northwinds.amsatstatus.util.Locator
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var clock: Clock

    //private ArrayAdapter<CharSequence>  mSatelliteAdapter;

    private val REQUEST_PERMISSION_LOCATION: Int = 1000
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    lateinit var callsign: EditText
    lateinit var gridsquare: EditText
    lateinit var timeMode: TextView

    lateinit var prefs: SharedPreferences

    inner class Changer : SharedPreferences.OnSharedPreferenceChangeListener {
        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

        override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
            if (key!! == context!!.getString(R.string.preference_local_time)) {
                if (prefs!!.getBoolean(context!!.getString(R.string.preference_local_time),
                        false)
                ) {
                    timeMode.setText(R.string.local_time)
                } else {
                    timeMode.setText(R.string.utc_time)
                }
            }
            if (key == getString(R.string.preference_callsign)) {
                callsign.text =
                    prefs!!.getString(getString(R.string.preference_callsign), "")?.toEditable()
            }
            if (key == getString(R.string.preference_default_grid)) {
                gridsquare.text =
                    prefs!!.getString(getString(R.string.preference_default_grid), "")?.toEditable()
            }
        }
    }

    val changer = Changer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mFirebaseAnalytics = Firebase.analytics
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Home")
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)

//        val homeViewModel by viewModels<HomeViewModel>()
        val homeViewModel: HomeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        val timePicker =
            binding.timeFixture
        //        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
//        {
//            @Override
//            public void onTimeChanged (TimePicker view, int hourOfDay, int minute)
//            {
//                view.setOnTimeChangedListener(null);
//                int currentMinute = roundMinute(minute);
//                view.setCurrentMinute(59);
//                view.setCurrentMinute(currentMinute);
//                view.setOnTimeChangedListener(this);
//            }
//        });
        timePicker.setIs24HourView(true)

        val datePicker = binding.dateFixture
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        timeMode = binding.timeMode
        val pickerTime =
            if (prefs.getBoolean(requireContext().getString(R.string.preference_local_time),
                    false)
            ) {
                timeMode.setText(R.string.local_time)
                clock.localCalendar
            } else {
                timeMode.setText(R.string.utc_time)
                clock.utcCalendar
            }
        prefs.registerOnSharedPreferenceChangeListener(changer)
        datePicker.updateDate(
            pickerTime.get(Calendar.YEAR),
            pickerTime.get(Calendar.MONTH),
            pickerTime.get(Calendar.DAY_OF_MONTH))
        if (Build.VERSION.SDK_INT < 23) {
            timePicker.currentHour = pickerTime.get(Calendar.HOUR_OF_DAY)
            timePicker.currentMinute = pickerTime.get(Calendar.MINUTE) / 15
        } else {
            timePicker.hour = pickerTime.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = pickerTime.get(Calendar.MINUTE) / 15
        }
        callsign = binding.callsign
        callsign.setText(prefs.getString(requireContext().getString(R.string.preference_callsign),
            ""))
        gridsquare = binding.gridsquare
        gridsquare.setText(prefs.getString(requireContext().getString(R.string.preference_default_grid),
            ""))

        val satelliteSpinner = binding.satHeard
        val satHeard =
            prefs.getString(requireContext().getString(R.string.preference_satellite), "")
        val idx = requireContext().resources.getStringArray(R.array.satellite_ids).indexOf(satHeard)
        if (idx >= 0)
            satelliteSpinner.setSelection(idx)

        val locationBtn =
            binding.locationButton
        locationBtn.setOnClickListener {
            val service =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                if (!checkPermissionForLocation(requireContext()))
                    return@setOnClickListener
                val location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                service.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
//                        gridsquare.setText("" + location.longitude + ":" + location.latitude)
                            gridsquare.setText(Locator.coordToGrid(location.latitude,
                                location.longitude).subSequence(0, 6))
                        }

                        override fun onStatusChanged(
                            provider: String,
                            status: Int,
                            extras: Bundle,
                        ) {
                        }

                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    },
                    Looper.getMainLooper())
//                service.getCurrentLocation(LocationManager.NETWORK_PROVIDER, null, requireContext().mainExecutor,
//                    Consumer { location ->
//                        gridsquare.setText(location.latitude.toString())
//                    })
                if (location != null) {
                    gridsquare.setText(Locator.coordToGrid(location.latitude, location.longitude)
                        .subSequence(0, 6))
                }
            } catch (ex: SecurityException) {
                return@setOnClickListener
            }
        }
        setTimePickerInterval(timePicker)
        val submitBtn =
            binding.submitButton
        submitBtn.setOnClickListener {
            val spinner =
                binding.satHeard
            val id = spinner.selectedItemPosition
            val satelliteIds =
                resources.getStringArray(R.array.satellite_ids)
            val report =
                binding.reportStatus
            val value: String
            value = when (report.checkedRadioButtonId) {
                R.id.uplinkAndDownlinkActiveRadio -> "Heard"
                R.id.telemetryOnlyRadio -> "Telemetry Only"
                R.id.notHeardRadio -> "Not Heard"
                R.id.crewActiveRadio -> "Crew Active"
                else -> "Crew Active"
            }
            val reportType = when (report.checkedRadioButtonId) {
                R.id.uplinkAndDownlinkActiveRadio -> Report.HEARD
                R.id.telemetryOnlyRadio -> Report.TELEMETRY_ONLY
                R.id.notHeardRadio -> Report.NOT_HEARD
                R.id.crewActiveRadio -> Report.CREW_ACTIVE
                else -> Report.NOT_HEARD
            }
            val day = datePicker.dayOfMonth.toString()
            val month = String.format("%02d", datePicker.month + 1)
            val year = datePicker.year.toString()
            val timePicker1 =
                binding.timeFixture
            val hour: String
            val period: String
            if (Build.VERSION.SDK_INT < 23) {
                hour = timePicker1.currentHour.toString()
                period = timePicker1.currentMinute.toString()
            } else {
                hour = timePicker1.hour.toString()
                period = timePicker1.minute.toString()
            }

            val callsign_w = binding.callsign
            val callsign = callsign_w.text
            val grid_w = binding.gridsquare
            val grid = grid_w.text
            val calendar =
                if (prefs.getBoolean(requireContext().getString(R.string.preference_local_time),
                        false)
                ) {
                    Calendar.getInstance()
                } else {
                    Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                }
            calendar.set(Calendar.YEAR, datePicker.year)
            calendar.set(Calendar.MONTH, datePicker.month)
            calendar.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, if (Build.VERSION.SDK_INT < 23) {
                timePicker1.currentHour
            } else {
                timePicker1.hour
            })
            calendar.set(Calendar.MINUTE, if (Build.VERSION.SDK_INT < 23) {
                timePicker1.currentMinute
            } else {
                timePicker1.minute
            } * 15)
            calendar.set(Calendar.SECOND, 0)
            val time = ReportTime(calendar)
            val satReport =
                SatReport(satelliteIds[id], reportType, time, callsign.toString(), grid.toString())
            Toast.makeText(
                requireActivity().applicationContext,
                "Submit SatName: " + satelliteIds[id] + ", SatReport: " + value + ", Period: " + period + ", SatHour: " + hour + ", SatDay: " + day + ", SatMonth: " + month + ", SatYear: " + year + ", SatCall: " + callsign + ", SatGridSquare: " + grid,
                Toast.LENGTH_LONG
            ).show()
            Toast.makeText(
                requireActivity().applicationContext,
                satReport.toString(),
                Toast.LENGTH_LONG
            ).show()
            val params = Bundle().apply {
                val statusId = when (reportType) {
                    Report.NOT_HEARD -> 0L
                    Report.TELEMETRY_ONLY -> 1
                    Report.HEARD -> 2
                    Report.CREW_ACTIVE -> 3
                    Report.CONFLICTED -> 4
                }
                putLong(FirebaseAnalytics.Param.SCORE, statusId)
                putLong(FirebaseAnalytics.Param.LEVEL, datePicker.month + 1L)
                putString(FirebaseAnalytics.Param.CHARACTER, satelliteIds[id])
                putString("satellite_report", reportType.value)
            }
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, params)
            class R : Runnable {
                override fun run() {
                    homeViewModel.mApi.sendReport(satReport)
                }
            }
            Thread(R()).start()
            //Thread(Runnable() {
            //    public override fun run() {
            //        api.sendReport(satReport)
            //    }
            //}).start()
        }
        return binding.root
    }

    override fun onDestroyView() {
        prefs.unregisterOnSharedPreferenceChangeListener(changer)
        super.onDestroyView()
    }

    private fun setTimePickerInterval(timePicker: TimePicker) {
        try {
            val classForid =
                Class.forName("com.android.internal.R\$id")
            // Field timePickerField = classForid.getField("timePicker");
//            val field = try {classForid.getField("minute")} catch(ex: NoSuchFieldException) {classForid.getField("currentMinute")}
//            val minutePicker = timePicker
//                .findViewById<View>(field.getInt(null)) as NumberPicker
            val minutePicker: NumberPicker = timePicker.findViewById(
                Resources.getSystem().getIdentifier(
                    "minute",
                    "id",
                    "android"
                )
            )
            val TIME_PICKER_INTERVAL = 15
            minutePicker.minValue = 0
            minutePicker.maxValue = 3
            val displayedValues =
                ArrayList<String>()
            run {
                var i = 0
                while (i < 60) {
                    displayedValues.add(String.format("%02d", i))
                    i += TIME_PICKER_INTERVAL
                }
            }
            minutePicker.displayedValues = displayedValues
                .toTypedArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    companion object {
        private fun roundMinute(minute: Int): Int {
            return if (minute == 0) 0 else minute / 5 * 5
        }
    }
}
