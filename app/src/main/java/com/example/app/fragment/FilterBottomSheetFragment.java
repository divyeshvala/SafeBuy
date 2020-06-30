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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "FilterBottomSheet";

    public FilterBottomSheetFragment()
    { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setCategoryCodes(String category){
        if(category.equals("All"))
            FragmentMerchants.categories.clear();
        switch (category) {
            case "Groceries and Supermarkets":
                FragmentMerchants.categories.add("5411");
                break;
            case "Pharmacies":
                FragmentMerchants.categories.add("5912");
                break;
            case "Hospitals":
                FragmentMerchants.categories.add("8062");
                break;
            case "Eateries":
                FragmentMerchants.categories.add("5812");
                FragmentMerchants.categories.add("5814");
                FragmentMerchants.categories.add("5462");
                break;
            case "Cloth Stores":
                FragmentMerchants.categories.add("5137");
                break;
            default:
                FragmentMerchants.categories.add("7230");
                break;
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

        final Spinner spinnerCategory = view.findViewById(R.id.categoryDropdown);
        spinnerCategory.setAdapter(adapter);

        String[] distance_format = new String[] {"KM", "M"};
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

                FragmentMerchants.distanceText = distance.getEditText().getText().toString();
                final String category = String.valueOf(spinnerCategory.getSelectedItem());
                setCategoryCodes(category);

                Intent intent = new Intent("ACTION_FILTER_APPLIED");
                Objects.requireNonNull(getActivity()).sendBroadcast(intent);

            }
        });
        return view;
    }
}
