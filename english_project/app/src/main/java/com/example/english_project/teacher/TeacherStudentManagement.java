package com.example.english_project.teacher;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.english_project.MainActivity;
import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


public class TeacherStudentManagement extends Fragment {

    private Spinner spGrade, spUnit, spClass;
    private Button searchBut;
    private ProgressBar progressBar;
    private int unit, myclass, rowNum;
    private TableLayout showText;

    JSONObject obj;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_teacher_student_management, container, false);

        spGrade = (Spinner) view.findViewById(R.id.spGrade);
        spClass = (Spinner) view.findViewById(R.id.spClass);
        spUnit = (Spinner) view.findViewById(R.id.spUnit);
        searchBut = (Button) view.findViewById(R.id.searchBut);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        showText = (TableLayout) view.findViewById(R.id.showText);

        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("searchBut", "onClick");
                unit = Integer.parseInt(spUnit.getSelectedItem().toString());
                myclass = (spGrade.getSelectedItemPosition()+3)*100 + spClass.getSelectedItemPosition()+1;
                showText.removeAllViewsInLayout();
                GetHistory();
            }
        });

        return view;
    }

    private void GetHistory(){

        class getHistory extends AsyncTask<Void, Void, String> {

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
                    Log.d("json", "get history");

                    if (!obj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        rowNum = obj.getInt("row");
                    }
                    else{
                        Log.d("toast", "oh");
                        Toast.makeText(getActivity().getApplicationContext(), "Can't get history", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("history frag","history json error");
                }
                //set history
                try{
                    setHistory(rowNum);
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d("history frag","history json error");
                };

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("myClass", String.valueOf(myclass));
                params.put("unit", String.valueOf(unit));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_HISTORY, params);
            }
        }

        getHistory ul = new getHistory();
        ul.execute();
    }

    public void setHistory(int rowNum) throws JSONException {
        Log.d("setHistory","start");
        TableRow tableRow = new TableRow(getContext());
        String[] arr = {"姓名","聽力練習","口說練習","聽力挑戰","口說挑戰"};
        for(int k=0; k<5; k++){
            TextView tv = new TextView(getContext());
            tv.setText(arr[k]);
            tv.setGravity(Gravity.CENTER);
            tv.setWidth(180);
            tableRow.addView(tv);
        }
        showText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));


        for (int i=0; i<rowNum; i++){
            JSONObject t = obj.getJSONObject(String.valueOf(i));
            Log.d("obj", t.getString("name"));

            tableRow = new TableRow(getContext());
            String[] arr2 = {"name","listen_p","speak_p","listen_c","speak_c"};
            for(int j=0; j<5; j++){
                TextView tv = new TextView(getContext());
                tv.setText(t.getString(arr2[j]));
                tv.setBackgroundColor(Color.parseColor("#ffffff"));
                tv.setGravity(Gravity.CENTER);
                tableRow.addView(tv);
            }
            showText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }



}