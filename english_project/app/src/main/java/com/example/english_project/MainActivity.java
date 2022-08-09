package com.example.english_project;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Menu_Home menu_home = new Menu_Home();
    Menu_Study menu_study = new Menu_Study();
    Menu_Test menu_test = new Menu_Test();
    Menu_Personal menu_personal = new Menu_Personal();
    Choose_Partner choose_partner = new Choose_Partner();
    Fragment currentFragment = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    changeFragment(menu_home);
                    return true;
                case R.id.study:
                    changeFragment(menu_study);
                    return true;
                case R.id.test:
                    changeFragment(menu_test);
                    return true;
                case R.id.personal:
                    changeFragment(menu_personal);
                    return true;
            }
            return false;
        }
    };

    protected void changeFragment(Fragment showFragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (!showFragment.isAdded()) {
            ft.add(R.id.container, showFragment);
        }
        if (currentFragment.isAdded()) {
            ft.hide(currentFragment);
        }
        ft.show(showFragment);
        currentFragment = showFragment;
        Log.d("showFragment", String.valueOf(showFragment));
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().add(R.id.container, menu_home).commit();
        currentFragment = menu_home;
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



}