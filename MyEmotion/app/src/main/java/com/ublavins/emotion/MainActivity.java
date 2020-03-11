package com.ublavins.emotion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements MainCallback {

    private BottomNavigationView bottomNavBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private DocumentSnapshot snap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavBar = (BottomNavigationView)findViewById(R.id.mainNavBar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("Users").document(mUser.getUid());
        docRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot docSnap = task.getResult();
                            if (docSnap.exists()) {
                                snap = docSnap;
                            }
                        }
                    }
                }
        );

        homeFragment();

        bottomNavBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                homeFragment();
                                break;
                            case R.id.nav_charts:
                                chartFragment();
                                break;
                            case R.id.nav_profile:
                                profileFragment();
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public void homeFragment() {
        HomeFragment homeFrag = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentFrame, homeFrag)
                .addToBackStack(null).commit();
    }

    @Override
    public void chartFragment() {
        ChartFragment chartFrag = new ChartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentFrame, chartFrag)
                .addToBackStack(null).commit();
    }

    @Override
    public void profileFragment() {
        ProfileFragment profileFrag = new ProfileFragment(snap);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentFrame, profileFrag)
                .addToBackStack(null).commit();
    }

}
