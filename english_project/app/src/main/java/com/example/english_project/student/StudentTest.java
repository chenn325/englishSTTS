package com.example.english_project.student;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;
import com.example.english_project.study.ListenTest;
import com.example.english_project.study.SpeakTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class StudentTest extends Fragment {

    private int rowNum, myclass;
    ProgressBar progressBar;
    TableLayout showSchedule;

    JSONObject obj;

    String category[] = {"Listen", "Speak"};
    String type[] = {"vocabulary","sentence","phrase"};

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_student_test,container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        showSchedule = (TableLayout) view.findViewById(R.id.showSchedule);

        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        myclass = user.getMyclass();
        set();

        return view;
    }

    private void set(){
        for(int i=0; i< category.length; i++)
            for(int j=0; j<type.length; j++)
                GetSchedule(i, j);
    }

    private void GetSchedule(int c, int t){

        class getSchedule extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try {
                    obj = new JSONObject(s);
                    Log.d("json", "get schedule");

                    if (!obj.getBoolean("error")){
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
                    setSchedule(rowNum, c, t);
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
                params.put("category", category[c].toLowerCase());
                params.put("type", type[t]);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_SETSCHEDULE, params);
            }
        }

        getSchedule ul = new getSchedule();
        ul.execute();
    }

    @SuppressLint("ResourceAsColor")
    public void setSchedule(int rowNum, int c, int t) throws JSONException {
//        Log.d("setSchedule","start");
        TextView tv = new TextView(getContext());
        TableRow tableRow = new TableRow(getContext());
        tv.setText(category[c]);
        tv.setTextSize(20);
        tv.setTextColor(getResources().getColor(R.color.black));
        tableRow.addView(tv);
        TextView tv2 = new TextView(getContext());
        tv2.setText("- "+type[t]);
        tv2.setTextSize(20);
        tv2.setTextColor(getResources().getColor(R.color.black));
        tableRow.addView(tv2);
        showSchedule.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int cusTextSize = metrics.widthPixels/60;
        for (int i=0; i<rowNum; i++){
            JSONObject topic = obj.getJSONObject(String.valueOf(i));
            tableRow = new TableRow(getContext());
            tv = new TextView(getContext());
            Button but = new Button(getContext());
            but.setText("Unit" + topic.getString("unit"));
            int u = Integer.parseInt(topic.getString("unit"));
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(c==0) { //listen
                        ListenTest listenTest = new ListenTest();
                        listenTest.setInfo(myclass, u, "listen".toLowerCase(), type[t]);
                        StudentMainActivity studentMainActivity = (StudentMainActivity) getActivity();
                        studentMainActivity.changeFragment(listenTest);
                    }
                    else{ //speak
                        SpeakTest speakTest = new SpeakTest();
                        speakTest.setInfo(myclass, u, "speak".toLowerCase(), type[t]);
                        StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                        studentMainActivity.changeFragment(speakTest);
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                but.setBackground(this.getResources().getDrawable(R.drawable.but_test));
            }
            but.setTextSize(cusTextSize);
            tableRow.addView(but);
            tv.setText(" "+topic.getString("startYmd")+" - "+topic.getString("endYmd"));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(cusTextSize);
            tv.setTextColor(getResources().getColor(R.color.black));
            tableRow.addView(tv);
            tableRow.setPadding(0, 10, 0, 10);
            showSchedule.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}
