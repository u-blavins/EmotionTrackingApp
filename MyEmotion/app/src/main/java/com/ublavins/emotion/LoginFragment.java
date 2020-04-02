package com.ublavins.emotion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private MaterialButton forgotPassButton;
    private TextInputLayout emailInput;
    private TextInputLayout passwordInput;
    private AuthCallback callback;
    private FirebaseAuth mAuth;
    private TextInputEditText email, password;
    private ProgressBar progressBar;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (AuthCallback) getActivity();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        progressBar = view.findViewById(R.id.homeProgress);
        email = view.findViewById(R.id.emailLogin);
        password = view.findViewById(R.id.passwordLogin);
        emailInput = view.findViewById(R.id.emailInputLayout);
        passwordInput = view.findViewById(R.id.passwordInputLayout);
        loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick (View v) {
                        login(); }
                    }
                );
        registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick (View v) {
                        callback.registerFragment(); }
                }
        );
        forgotPassButton = view.findViewById(R.id.forgetPassButton);
        forgotPassButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.resetPassFragment();
                    }
                }
        );
        return view;
    }

    public void login() {
        if (validateLogin()) {
            progressBar.setVisibility(View.VISIBLE);
            String emailText = email.getText().toString();
            String passText = password.getText().toString();
            mAuth.signInWithEmailAndPassword(emailText, passText)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.invalidate();
                                progressBar.setVisibility(View.INVISIBLE);
                                // makeToast("User exists");
                                Intent intent =  new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                makeToast("User does not exist");
                            }
                        }
                    });
        }
    }

    private boolean validateLogin() {
        boolean isValid = true;
        String emailText = email.getText().toString();
        String passText = password.getText().toString();


        if (emailText.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailInput.setError("Enter a valid email address");
            isValid = false;
        } else {
            emailInput.setError(null);
        }

        if (passText.isEmpty()) {
            passwordInput.setError("Enter a password");
            isValid = false;
        } else {
            passwordInput.setError(null);
        }
        return isValid;
    }

    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
