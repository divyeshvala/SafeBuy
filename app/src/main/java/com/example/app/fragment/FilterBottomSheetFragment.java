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
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "FilterBottomSheet";
    private BottomSheetListener bottomSheetListener;
    private List<String> va = new ArrayList<>();
    private String distanceText;
    private String distance_unit;
    //private boolean filter_changed;
    public FilterBottomSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void setDefaultValues(){
        System.out.println("DEfault filters set");
        addalltoVa();
        distanceText="1";
        distance_unit="KM";
        //filter_changed=false;
    }
    public void addalltoVa() {
        va.add("5411");
        va.add("5912");
        va.add("8062");
        va.add("5812");
        va.add("5814");
        va.add("5462");
        va.add("5137");
        va.add("7230");
    }

    public List<String> getCategoryCodes() {
        return va;
    }
    public String getDistanceText(){
        return distanceText;
    }
    public String getDistance_unit(){
        return distance_unit;
    }
    public void setVa(String categorydesc){
        va.clear();
        if(categorydesc.equals("All")){
            addalltoVa();
        }
        else if(categorydesc.equals("Groceries and Supermarkets")){
            va.add("5411");
        }
        else if(categorydesc.equals("Pharmacies")){
            va.add("5912");
        }
        else if(categorydesc.equals("Hospitals")){
            va.add("8062");
        }
        else if(categorydesc.equals("Eateries")){
            va.add("5812");
            va.add("5814");
            va.add("5462");
        }
        else if(categorydesc.equals("Cloth Stores")){
            va.add("5137");
        }
        else{
            va.add("7230");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Inside filtercreateview");
        View view =  inflater.inflate(R.layout.filter_bottom_sheet, container, true);

        String[] category = new String[] {"All","Groceries and Supermarkets","Pharmacies","Hospitals","Eateries","Cloth Stores","Barber Shop"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        category);
//        final AutoCompleteTextView editTextFilledExposedDropdown =
//                view.findViewById(R.id.filled_exposed_dropdown);
//        editTextFilledExposedDropdown.setAdapter(adapter);

        final Spinner spinnerCategory = view.findViewById(R.id.categoryDropdown);
        spinnerCategory.setAdapter(adapter);

        String[] distance_format = new String[] {"km", "m"};
        ArrayAdapter<String> distance_adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.dropdown_menu_popup_item,
                        distance_format);
        final AutoCompleteTextView edittextFilledDistanceValues =
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
                Log.d(TAG, "onClick: apply");
                System.out.println("Filter okay clicked");
                Integer distance_value = 0;
                if(!distance.getEditText().getText().toString().equals(""))
                    distance_value = Integer.valueOf(distance.getEditText().getText().toString());
                String distancedesc=edittextFilledDistanceValues.getText().toString();
                if(distancedesc.equals("km")){
                    distance_unit="KM";
                }
                else{
                    distance_unit="M";
                }
                distanceText=distance_value.toString();
                String categorydesc=String.valueOf(spinnerCategory.getSelectedItem());
                System.out.println(categorydesc);
                setVa(categorydesc);
                //System.out.println(va);
                //bottomSheetListener.onButtonClicked();
                System.out.println("Crossed onbuttonclicked");
                Intent intent = new Intent("ACTION_FILTER_APPLIED");
                Objects.requireNonNull(getActivity()).sendBroadcast(intent);
                System.out.println("Intent is sent");
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
