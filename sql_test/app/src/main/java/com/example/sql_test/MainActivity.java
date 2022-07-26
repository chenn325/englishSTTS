package com.example.sql_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private fra1 f1 = new fra1();
    private fra2 f2 = new fra2();
    private fra3 f3 = new fra3();
    private TabLayout tl;
    int now = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.fraContain, f1, "f1");
        ft.add(R.id.fraContain, f2, "f2");
        ft.add(R.id.fraContain, f3, "f3");

        ft.hide(f2);
        ft.hide(f3);
        ft.commit();

        tl = findViewById(R.id.tablayout);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fraChange(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void fraChange(int position){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch(now){
            case 0:
                ft.hide(f1);
                break;
            case 1:
                ft.hide(f2);
                break;
            case 2:
                ft.hide(f3);
        }
        switch(position){
            case 0:
                ft.show(f1);
                break;
            case 1:
                ft.show(f2);
                break;
            case 2:
                ft.show(f3);
        }
        ft.commit();
        now = position;
    }
}