package org.northwinds.amsatstatus.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Spinner
import android.widget.NumberPicker
import android.widget.Button
import android.widget.Toast
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.northwinds.amsatstatus.R

class HomeFragment : Fragment() {

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
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = ""
        })
        */

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
            val date_picker =
                root.findViewById<View>(R.id.date_fixture) as DatePicker
            val day = date_picker.dayOfMonth.toString()
            val month = String.format("%02d", date_picker.month + 1)
            val year = date_picker.year.toString()
            val time_picker =
                root.findViewById<View>(R.id.time_fixture) as TimePicker
            val hour = time_picker.hour.toString()
            Toast.makeText(
                activity!!.applicationContext,
                "Submit SatName: " + satellite_ids[id] + ", SatReport: " + value + ", SatHour: " + hour + ", SatDay: " + day + ", SatMonth: " + month + ", SatYear: " + year,
                Toast.LENGTH_LONG
            ).show()
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
            minutePicker.maxValue = 7
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
