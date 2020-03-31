package com.ublavins.emotion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    private MaterialButton resetPassButton;
    private TextInputEditText emailReset;
    private TextInputLayout emailResetLayout;
    private FirebaseAuth mAuth;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    public static ResetPasswordFragment newInstance() {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        resetPassButton = view.findViewById(R.id.resetPassButton);
        emailReset = view.findViewById(R.id.emailReset);
        emailResetLayout = view.findViewById(R.id.emailInputLayout);

        resetPassButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (validateEmail()) {
                            mAuth.sendPasswordResetEmail(emailReset.getText().toString())
                                    .addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(),
                                                        "Password reset email sent",
                                                        Toast.LENGTH_SHORT).show();
                                                getFragmentManager().popBackStack();
                                            } else {
                                                Toast.makeText(getContext(),
                                                        "Email is not registered",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                            );
                        }
                    }
                }
        );

        return view;
    }

    private boolean validateEmail() {
        boolean isValid = true;
        String emailText = emailReset.getText().toString();

        if (emailText.isEmpty()) {
            emailResetLayout.setError("Please enter an email address");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailResetLayout.setError("Please enter a valid email address");
            isValid = false;
        } else {
            emailResetLayout.setError(null);
        }

        return isValid;
    }

}
