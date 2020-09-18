package org.northwinds.amsatstatus.ui.home

import java.util.Calendar
import java.util.TimeZone

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_dashboard_item.*
import org.northwinds.amsatstatus.*

class HomeFragment(private val clock: Clock, private val api: AmsatApi) : Fragment() {
    constructor() : this(Clock(), AmsatApi())

    //private ArrayAdapter<CharSequence>  mSatelliteAdapter;

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        /*
        final Spinner spinner = (Spinner) root.findViewById(R.id.satHeard);
        ArrayAdapter<CharSequence> mSatelliteAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.satellite_ids, android.R.layout.simple_spinner_item);
        mSatelliteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mSatelliteAdapter);
        */
        val timePicker =
            root.findViewById<View>(R.id.time_fixture) as TimePicker
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

        var date_picker = root.findViewById(R.id.date_fixture) as DatePicker
        val prefs = PreferenceManager(context).sharedPreferences
        //val clock = Clock()
        val timeMode = root.findViewById(R.id.time_mode) as TextView
        val picker_time = if(prefs.getBoolean(context!!.getString(R.string.preference_local_time), false)) {
            timeMode.setText(R.string.local_time)
            clock.localCalendar
        } else {
            timeMode.setText(R.string.utc_time)
            clock.utcCalendar
        }
        date_picker.updateDate(
                picker_time.get(Calendar.YEAR),
                picker_time.get(Calendar.MONTH),
                picker_time.get(Calendar.DAY_OF_MONTH))
        if(Build.VERSION.SDK_INT < 23) {
            timePicker.currentHour = picker_time.get(Calendar.HOUR_OF_DAY)
            timePicker.currentMinute = picker_time.get(Calendar.MINUTE) / 15
        } else {
            timePicker.hour = picker_time.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = picker_time.get(Calendar.MINUTE) / 15
        }
        val callsign_w = root.findViewById(R.id.callsign) as EditText
        callsign_w?.setText(prefs.getString(context!!.getString(R.string.preference_callsign), ""))
        val grid_w = root.findViewById(R.id.gridsquare) as EditText
        grid_w?.setText(prefs.getString(context!!.getString(R.string.preference_default_grid), ""))

        setTimePickerInterval(timePicker)
        val submit_btn =
            root.findViewById<View>(R.id.submit_button) as Button
        submit_btn.setOnClickListener {
            val spinner =
                root.findViewById<View>(R.id.satHeard) as Spinner
            val id = spinner.selectedItemPosition
            val satellite_ids =
                resources.getStringArray(R.array.satellite_ids)
            val report =
                root.findViewById<View>(R.id.report_status) as RadioGroup
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
            val day = date_picker.dayOfMonth.toString()
            val month = String.format("%02d", date_picker.month + 1)
            val year = date_picker.year.toString()
            val time_picker =
                root.findViewById<View>(R.id.time_fixture) as TimePicker
            var hour: String
            var period: String
            if(Build.VERSION.SDK_INT < 23) {
                hour = time_picker.currentHour.toString()
                period = time_picker.currentMinute.toString()
            } else {
                hour = time_picker.hour.toString()
                period = time_picker.minute.toString()
            }

            val callsign_w = root.findViewById(R.id.callsign) as EditText
            val callsign = callsign_w.text
            val grid_w = root.findViewById(R.id.gridsquare) as EditText
            val grid = grid_w.text
            val calendar = if(prefs.getBoolean(context!!.getString(R.string.preference_local_time), false)) {
                Calendar.getInstance()
            } else {
                Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            }
            calendar.set(Calendar.YEAR, date_picker.year)
            calendar.set(Calendar.MONTH, date_picker.month)
            calendar.set(Calendar.DAY_OF_MONTH, date_picker.dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, if(Build.VERSION.SDK_INT < 23) { time_picker.currentHour } else { time_picker.hour })
            calendar.set(Calendar.MINUTE, if(Build.VERSION.SDK_INT < 23) { time_picker.currentMinute } else { time_picker.minute } * 15)
            calendar.set(Calendar.SECOND, 0)
            val time = ReportTime(calendar)
            val satReport = SatReport(satellite_ids[id], reportType, time, callsign.toString(), grid.toString())
            Toast.makeText(
                activity!!.applicationContext,
                "Submit SatName: " + satellite_ids[id] + ", SatReport: " + value + ", Period: " + period + ", SatHour: " + hour + ", SatDay: " + day + ", SatMonth: " + month + ", SatYear: " + year + ", SatCall: " + callsign + ", SatGridSquare: " + grid,
                Toast.LENGTH_LONG
            ).show()
            Toast.makeText(
                activity!!.applicationContext,
                satReport.toString(),
                Toast.LENGTH_LONG
            ).show()
            //thread() {
            //    api.sendreport(satreport)
            //}
            class R: Runnable {
                public override fun run() {
                    api.sendReport(satReport)
                }
            }
            Thread(R()).start()
            //Thread(Runnable() {
            //    public override fun run() {
            //        api.sendReport(satReport)
            //    }
            //}).start()
        }
        Toast.makeText(
            activity!!.applicationContext,
            "Application is loaded!",
            Toast.LENGTH_SHORT
        ).show()
        return root
    }

    //@SuppressLint("NewApi")
    private fun setTimePickerInterval(timePicker: TimePicker) {
        try {
            val classForid =
                Class.forName("com.android.internal.R\$id")
            // Field timePickerField = classForid.getField("timePicker");
            val field = classForid.getField("minute")
            val minutePicker = timePicker
                .findViewById<View>(field.getInt(null)) as NumberPicker
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
            var i = 0
            while (i < 60) {
                displayedValues.add(String.format("%02d", i))
                i += TIME_PICKER_INTERVAL
            }
            minutePicker.displayedValues = displayedValues
                .toTypedArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private fun roundMinute(minute: Int): Int {
            return if (minute == 0) 0 else minute / 5 * 5
        }
    }
}
