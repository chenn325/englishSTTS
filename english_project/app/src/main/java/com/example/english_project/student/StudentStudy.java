package com.example.english_project.student;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;
import com.example.english_project.student.StudentMainActivity;
import com.example.english_project.study.ListenLearning;
import com.example.english_project.study.SpeakLearning;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class StudentStudy extends Fragment {

    private int rowNum, myclass;
    ProgressBar progressBar;
    TableLayout showSchedule;

    JSONObject obj;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_student_study,container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        showSchedule = (TableLayout) view.findViewById(R.id.showSchedule);

        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        myclass = user.getMyclass();
        GetSchedule();

        return view;
    }


    private void GetSchedule(){

        class getSchedule extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try {
                    obj = new JSONObject(s);
                    Log.d("json", "get schedule");

                    if (!obj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        rowNum = obj.getInt("row");
                    }
                    else{
                        Log.d("obj", obj.getString("message"));
                        Toast.makeText(getActivity().getApplicationContext(), "Can't get schedule", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("schedule frag","schedule json error");
                }
                //set schedule
                try{
                    setSchedule(rowNum);
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d("schedule frag","schedule json error");
                };

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("myclass", String.valueOf(myclass));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_SETSCHEDULE, params);
            }
        }

        getSchedule ul = new getSchedule();
        ul.execute();
    }

    public void setSchedule(int rowNum) throws JSONException {
        Log.d("setSchedule","start");
        //listen
        TableRow tableRow = new TableRow(getContext());
        TextView tv = new TextView(getContext());
        tv.setText("Listen");
        tv.setTextSize(20);
        tableRow.addView(tv);
        showSchedule.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i=0; i<rowNum; i++){
            JSONObject t = obj.getJSONObject(String.valueOf(i));
            tableRow = new TableRow(getContext());
            tv = new TextView(getContext());
            Button but = new Button(getContext());
            but.setText("Unit" + t.getString("unit"));
            int u = Integer.parseInt(t.getString("unit"));
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ListenLearning listenLearning = new ListenLearning();
                    listenLearning.setInfo(myclass, u, "listen", "vocabulary");
                    StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                    studentMainActivity.changeFragment(listenLearning);
                }
            });
            //but.setBackground(this.getResources().getDrawable(R.color.third));
            tableRow.addView(but);
            tv.setText(t.getString("startYmd")+" - "+t.getString("endYmd"));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20);
            tableRow.addView(tv);
            showSchedule.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        //speak
        tableRow = new TableRow(getContext());
        tv = new TextView(getContext());
        tv.setText("Speak");
        tv.setTextSize(20);
        tableRow.addView(tv);
        showSchedule.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i=0; i<rowNum; i++){
            JSONObject t = obj.getJSONObject(String.valueOf(i));
            tableRow = new TableRow(getContext());
            tv = new TextView(getContext());
            Button but = new Button(getContext());
            but.setText("Unit" + t.getString("unit"));
            int u = Integer.parseInt(t.getString("unit"));
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpeakLearning speakLearning = new SpeakLearning();
                    //speakLearning.setInfo(myclass, u, "speak", "vocabulary");
                    StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                    studentMainActivity.changeFragment(speakLearning);
                }
            });
            tableRow.addView(but);
            tv.setText(t.getString("startYmd")+" - "+t.getString("endYmd"));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20);
            tableRow.addView(tv);
            showSchedule.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}
