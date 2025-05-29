package com.example.gromoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.gromoapp.fragments.ForYouFragment;
import com.example.gromoapp.fragments.GroMoFragment;
import com.example.gromoapp.fragments.LeadsFragment;
import com.example.gromoapp.fragments.LearnFragment;
import com.example.gromoapp.fragments.SellFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        bottomNavigationView=findViewById(R.id.bottomNavigation);
        loadFragment(new GroMoFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.nav_gromo:
                    fragment = new GroMoFragment();
                    break;
                case R.id.nav_leads:
                    fragment = new LeadsFragment();
                    break;
                case R.id.nav_sell:
                    fragment = new SellFragment();
                    break;
                case R.id.nav_for_you:
                    fragment = new ForYouFragment();
                    break;
                case R.id.nav_learn:
                    fragment = new LearnFragment();
                    break;
            }
            return loadFragment(fragment);
        });
    }
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}