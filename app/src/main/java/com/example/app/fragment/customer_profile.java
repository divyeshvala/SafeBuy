package com.example.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.app.Activities.Login;
import com.example.app.R;
import com.example.app.Utilities.EncryptionDecryption;
import com.google.firebase.auth.FirebaseAuth;
import com.manojbhadane.PaymentCardView;
import static android.content.Context.MODE_PRIVATE;

public class customer_profile extends Fragment
{
    ImageView im;
    customer_profile mContext;
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_profile, container, false);

        final LinearLayout child = rootView.findViewById(R.id.parent);

        im = rootView.findViewById(R.id.transactions);
        im.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                FragmentManager fx = getActivity().getSupportFragmentManager();
//                FragmentManager fx = getFragmentManager();
                fx.beginTransaction().replace(R.id.holder,new fragment_transactions()).addToBackStack(null).commit();

            }
        });

        final PaymentCardView paymentCardView = rootView.findViewById(R.id.creditCard);
        final SharedPreferences settings = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);

        paymentCardView.setCardTitle("PAN");
        paymentCardView.setOnPaymentCardEventListener(new PaymentCardView.OnPaymentCardEventListener() {
            @Override
            public void onCardDetailsSubmit(String month, String year, String cardNumber, String cvv)
            {
                //EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
                cardNumber = cardNumber.replace(" ", "");
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("month", month);
                editor.putString("year", year);
                editor.putString("cardNumber", cardNumber);
                editor.putString("cvv", cvv);
                editor.apply();
                child.removeAllViews();
                TextView tv = new TextView(getActivity());
                tv.setText("Your card is added");
                child.addView(tv);
                Log.i("customer_profile", month+year+cardNumber+cvv);

                Log.i("customer_profile", settings.getString("cardNumber", "-1"));
            }

            @Override
            public void onError(String error) { }
            @Override
            public void onCancelClick() { }
        });
        sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String senderPAN = sharedPreferences.getString("cardNumber", "-1");
        senderPAN = senderPAN.replace(" ", "");

        if(!senderPAN.equals("-1"))
        {
            child.removeAllViews();
            TextView tv = new TextView(getActivity());
            tv.setText("Your card is added");
            child.addView(tv);
        }

        RelativeLayout logOut = rootView.findViewById(R.id.id_customerLogOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finish();

                sharedPreferences.edit().clear().commit();
            }
        });
        return rootView;
    }

}
