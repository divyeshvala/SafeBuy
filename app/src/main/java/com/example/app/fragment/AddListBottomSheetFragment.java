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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddListBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "AddListBottomSheetFragment";
    private BottomSheetListener bottomSheetListener;
    ArrayList<String> list = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> adapter;

    public AddListBottomSheetFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.bottom_sheet_add_list, container, true);

        ImageView btn =  view.findViewById(R.id.addItem);
        TextView takePicButton = view.findViewById(R.id.takePicButton);

        adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_list, list);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText) view.findViewById(R.id.addItemEditText);
                if(edit.getText().toString().length() > 0) {
                    list.add((list.size() + 1) + ": " + edit.getText().toString());
                    edit.setText("");
                    adapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getContext(), "Please enter an Item", Toast.LENGTH_SHORT).show();
            }
        };

        btn.setOnClickListener(listener);

        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Import Pic yet to be added", Toast.LENGTH_SHORT).show();
            }
        });

        ListView listView = view.findViewById(R.id.listItems);
        listView.setAdapter(adapter);

        final Button applyFilterBTN = view.findViewById(R.id.send_list);
        applyFilterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(list.size() == 0)
                {
                    Toast.makeText(getActivity(), "Please enter an Item to the list", Toast.LENGTH_SHORT).show();
                }
                else {
                    bottomSheetListener.onButtonClicked(list);
//                    Intent intent = new Intent("ACTION_FILTER_APPLIED");
//                    intent.putExtra("list", list);
//                    getActivity().sendBroadcast(intent);
                }
            }
        });

        return view;
    }

    public interface BottomSheetListener{
        void onButtonClicked(ArrayList<String> list);
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
