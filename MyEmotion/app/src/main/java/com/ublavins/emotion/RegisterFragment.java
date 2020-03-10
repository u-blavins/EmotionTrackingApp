package com.ublavins.emotion;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiper.MaterialSpinner;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private AuthCallback callback;
    private TextInputLayout fnameInput, lnameInput, emailInput, passwordInput,
        dateInput;
    private TextInputEditText fname, lname, email, password, date;
    private MaterialButton signUp;
    private CheckBox tos;
    private static final String[] GENDERS = {"Male", "Female", "Other"};
    private MaterialSpinner gender;
    private FirebaseAuth mAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (AuthCallback)getActivity();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        fnameInput = (TextInputLayout)view.findViewById(R.id.fnameInputLayout);
        lnameInput = (TextInputLayout)view.findViewById(R.id.lnameInputLayout);
        emailInput = (TextInputLayout)view.findViewById(R.id.emailInputLayout);
        passwordInput = (TextInputLayout)view.findViewById(R.id.passwordInputLayout);
        dateInput = (TextInputLayout)view.findViewById(R.id.dobInputLayout);
        tos = (CheckBox)view.findViewById(R.id.tosCheck);
        fname = (TextInputEditText)view.findViewById(R.id.fnameRegister);
        lname = (TextInputEditText)view.findViewById(R.id.lnameRegister);
        email = (TextInputEditText)view.findViewById(R.id.emailRegister);
        password = (TextInputEditText)view.findViewById(R.id.passwordRegister);
        date = (TextInputEditText)view.findViewById(R.id.dobRegister);
        gender = (MaterialSpinner)view.findViewById(R.id.genderRegister);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, GENDERS);
        gender.setAdapter(adapter);
        signUp = (MaterialButton)view.findViewById(R.id.signUpButton);


        date.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getBirthDate();
                    }
                }
        );

        signUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        register();
                    }
                }
        );

        return view;
    }

    public void register() {
        if (validateRegister()) {
            String emailText = email.getText().toString();
            String passText = password.getText().toString();
            final Map<String, Object> user = new HashMap<>();
            user.put("FirstName", fname.getText().toString());
            user.put("LastName", lname.getText().toString());
            user.put("Email", emailText);
            user.put("Dob", date.getText().toString());
            user.put("Gender", gender.getSelectedItem().toString());
            mAuth.createUserWithEmailAndPassword(user.get("Email").toString(), passText)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            makeToast("Error creating account");
                                        }

                                    }
                                });
                            } else {
                                makeToast("Account already exists");
                            }
                        }
                    });
        }
    }

    private void getBirthDate() {
        final Calendar calendar = Calendar.getInstance();
        final int birthYear = calendar.get(Calendar.YEAR);
        final int birthMonth = calendar.get(Calendar.MONTH);
        final int birthDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, birthYear, birthMonth, birthDay);
        datePicker.show();
    }

    private boolean validateRegister() {
        boolean isValid = true;
        String fnameText = fname.getText().toString();
        String lnameText = lname.getText().toString();
        String emailText = email.getText().toString();
        String passText = password.getText().toString();
        String dateText = date.getText().toString();
        String genderText;

        if (fnameText.isEmpty()) {
            fnameInput.setError("Field must not be empty");
            isValid = false;
        } else {
            fnameInput.setError(null);
        }

        if (lnameText.isEmpty()) {
            lnameInput.setError("Field must not be empty");
            isValid = false;
        } else {
            lnameInput.setError(null);
        }

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

        if (dateText.isEmpty()) {
            dateInput.setError("Enter a password");
            isValid = false;
        } else {
            dateInput.setError(null);
        }

        try {
            genderText = gender.getSelectedItem().toString();
            gender.setError(null);
        } catch (NullPointerException ex) {
            gender.setError("Select a gender");
        }

        if (!tos.isChecked()) {
            isValid = false;
            makeToast("Check terms of services");
        }

        return isValid;
    }

    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
