package com.example.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

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

        final TextInputLayout location = view.findViewById(R.id.location);
        final TextInputLayout distance = view.findViewById(R.id.distance);
        final Button applyFilterBTN = view.findViewById(R.id.id_apply_filter_btn);

        applyFilterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String locationText = location.getEditText().getText().toString();
                String distanceText = distance.getEditText().getText().toString();

                if(locationText.equals(""))
                {
                    Toast.makeText(getActivity(), "Please enter the location", Toast.LENGTH_LONG).show();
                }
                else{
                    //bottomSheetListener.onButtonClicked(locationText, distanceText);
                    Intent intent = new Intent("ACTION_FILTER_APPLIED");
                    intent.putExtra("addressLine", locationText);
                    intent.putExtra("distance", distanceText);
                    getActivity().sendBroadcast(intent);
                }
            }
        });
        return view;
    }

    public interface BottomSheetListener{
        void onButtonClicked(String location, String distance);
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try{
//            bottomSheetListener = (BottomSheetListener) context;
//        }catch (ClassCastException e){
//            e.printStackTrace();
//        }
//    }
}
