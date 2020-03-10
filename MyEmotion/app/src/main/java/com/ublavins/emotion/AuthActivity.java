package com.ublavins.emotion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity implements AuthCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
        } else {
            loginFragment();
        }
    }

    @Override
    public void loginFragment() {
        LoginFragment loginFrag = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, loginFrag).commit();
    }

    @Override
    public void registerFragment() {
        RegisterFragment registerFrag = new RegisterFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, registerFrag)
                .addToBackStack(null).commit();
    }

    @Override
    public void resetPassFragement() {

    }
}
