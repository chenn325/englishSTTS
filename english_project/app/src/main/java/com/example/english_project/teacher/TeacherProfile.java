package com.example.english_project.teacher;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.english_project.LoginActivity;
import com.example.english_project.R;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.User;

public class TeacherProfile extends Fragment {
    TextView textViewName;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_teacher_profile,container,false);


        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }


        textViewName = (TextView) view.findViewById(R.id.textViewName);

        //getting the current user
        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        textViewName.setText(user.getName());

        //when the user presses logout button
        //calling the logout method
        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).logout();
                //Intent intent = new Intent(getActivity(), LoginActivity.class);
                //startActivity(intent);
                Log.d("Profile", "logout");
            }
        });

        return view;
    }
}