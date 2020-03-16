package com.ublavins.emotion;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private MaterialButton logout;
    private TextInputEditText fname, lname, email, dob, gender;
    private FirebaseAuth mAuth;
    private DocumentSnapshot docSnap;

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
        logout = view.findViewById(R.id.logoutButton);

        fname.setText(docSnap.get("FirstName").toString());
        lname.setText(docSnap.get("LastName").toString());
        email.setText(docSnap.get("Email").toString());
        dob.setText(docSnap.get("Dob").toString());
        gender.setText(docSnap.get("Gender").toString());

        logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout();
                    }
                }
        );

        return view;
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }

}
