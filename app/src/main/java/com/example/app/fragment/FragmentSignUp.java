package com.example.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.app.Activities.MainActivity;
import com.example.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class FragmentSignUp extends Fragment implements View.OnClickListener {

    private TextInputLayout txtEmail, txtPassword, tvFirstName, tvLastName;

    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    public static FragmentSignUp newInstance()
    {
        return new FragmentSignUp();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        txtEmail = view.findViewById(R.id.username);
        txtPassword = view.findViewById(R.id.password);
        tvFirstName = view.findViewById(R.id.firstName);
        tvLastName = view.findViewById(R.id.lastName);
        Button registerBTN = view.findViewById(R.id.login_register);
        progressBar = view.findViewById(R.id.loading);
        firebaseAuth = FirebaseAuth.getInstance();

        registerBTN.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.login_register) {

            String email = txtEmail.getEditText().getText().toString().trim();
            String password = txtPassword.getEditText().getText().toString().trim();
            final String firstName = tvFirstName.getEditText().getText().toString().trim();
            final String lastName = tvLastName.getEditText().getText().toString().trim();

            if(TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)){
                Toast.makeText(getActivity(), "Please your name", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(email)) {
                Toast.makeText(getActivity(), "Please enter your mail", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(getActivity(), "Please enter your password", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(getActivity(), "Password is too short", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful())
                                {
                                    Log.i("SignUp", "task successfull");
                                    final SharedPreferences settings = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);

                                    final DatabaseReference newRequestDB = FirebaseDatabase.getInstance()
                                            .getReference().child(settings.getString("userType", "customer")+"s").child(FirebaseAuth.getInstance().getUid());

                                    Map<String, Object> messageData = new HashMap<>();
                                    messageData.put("firstName", firstName);
                                    messageData.put("lastName", lastName);
                                    newRequestDB.updateChildren(messageData);
                                    Toast.makeText(getActivity(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    Objects.requireNonNull(getActivity()).finish();
                                } else {
                                    Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }

    }
}