package com.ublavins.emotion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private AuthCallback callback;
    private EditText fname;
    private EditText lname;
    private EditText email;
    private EditText password;
    private Button signUp;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (AuthCallback)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        fname = (EditText)view.findViewById(R.id.fnameRegister);
        lname = (EditText)view.findViewById(R.id.lnameRegister);
        email = (EditText)view.findViewById(R.id.emailRegister);
        password = (EditText)view.findViewById(R.id.passwordRegister);
        signUp = (Button)view.findViewById(R.id.signUpButton);

        return view;
    }

    public void register() {
        if (validateRegister()) {

        }
    }

    public boolean validateRegister() {
        boolean isValid = true;
        String fnameText = fname.getText().toString();
        String lnameText = lname.getText().toString();
        String emailText = email.getText().toString();
        String passText = password.getText().toString();
        return isValid;
    }

    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
