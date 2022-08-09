package com.example.english_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.User;

public class Menu_Personal extends Fragment {

    TextView textViewId, textViewUsername;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view=inflater.inflate(R.layout.show_personal,container,false);
        Log.d("Profile", "onCreate");

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }


        textViewId = (TextView) view.findViewById(R.id.textViewId);
        textViewUsername = (TextView) view.findViewById(R.id.textViewUsername);


        //getting the current user
        User user = SharedPrefManager.getInstance(getActivity()).getUser();

        //setting the values to the textviews
        textViewId.setText(String.valueOf(user.getId()));
        textViewUsername.setText(user.getUsername());

        //when the user presses logout button
        //calling the logout method
        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).logout();
                Log.d("Profile", "logout");
            }
        });

        view.findViewById(R.id.buttonChoose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.changeFragment(new Choose_Partner());
            }
        });
        return view;
    }
}
