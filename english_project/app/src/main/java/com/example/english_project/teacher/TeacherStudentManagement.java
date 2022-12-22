package com.example.english_project.teacher;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.URLs;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


public class TeacherStudentManagement extends Fragment {

    private Spinner spGrade, spUnit, spClass, spType;
    private Button searchBut;
    private ProgressBar progressBar;
    private int user_id, unit, myclass, rowNum, scoreRowNum, errorRowNum, maxCounts;
    private String type;
    private TableLayout showText;
    private TableLayout showErrorText;
    private String[] errorText = new String[1];


    JSONObject obj;
    JSONObject TextObj;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_teacher_student_management, container, false);

        spGrade = (Spinner) view.findViewById(R.id.spGrade);
        spClass = (Spinner) view.findViewById(R.id.spClass);
        spUnit = (Spinner) view.findViewById(R.id.spUnit);
        spType = (Spinner) view.findViewById(R.id.spType);
        searchBut = (Button) view.findViewById(R.id.searchBut);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        showText = (TableLayout) view.findViewById(R.id.showText);
        showErrorText = (TableLayout) view.findViewById(R.id.showErrorText);
        //default
        unit = 1;
        myclass = 301;
        type = "vocabulary";
        GetHistory();
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("searchBut", "onClick");
                unit = Integer.parseInt(spUnit.getSelectedItem().toString());
                myclass = (spGrade.getSelectedItemPosition()+3)*100 + spClass.getSelectedItemPosition()+1;
                type = spType.getSelectedItem().toString();
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
                int id[] = new int[1];
                progressBar.setVisibility(View.GONE);
                try {
                    obj = new JSONObject(s);
                    Log.d("json", "get history");

                    if (!obj.getBoolean("error")){
//                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        rowNum = obj.getInt("row");
                        int ID[] = new int[rowNum];
                        for (int i=0; i<rowNum; i++) {
                            JSONObject t = obj.getJSONObject(String.valueOf(i));
                            ID[i] = t.getInt("user_id");
                            Log.d("id",String.valueOf(ID[i]));
                        }
                        id = ID;

                        Log.d("rowNum", String.valueOf(rowNum));
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
                    setHistory(rowNum, id);
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
                params.put("type", String.valueOf(type));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_HISTORY, params);
            }
        }

        getHistory ul = new getHistory();
        ul.execute();
    }

    public void setHistory(int rowNum, int id[]) throws JSONException {

        //Button btn[] = new Button[rowNum];

        Log.d("setHistory","start");
        TableRow tableRow = new TableRow(getContext());
        String[] arr = {"　　","姓名","聽力練習","口說練習","聽力測驗","口說測驗"};
        for(int k=0; k<6; k++){
            TextView tv = new TextView(getContext());
            tv.setText(arr[k]);
            tv.setGravity(Gravity.CENTER);
            tv.setWidth(200);
            tv.setTextColor(getResources().getColor(R.color.black));
            tableRow.addView(tv);
        }
        showText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));


        int n = 0;
        for (int i=0; i<rowNum; i++){
            JSONObject t = obj.getJSONObject(String.valueOf(i));
            Log.d("obj", t.getString("name"));

            tableRow = new TableRow(getContext());
            Button btn = new Button(getContext());
            btn.setText(String.valueOf(i+1));
            int finalN = n;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showErrorText.removeAllViewsInLayout();
                    try {
                        setErrorText(id[finalN],t.getString("name"),unit,type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            n++;
            tableRow.addView(btn);

            String[] arr2 = {"name","listen_p","speak_p","listen_c","speak_c"};
            for(int j=0; j<5; j++){

                TextView tv = new TextView(getContext());
                if(t.getString(arr2[j]).equals("-1")) {
                    tv.setText("未完成");
                }
                else{
                    tv.setText(t.getString(arr2[j]));
                }

                tv.setBackgroundColor(Color.parseColor("#ffffff"));
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(getResources().getColor(R.color.black));
                tableRow.addView(tv);
            }

            showText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }

    private void setErrorText(int user_id, String name, int unit, String type){
        TableRow tableRow = new TableRow(getContext());
        TextView tv = new TextView(getContext());

        //name
        tableRow = new TableRow(getContext());
        tv = new TextView(getContext());
        tv.setText(name);
        tv.setTextColor(getResources().getColor(R.color.black));
        tableRow.addView(tv);
        showErrorText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        //listen_c
        tableRow = new TableRow(getContext());
        tv = new TextView(getContext());
        tv.setText("聽力測驗");
        tv.setTextColor(getResources().getColor(R.color.black));
        tableRow.addView(tv);
        showErrorText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        //列出score and errorText

        getEveryScore(user_id, unit, "listen_c", type);
        getErrorText(user_id, unit, "listen_c", type);

        //speak_c
        tableRow = new TableRow(getContext());
        tv = new TextView(getContext());
        tv.setText("口說測驗");
        tv.setTextColor(getResources().getColor(R.color.black));
        tableRow.addView(tv);
        showErrorText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        getEveryScore(user_id, unit, "speak_c", type);
        getErrorText(user_id, unit, "speak_c", type);


    }
    private void setScore(int[] score){
        Log.d("score.length",String.valueOf(score.length));
        TableRow tableRow = new TableRow(getContext());
        TextView tv = new TextView(getContext());
        for(int i=0; i<score.length; i++) {
            tableRow = new TableRow(getContext());
            tv = new TextView(getContext());
            if(score.length == 1){
                tv.setText("未測驗");
                tv.setTextColor(getResources().getColor(R.color.black));
                tableRow.addView(tv);
                showErrorText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
            }
            else{
                tv.setText("第" + (i) + "次成績: " + score[i] + "　錯題: ");
                tv.setTextColor(getResources().getColor(R.color.black));
                tableRow.addView(tv);
                showErrorText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //setError(errorText,i);
            }
        }
    }
    private void setError(String[] error, int i){
        TableRow tableRow = new TableRow(getContext());
        TextView tv = new TextView(getContext());
        tv.setText(error[i]);
        tv.setTextColor(getResources().getColor(R.color.black));
        tableRow.addView(tv);
        showErrorText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }
    private void getErrorText(int user_id, int unit, String category, String type){

        class GetErrorText extends AsyncTask<Void, Void, String> {
            //String errorText[] = new String[1];
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
                    JSONObject ErrorTextObj = new JSONObject(s);
                    Log.d("json", "get Text");

                    if (!ErrorTextObj.getBoolean("error")){
//                      Toast.makeText(getActivity().getApplicationContext(), TextObj.getString("message"), Toast.LENGTH_SHORT).show();
                        errorRowNum = ErrorTextObj.getInt("row");
                        maxCounts=0;
                        if(errorRowNum > 0){

                            //init
                            for(int i=0; i<errorRowNum; i++){
                                JSONObject t = ErrorTextObj.getJSONObject(String.valueOf(i));
                                int counts = t.getInt("counts");
                                if(counts > maxCounts)
                                    maxCounts = counts;
                            }
                            Log.d("maxCounts", String.valueOf(maxCounts));
                            String error[] = new String[maxCounts+1];
                            for(int i=0; i<=maxCounts; i++){
                                error[i] = "";
                            }
                            for(int i=0; i<errorRowNum; i++){
                                JSONObject t = ErrorTextObj.getJSONObject(String.valueOf(i));
                                error[t.getInt("counts")] += t.getString("text") + " ";
                            }
                            errorText = error;
                            for(int i=0; i<errorText.length; i++){
                                Log.d("errorText"+i, String.valueOf(error[i]));
                            }
                        }
                        else{
                            errorText[0] = "無";
                        }
                    }
                    else{
                        // Toast.makeText(getActivity().getApplicationContext(), "Can't get ErrorText", Toast.LENGTH_SHORT).show();
                        Log.d("ErrorText frag","ErrorText json error1");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ErrorText frag","ErrorText json error2");
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();
                Log.d("variable", user_id + String.valueOf(unit)+ category + type);
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("unit", String.valueOf(unit));
                params.put("category", category);
                params.put("type",type);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_DOWNLOADERROR, params);
            }


        }

        GetErrorText ul = new GetErrorText();
        ul.execute();
    }
    private void getEveryScore(int user_id, int unit, String category, String type){
        class GetEveryScore extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try{
                    JSONObject ScoreObj = new JSONObject(s);
                    if(!ScoreObj.getBoolean("error")){
                        scoreRowNum = ScoreObj.getInt("row");
                        int[] sc = new int[scoreRowNum];
                        for(int i=0; i<scoreRowNum; i++){
                            JSONObject t = ScoreObj.getJSONObject(String.valueOf(i));
                            sc[i] = t.getInt("score");
                        }

                        setScore(sc);

                    }
                    else{
                        Log.d("EveryScore frag","EveryScore json error1");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("EveryScore frag","EveryScore json error2");
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user_id));
                params.put("unit", String.valueOf(unit));
                params.put("category", category);
                params.put("type",type);
                return requestHandler.sendPostRequest(URLs.URL_DOWNLOADEVERYSCORE, params);
            }
        }
        GetEveryScore ul = new GetEveryScore();
        ul.execute();

    }



}