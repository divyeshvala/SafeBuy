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
        
        PaymentCardView paymentCardView = rootView.findViewById(R.id.creditCard);
        final SharedPreferences settings = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);

        paymentCardView.setCardTitle("PAN");
        paymentCardView.setOnPaymentCardEventListener(new PaymentCardView.OnPaymentCardEventListener() {
            @Override
            public void onCardDetailsSubmit(String month, String year, String cardNumber, String cvv)
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("month", month);
                editor.putString("year", year);
                editor.putString("cardNumber", cardNumber);
                editor.putString("cvv", cvv);
                editor.apply();

                Log.i("profile", month+year+cardNumber+cvv);
            }
            @Override
            public void onError(String error) { }
            @Override
            public void onCancelClick() { }
        });
        
        RelativeLayout logOut = rootView.findViewById(R.id.id_customerLogOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
            }
        });
        return rootView;
        
    }
}
