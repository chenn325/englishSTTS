package com.example.english_project.teacher;

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

public class TeacherMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TeacherHome teacherHome = new TeacherHome();
    TeacherTextbook teacherTextbook = new TeacherTextbook();
    TeacherStudentManagement teacherStudentManagement = new TeacherStudentManagement();
    TeacherProfile teacherProfile = new TeacherProfile();

    Fragment currentFragment = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    changeFragment(teacherHome);
                    return true;
                case R.id.textbook:
                    changeFragment(teacherTextbook);
                    return true;
                case R.id.student:
                    changeFragment(teacherStudentManagement);
                    return true;
                case R.id.personal:
                    changeFragment(teacherProfile);
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
        setContentView(R.layout.activity_teacher_main);
        bottomNavigationView = findViewById(R.id.teacher_bottom_navigation);

        getSupportFragmentManager().beginTransaction().add(R.id.container, teacherHome).commit();
        currentFragment = teacherHome;
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



}
