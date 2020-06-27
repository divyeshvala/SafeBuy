package com.example.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.app.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Objects;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "FilterBottomSheet";
    private BottomSheetListener bottomSheetListener;

    public FilterBottomSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.filter_bottom_sheet, container, true);

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

        final TextInputLayout distance = view.findViewById(R.id.distance);
        final Button applyFilterBTN = view.findViewById(R.id.id_apply_filter_btn);

        applyFilterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String distanceText = distance.getEditText().getText().toString();

                bottomSheetListener.onButtonClicked();
                Intent intent = new Intent("ACTION_FILTER_APPLIED");
                intent.putExtra("distance", distanceText);
                Objects.requireNonNull(getActivity()).sendBroadcast(intent);

            }
        });
        return view;
    }

    public interface BottomSheetListener{
        void onButtonClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            bottomSheetListener = (BottomSheetListener) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
