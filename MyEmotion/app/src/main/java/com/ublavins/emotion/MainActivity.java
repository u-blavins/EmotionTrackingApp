package com.ublavins.emotion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity implements MainCallback {

    private BottomNavigationView bottomNavBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private DocumentSnapshot snap;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutButtonMain:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavBar = findViewById(R.id.mainNavBar);

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

        docRef.addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            snap = documentSnapshot;
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
                            case R.id.nav_add_entry:
                                addEntryFragment();
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
    public void addEntryFragment() {
        AddEntryFragment addEntryFragment = new AddEntryFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentFrame,
                addEntryFragment).addToBackStack(null).commit();
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
