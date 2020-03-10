package com.ublavins.emotion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private MaterialButton logout;
    private TextInputLayout fnameLayout;
    private TextInputEditText fname, lname, email, dob, gender;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(DocumentSnapshot snap) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    // Will need to think of caching firebase store
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fname = (TextInputEditText)view.findViewById(R.id.fnameText);
        lname = (TextInputEditText)view.findViewById(R.id.lnameText);
        email = (TextInputEditText)view.findViewById(R.id.emailText);
        dob = (TextInputEditText)view.findViewById(R.id.dobText);
        gender = (TextInputEditText)view.findViewById(R.id.genderText);
        logout = (MaterialButton)view.findViewById(R.id.logoutButton);

        final DocumentReference docRef = db.collection("Users").document(mUser.getUid());
        docRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot docSnap = task.getResult();
                            if (docSnap.exists()) {
                                fname.setText(docSnap.get("FirstName").toString());
                                lname.setText(docSnap.get("LastName").toString());
                                email.setText(docSnap.get("Email").toString());
                                dob.setText(docSnap.get("Dob").toString());
                                gender.setText(docSnap.get("Gender").toString());
                            }
                        }
                    }
                }
        );

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
