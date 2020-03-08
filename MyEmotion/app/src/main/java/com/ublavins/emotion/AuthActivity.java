package com.ublavins.emotion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class AuthActivity extends AppCompatActivity implements AuthCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        loginFragment();
    }

    @Override
    public void loginFragment() {
        LoginFragment loginFrag = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, loginFrag).commit();
    }

    @Override
    public void registerFragment() {
        RegisterFragment registerFrag = new RegisterFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, registerFrag).addToBackStack(null).commit();
    }

    @Override
    public void resetPassFragement() {

    }
}
