package com.example.gomind.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.fragments.GreetingFragment;
import com.example.gomind.fragments.LoginFragment;
import com.example.gomind.fragments.RegisterFragment;

public class AuthenticationActivity extends AppCompatActivity {

    Fragment fragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        fragmentManager = getSupportFragmentManager();

        // Проверяем, вошел ли пользователь
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            // Если вошел, сразу переходим в MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();  // Закрываем текущую активность, чтобы вернуться в нее нельзя было
        } else {
            // Если не вошел, показываем экран приветствия
            fragment = new GreetingFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.authentication_container, fragment)
                    .commit();
        }
    }
    /////

    public void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.authentication_container, fragment)
                .commit();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.login_button) {
            replaceFragment(new LoginFragment());
        } else if (id == R.id.enter_btn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.register_button) {
            replaceFragment(new RegisterFragment());
        }
    }
}