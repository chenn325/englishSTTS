package com.example.english_project.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.english_project.R;
import com.example.english_project.student.StudentMainActivity;
import com.example.english_project.study.ListenLearning;
import com.example.english_project.study.SpeakLearning;

public class StudentStudy extends Fragment {

    Button listen_but, speak_but;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_student_study,container,false);

        Button listen_but = (Button) view.findViewById(R.id.listen_but);
        Button speak_but = (Button) view.findViewById(R.id.speak_but);

        listen_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                studentMainActivity.changeFragment(new ListenLearning());
            }
        });

        speak_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                studentMainActivity.changeFragment(new SpeakLearning());
            }
        });
        return view;
    }

}
