package com.example.app.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.app.Activities.Login;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.manojbhadane.PaymentCardView;

import static android.content.Context.MODE_PRIVATE;

public class fragment_merchant_profile extends Fragment {

    private String m_Text = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_merchant_profile, container, false);
        Button bt = rootView.findViewById(R.id.validate);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Your Id");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Validate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        PaymentCardView paymentCardView = rootView.findViewById(R.id.creditCard);
        final SharedPreferences settings = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);

        paymentCardView.setCardTitle("PAN Card");
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

        RelativeLayout logOut = rootView.findViewById(R.id.id_merchantLogOut);
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
