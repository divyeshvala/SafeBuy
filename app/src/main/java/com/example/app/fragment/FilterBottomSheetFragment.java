package com.example.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    public FilterBottomSheetFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.filter_bottom_sheet, container, false);

        String[] category = new String[] {"Item 1", "Item 2", "Item 3", "Item 4"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        category);
        AutoCompleteTextView editTextFilledExposedDropdown =
                view.findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);


        String[] distance_format = new String[] {"km", "m"};
        ArrayAdapter<String> distance_adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        distance_format);
        AutoCompleteTextView edittextFilledDistanceValues =
                view.findViewById(R.id.distance_dropdown);
        edittextFilledDistanceValues.setText(distance_format[0]);
        edittextFilledDistanceValues.setAdapter(distance_adapter);
        //set default value




        return view;
    }
}
