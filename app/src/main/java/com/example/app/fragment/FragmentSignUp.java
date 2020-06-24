package com.example.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app.Activities.Login;
import com.example.app.Activities.MainActivity;
import com.example.app.Activities.SignUpActivity;
import com.example.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentSignUp extends Fragment implements View.OnClickListener {

    private TextInputLayout txtEmail, txtPassword;
    private Button btnLogin_register;
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

        txtEmail = (TextInputLayout) view.findViewById(R.id.username);
        txtPassword = (TextInputLayout) view.findViewById(R.id.password);
        btnLogin_register = (Button) view.findViewById(R.id.login_register);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        firebaseAuth = FirebaseAuth.getInstance();

        System.out.println("Entered Signup fragement");

        btnLogin_register.setOnClickListener(this);

        return view;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        System.out.println("Button clicked");

        switch(v.getId()){

            case R.id.login_register:
                    Log.i("FragmentSignUp", "inside it");
                    String email = txtEmail.getEditText().getText().toString().trim();
                    String password = txtPassword.getEditText().getText().toString().trim();

                    System.out.println("Email : " + email);
                    System.out.println("Password : " + password);

                    if(TextUtils.isEmpty(email)){
                        Toast.makeText(getActivity(),"Please Enter Email", Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(password)){
                        Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                    }
                    else if(password.length() < 6){
                        Toast.makeText(getActivity(), "Password is too short", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressBar.setVisibility(View.VISIBLE);

                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(getActivity(), Login.class));
                                            Toast.makeText(getActivity(), "Sign Up Successful", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    break;

            default: break;
        }

    }
}