package com.example.english_project.teacher;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.english_project.R;


public class TeacherStudentManagement extends Fragment {

    private Spinner spGrade;
    private Spinner spMyclass;
    private TextView textViewGrade, textViewClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_teacher_student_management, container, false);

        spGrade = (Spinner) view.findViewById(R.id.spGrade);
        spMyclass = (Spinner) view.findViewById(R.id.spMyclass);
        textViewGrade = (TextView) view.findViewById(R.id.textViewGrade);
        textViewClass = (TextView) view.findViewById(R.id.textViewClass);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.grade, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spGrade.setAdapter(adapter);

        spGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                textViewGrade.setText(item);
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                Log.d("grade", item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("class", "on nothing selected");
            }
        });


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.myclass, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spMyclass.setAdapter(adapter2);

        spMyclass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                textViewClass.setText(item);
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                Log.d("class", item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("class", "on nothing selected");
            }
        });
        return view;
    }
}