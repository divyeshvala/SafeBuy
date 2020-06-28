package com.example.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.app.Activities.Login;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manojbhadane.PaymentCardView;

import static android.content.Context.MODE_PRIVATE;

public class customer_profile extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_customer_profile, container, false);

        ImageView transactions = rootView.findViewById(R.id.transactions);
        transactions.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FragmentManager fx = getActivity().getSupportFragmentManager();
                fx.beginTransaction().replace(R.id.holder,new fragment_transactions()).addToBackStack(null).commit();
            }
        });

        return rootView;
    }
}
