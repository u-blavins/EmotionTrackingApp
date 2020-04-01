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
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private MaterialButton editProfile, saveProfile, deleteAccount;
    private TextInputEditText fname, lname, email, dob, gender;
    private TextInputLayout fnameInput, lnameInput, emailInput, dobInput;
    private FirebaseAuth mAuth;
    private DocumentSnapshot docSnap;
    private String fnameStr, lnameStr, emailStr, dobStr, genderStr;

    public ProfileFragment(DocumentSnapshot snap) {
        // Required empty public constructor
        docSnap = snap;
    }

    public static ProfileFragment newInstance(DocumentSnapshot snap) {
        ProfileFragment fragment = new ProfileFragment(snap);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        fnameStr = docSnap.get("FirstName").toString();
        lnameStr = docSnap.get("LastName").toString();
        emailStr = docSnap.get("Email").toString();
        dobStr = docSnap.get("Dob").toString();
        genderStr = docSnap.get("Gender").toString();
    }

    // Will need to think of caching firebase store
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fname = view.findViewById(R.id.fnameText);
        lname = view.findViewById(R.id.lnameText);
        email = view.findViewById(R.id.emailText);
        dob = view.findViewById(R.id.dobText);
        gender = view.findViewById(R.id.genderText);
        fnameInput = view.findViewById(R.id.fnameInputLayout);
        lnameInput = view.findViewById(R.id.lnameInputLayout);
        emailInput = view.findViewById(R.id.emailInputLayout);
        dobInput = view.findViewById(R.id.dobInputLayout);
        editProfile = view.findViewById(R.id.editButton);
        saveProfile = view.findViewById(R.id.saveButton);
        deleteAccount = view.findViewById(R.id.deleteButton);
        fname.setText(fnameStr);
        lname.setText(lnameStr);
        email.setText(emailStr);
        dob.setText(dobStr);
        gender.setText(genderStr);

        deleteAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                        builder.setTitle("Delete account?");
                        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String uuid = mAuth.getUid();
                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(mAuth.getUid()).delete();
                                FirebaseFirestore.getInstance().collection("Entries")
                                        .document(mAuth.getUid()).delete();
                                mAuth.getCurrentUser().delete();
                                mAuth.signOut();
                                Intent intent = new Intent(getActivity(), AuthActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
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

        dob.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dob.isFocusable()){
                            getBirthDate();
                        }
                    }
                }
        );

        editProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editProfile.getVisibility() == View.VISIBLE) {
                            editProfile.setVisibility(View.INVISIBLE);
                            saveProfile.setVisibility(View.VISIBLE);
                            fname.setFocusable(true);
                            fname.setFocusableInTouchMode(true);
                            lname.setFocusable(true);
                            lname.setFocusableInTouchMode(true);
                            email.setFocusable(true);
                            email.setFocusableInTouchMode(true);
                            dob.setFocusableInTouchMode(true);
                            gender.setFocusable(true);
                            gender.setFocusableInTouchMode(true);
                        }
                    }
                }
        );

        saveProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (saveProfile.getVisibility() == View.VISIBLE) {
                            if (validateUpdate()) {
                                if (!email.getText().toString().equals(mAuth.getCurrentUser().getEmail())) {
                                    Toast.makeText(getContext(), "TEST", Toast.LENGTH_SHORT);
                                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(email.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Updated Profile", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(mAuth.getUid()).update(
                                                "FirstName", fname.getText().toString(),
                                        "LastName", lname.getText().toString(),
                                        "Email", email.getText().toString(),
                                        "Dob", dob.getText().toString()
                                );

                                fname.setFocusable(false);
                                fname.setFocusableInTouchMode(false);
                                lname.setFocusable(false);
                                lname.setFocusableInTouchMode(false);
                                email.setFocusable(false);
                                email.setFocusableInTouchMode(false);
                                dob.setFocusable(false);
                                dob.setFocusableInTouchMode(false);
                                gender.setFocusable(false);
                                gender.setFocusableInTouchMode(false);
                                saveProfile.setVisibility(View.INVISIBLE);
                                editProfile.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
        );

        return view;
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
                        dob.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, birthYear, birthMonth, birthDay);
        datePicker.show();
    }

    private boolean validateUpdate() {
        boolean isValid = true;
        String fnameText = fname.getText().toString();
        String lnameText = lname.getText().toString();
        String emailText = email.getText().toString();
        String dateText = dob.getText().toString();

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

        if (dateText.isEmpty()) {
            dobInput.setError("Enter a date");
            isValid = false;
        } else {
            dobInput.setError(null);
        }
        return isValid;
    }

}
