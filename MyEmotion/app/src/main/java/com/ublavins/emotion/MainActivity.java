package com.ublavins.emotion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements MainCallback {

    private BottomNavigationView bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavBar = (BottomNavigationView)findViewById(R.id.mainNavBar);

        bottomNavBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                mainFragment();
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
    public void mainFragment() {
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
        ProfileFragment profileFrag = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentFrame, profileFrag)
                .addToBackStack(null).commit();
    }
}
