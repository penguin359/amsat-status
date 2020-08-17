package org.northwinds.amsatstatus.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.northwinds.amsatstatus.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText("");
            }
        });
        */

        Spinner spinner = (Spinner) root.findViewById(R.id.satHeard);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.satellite_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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