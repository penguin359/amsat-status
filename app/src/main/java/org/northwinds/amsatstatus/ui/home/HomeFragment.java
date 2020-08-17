package org.northwinds.amsatstatus.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.northwinds.amsatstatus.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    //private ArrayAdapter<CharSequence>  mSatelliteAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("");
            }
        });
        */

        /*
        final Spinner spinner = (Spinner) root.findViewById(R.id.satHeard);
        ArrayAdapter<CharSequence> mSatelliteAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.satellite_ids, android.R.layout.simple_spinner_item);
        mSatelliteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mSatelliteAdapter);
        */

        TimePicker timePicker = (TimePicker) root.findViewById(R.id.time_fixture);
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
        setTimePickerInterval(timePicker);

        Button submit_btn = (Button) root.findViewById(R.id.submit_button);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner spinner = (Spinner) root.findViewById(R.id.satHeard);
                int id = spinner.getSelectedItemPosition();
                String[] satellite_ids = getResources().getStringArray(R.array.satellite_ids);
                RadioGroup report = (RadioGroup) root.findViewById(R.id.report_status);
                String value;
                switch(report.getCheckedRadioButtonId()) {
                    case R.id.uplinkAndDownlinkActiveRadio:
                        value = "Heard";
                        break;
                    case R.id.telemetryOnlyRadio:
                        value = "Telemetry Only";
                        break;
                    case R.id.notHeardRadio:
                        value = "Not Heard";
                        break;
                    case R.id.crewActiveRadio:
                    default:
                        value = "Crew Active";
                }

                DatePicker date_picker = (DatePicker) root.findViewById(R.id.date_fixture);
                String day = String.valueOf(date_picker.getDayOfMonth());
                String month = String.format("%02d", date_picker.getMonth()+1);
                String year = String.valueOf(date_picker.getYear());

                TimePicker time_picker = (TimePicker) root.findViewById(R.id.time_fixture);
                String hour = String.valueOf(time_picker.getHour());
                Toast.makeText(getActivity().getApplicationContext(), "Submit SatName: " + satellite_ids[id] + ", SatReport: " + value + ", SatHour: " + hour + ", SatDay: " + day + ", SatMonth: " + month + ", SatYear: " + year, Toast.LENGTH_LONG).show();
            }
        });

        Toast.makeText(getActivity().getApplicationContext(), "Application is loaded!", Toast.LENGTH_SHORT).show();

        return root;
    }

    //@SuppressLint("NewApi")
    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            // Field timePickerField = classForid.getField("timePicker");
            Field field = classForid.getField("minute");
            NumberPicker minutePicker = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));

            final int TIME_PICKER_INTERVAL = 15;
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(7);
            ArrayList<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues
                    .toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int roundMinute (int minute)
    {
        return minute == 0 ? 0 : minute/5*5;
    }
}