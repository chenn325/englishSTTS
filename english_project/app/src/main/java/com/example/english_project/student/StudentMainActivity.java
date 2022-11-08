package com.example.english_project.student;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.english_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudentMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    StudentHome student_home = new StudentHome();
    StudentStudy student_study = new StudentStudy();
    StudentTest student_test = new StudentTest();
    StudentProfile studentProfile = new StudentProfile();
    StudentChoosePartner studentChoose_partner = new StudentChoosePartner();
    Fragment currentFragment = student_home;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    changeFragment(student_home);
                    return true;
                case R.id.study:
                    changeFragment(student_study);
                    return true;
                case R.id.test:
                    changeFragment(student_test);
                    return true;
                case R.id.personal:
                    changeFragment(studentProfile);
                    return true;
            }
            return false;
        }
    };

    public void changeFragment(Fragment showFragment) {
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

        getSupportFragmentManager().beginTransaction().add(R.id.container, student_home).commit();
        currentFragment = student_home;
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



}