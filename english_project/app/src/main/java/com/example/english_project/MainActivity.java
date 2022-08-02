package com.example.english_project;



import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

        private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:  //我上一篇的menu裡面設的id
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Menu_Home()).commit();  //切換fragment
                        return true;
                    case R.id.study:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Menu_Study()).commit();
                        return true;
                    case R.id.test:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Menu_Test()).commit();
                        return true;
                    case R.id.personal:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Menu_Personal()).commit();
                        return true;
                }
                return false;
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            setMain();

            BottomNavigationView navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }


        private void setMain() {  //這個副程式用來設置顯示剛進來的第一個主畫面

            this.getSupportFragmentManager().beginTransaction().add(R.id.frameLayout,new Menu_Home()).commit();
        }

}