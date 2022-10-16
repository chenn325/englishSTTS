package com.example.english_project.student;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.english_project.R;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.User;

public class StudentHome extends Fragment {


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home,container,false);
        ImageView imagePartner = (ImageView) view.findViewById(R.id.ImagePartner);
        //getting the current user
        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        int partner = user.getPartner();

        switch (partner){
            case 1:
                imagePartner.setImageResource(R.drawable.girl1);
                break;
            case 2:
                imagePartner.setImageResource(R.drawable.girl2);
                break;
            case 3:
                imagePartner.setImageResource(R.drawable.boy3);
                break;
        }

        return view;
    }


}
