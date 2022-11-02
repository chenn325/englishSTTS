package com.example.english_project.teacher;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TeacherStudentManagement extends Fragment {

    private Spinner spGrade, spUnit, spClass, spType;
    private Button searchBut;
    private ProgressBar progressBar;
    private int user_id, unit, myclass, rowNum;
    private String type;
    private TableLayout showText;
    private TextView showErrorText;
    private String errorText[];

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
        showErrorText = (TextView) view.findViewById(R.id.showErrorText);

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
                params.put("type", String.valueOf(type));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_HISTORY, params);
            }
        }

        getHistory ul = new getHistory();
        ul.execute();
    }

    public void setHistory(int rowNum) throws JSONException {

        Button btn[] = new Button[rowNum*2];
        String errorText[] = new String[rowNum*2];

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


        int n = 0;
        for (int i=0; i<rowNum; i++){
            JSONObject t = obj.getJSONObject(String.valueOf(i));
            Log.d("obj", t.getString("name"));

            tableRow = new TableRow(getContext());
            String[] arr2 = {"name","listen_p","speak_p","listen_c","speak_c"};
            for(int j=0; j<=2; j++){
                TextView tv = new TextView(getContext());
                tv.setText(t.getString(arr2[j]));
                tv.setBackgroundColor(Color.parseColor("#ffffff"));
                tv.setGravity(Gravity.CENTER);
                tableRow.addView(tv);
            }
            int k;
            for(k=3; k<5; k++){
                btn[n] = new Button(getContext());
                if(t.getString(arr2[k]).equals("-1")){
                    btn[n].setText("未完成");
                }
                else{
                    btn[n].setText(t.getString(arr2[k]));
                }
                btn[n].setGravity(Gravity.CENTER);
                int finalK = k;
                if(finalK == 3) {
                    try {
                        GetText("listen", t.getString("user_id"), errorText, n);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        GetText("speak", t.getString("user_id"), errorText, n);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                int finalN = n;
                btn[n].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showErrorText.setText(errorText[finalN]);
                        Log.d("showErrorText", errorText[finalN]);
                    }
                });
                tableRow.addView(btn[n]);
                n++;
            }
            showText.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }

    private void GetText(String category, String userId, String err[], int n){

        class getText extends AsyncTask<Void, Void, String> {

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
                    TextObj = new JSONObject(s);
                    Log.d("json", "get Text");

                    if (!TextObj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), TextObj.getString("message"), Toast.LENGTH_SHORT).show();
                        rowNum = TextObj.getInt("row");
                        Log.d("rowNum", String.valueOf(rowNum));
                        if(rowNum > 0){
                            err[n] = "";
                            for(int i=0; i<rowNum; i++){
                                JSONObject t = TextObj.getJSONObject(String.valueOf(i));
                                err[n] += t.getString("text") + ", ";
                            }
                        }
                        else{
                            err[n] = "無";
                        }
                        Log.d("errorText", err[n]);
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "Can't get ErrorText", Toast.LENGTH_SHORT).show();
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
                Log.d("variable", userId + String.valueOf(unit)+ category + type);
                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("unit", String.valueOf(unit));
                params.put("category", category);
                params.put("type",type);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_DOWNLOADERROR, params);
            }


        }

        getText ul = new getText();
        ul.execute();
    }

}