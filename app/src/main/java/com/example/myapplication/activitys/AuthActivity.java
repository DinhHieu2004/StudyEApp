package com.example.myapplication.activitys;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;

public class AuthActivity extends AppCompatActivity {
    private TextView loginTab, signupTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loginTab = findViewById(R.id.loginTab);
        signupTab = findViewById(R.id.signupTab);

        loadFragment(new LoginActivity());
        setTabSelected(loginTab, signupTab);

        loginTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new LoginActivity());
                setTabSelected(loginTab, signupTab);
            }
        });

        signupTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SignUpActivity());
                setTabSelected(signupTab, loginTab);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
    }

    private void setTabSelected(TextView selectedTab, TextView unselectedTab) {    selectedTab.setBackgroundResource(R.drawable.tab_selected);
        selectedTab.setTypeface(null, Typeface.BOLD);
        selectedTab.setTextColor(Color.parseColor("#FFFFFF"));

        unselectedTab.setBackgroundResource(R.drawable.tab_unselected);
        unselectedTab.setTypeface(null, Typeface.NORMAL);
        unselectedTab.setTextColor(Color.parseColor("#80FFFFFF"));
    }

}
