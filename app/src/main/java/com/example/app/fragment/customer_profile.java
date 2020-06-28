package com.example.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.app.R;

public class customer_profile extends Fragment {

    ImageView im;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_customer_profile, container, false);
        im = rootView.findViewById(R.id.transactions);
        im.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                FragmentManager fx = getActivity().getSupportFragmentManager();
//                FragmentManager fx = getFragmentManager();
                fx.beginTransaction().replace(R.id.holder,new fragment_transactions()).addToBackStack(null).commit();

            }
        });
        return rootView;
    }
}
