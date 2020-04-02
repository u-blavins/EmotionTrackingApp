package com.ublavins.emotion;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
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
    private TextView tosText;
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
        fnameInput = view.findViewById(R.id.fnameInputLayout);
        lnameInput = view.findViewById(R.id.lnameInputLayout);
        emailInput = view.findViewById(R.id.emailInputLayout);
        passwordInput = view.findViewById(R.id.passwordInputLayout);
        dateInput = view.findViewById(R.id.dobInputLayout);
        tos = view.findViewById(R.id.tosCheck);
        tosText = view.findViewById(R.id.tosText);
        fname = view.findViewById(R.id.fnameRegister);
        lname = view.findViewById(R.id.lnameRegister);
        email = view.findViewById(R.id.emailRegister);
        password = view.findViewById(R.id.passwordRegister);
        date = view.findViewById(R.id.dobRegister);
        gender = view.findViewById(R.id.genderRegister);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, GENDERS);
        gender.setAdapter(adapter);
        signUp = view.findViewById(R.id.signUpButton);

        tosText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                        builder.setTitle("Agree to terms and conditions");
                        builder.setMessage("By clicking agree, I hereby accept the storage of personal data and possible use of data for research purposes.");
                        builder.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tos.setChecked(true);
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();
                    }
                }
        );

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
        String genderText = "";

        try {
            genderText = gender.getSelectedItem().toString();
        } catch (NullPointerException ex) {}

        ValidateRegistration validate = new ValidateRegistration(
                fnameText, lnameText, emailText, passText, dateText, genderText
        );

        if (!validate.validateFirstName().getCheck()) {
            fnameInput.setError(validate.validateFirstName().getMessage());
            isValid = validate.validateFirstName().getCheck();
        } else {
            fnameInput.setError(null);
        }

        if (!validate.validateLastName().getCheck()) {
            lnameInput.setError(validate.validateLastName().getMessage());
            isValid = validate.validateLastName().getCheck();
        } else {
            lnameInput.setError(null);
        }

        if (!validate.validateEmail().getCheck()) {
            emailInput.setError(validate.validateEmail().getMessage());
            isValid = validate.validateEmail().getCheck();
        } else {
            emailInput.setError(null);
        }

        if (!validate.validatePassword().getCheck()) {
            passwordInput.setError(validate.validatePassword().getMessage());
            isValid = validate.validatePassword().getCheck();
        } else {
            passwordInput.setError(null);
        }

        if (!validate.validateGender().getCheck()) {
            gender.setError(validate.validateGender().getMessage());
            isValid = validate.validateGender().getCheck();
        } else {
            gender.setError(null);
        }

        if (dateText.isEmpty()) {
            dateInput.setError("Enter a date");
            isValid = false;
        } else {
            dateInput.setError(null);
        }



        if (!tos.isChecked()) {
            isValid = false;
            makeToast("Check terms and conditions");
        }

        return isValid;
    }

    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
