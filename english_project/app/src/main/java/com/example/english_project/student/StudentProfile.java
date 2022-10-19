package com.example.english_project.student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.english_project.LoginActivity;
import com.example.english_project.R;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.User;

public class StudentProfile extends Fragment {

    TextView textViewId, textViewUsername;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_student_profile,container,false);
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
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                startActivity(intent);
                Log.d("Profile", "logout");
            }
        });

        view.findViewById(R.id.buttonChoose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                studentMainActivity.changeFragment(new StudentChoosePartner());
            }
        });
        return view;
    }
}
