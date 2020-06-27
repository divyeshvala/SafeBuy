package com.example.app.fragment;

import android.content.Intent;
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

import java.util.Objects;

public class FragmentLogin extends Fragment implements View.OnClickListener{

    private TextInputLayout txtEmail, txtPassword;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    public static FragmentLogin newInstance() {
        return new FragmentLogin();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        txtEmail = view.findViewById(R.id.username);
        txtPassword = view.findViewById(R.id.password);
        Button loginBTN = view.findViewById(R.id.login_register);
        progressBar = view.findViewById(R.id.loading);
        firebaseAuth = FirebaseAuth.getInstance();

        loginBTN.setOnClickListener(this);

        return view;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.login_register) {
            Log.i("FragmentLogin", "inside it");

            String email = txtEmail.getEditText().getText().toString().trim();
            String password = txtPassword.getEditText().getText().toString().trim();

            System.out.println("Email : " + email);
            System.out.println("Password : " + password);

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getActivity(), "Please Enter Email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    Objects.requireNonNull(getActivity()).finish();

                                } else {
                                    Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }

    }
}