package com.example.english_project.teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.URLs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TeacherTextbook extends Fragment {

    int TotalQNum, classForSearch, unit;
    String category, type, time;

    ProgressBar progressBar;
    LinearLayout showTextLayout1;
    Button search;
    Spinner SpGrade, SpClass, SPUnit, SPYear, SPMonth, SPDay, SPCategory, SPType;
    FloatingActionButton fab1, fab2;

    JSONObject AddTextObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        classForSearch = 301;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_textbook, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        showTextLayout1 = view.findViewById(R.id.showTextbook1);
        search = view.findViewById(R.id.ButSearch);
        SpGrade = view.findViewById(R.id.SPgrade);
        SpClass = view.findViewById(R.id.SPclass);
        SPUnit = view.findViewById(R.id.SPunit);
        SPYear = view.findViewById(R.id.SPyear);
        SPMonth = view.findViewById(R.id.SPmonth);
        SPDay = view.findViewById(R.id.SPday);
        SPCategory = view.findViewById(R.id.SPcategory);
        SPType = view.findViewById(R.id.SPtype);

        fab1 = view.findViewById(R.id.fab);
//        fab2 = view.findViewById(R.id.fab2);

        int pg = SpGrade.getSelectedItemPosition() + 3;
        int pc = SpClass.getSelectedItemPosition() + 1;
        classForSearch = pg*100 +pc;
        unit = Integer.parseInt(SPUnit.getSelectedItem().toString());
        time = SPYear.getSelectedItem().toString() + '-' + SPYear.getSelectedItem().toString() + '-' +SPDay.getSelectedItem().toString();
        category = SPCategory.getSelectedItem().toString();
        type = SPType.getSelectedItem().toString();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnClick();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { fab1OnClick(); }
        });
        getTopic();
        return view;
    }

    private void getTopic() {
        class GetTopic extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try {
//                    Log.d("json", String.valueOf(QNum));
                    AddTextObj = new JSONObject(s);
                    Log.d("json", "拿到題庫6");

                    if (!AddTextObj.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), AddTextObj.getString("message"), Toast.LENGTH_SHORT).show();
//                      取得題目數量
                        TotalQNum = AddTextObj.getInt("rownum");
                    } else {
                        Log.d("toast", "oh");
                        Toast.makeText(getActivity().getApplicationContext(), "Can't get topic", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("study frag","study json error");
                }
                //設置題目
                try{
                    setTopic(TotalQNum);
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d("study frag","topic json error");
                };
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                //動態取得學習單元和班級
                params.put("unit", "1");
                params.put("classnum", String.valueOf(classForSearch));
                return requestHandler.sendPostRequest(URLs.URL_STUDY, params);
            }
        }

        GetTopic gt = new GetTopic();
        gt.execute();
    }

    private void setTopic(int n) throws JSONException{
        for (int i=0; i<n; i++){
            Log.d("add text", "add");
            JSONObject t = AddTextObj.getJSONObject(String.valueOf(i));
            TextView tv = new TextView(getContext());
            tv.setText( (i+1) + ". " + t.getString("en"));
            tv.setTextColor(getResources().getColor(R.color.black));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            showTextLayout1.addView(tv);

        }
    }

    private void searchOnClick(){
        int pg = SpGrade.getSelectedItemPosition() + 3;
        int pc = SpClass.getSelectedItemPosition() + 1;
        classForSearch = pg*100 +pc;

        showTextLayout1.removeAllViewsInLayout();
        getTopic();
    }

    private void fab1OnClick(){
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        View v = getLayoutInflater().inflate(R.layout.in_fab_dialog_layout, null);
        ad.setView(v);
        EditText edE = v.findViewById(R.id.edE);
        EditText edC = v.findViewById(R.id.edC);
        ad.setPositiveButton("新增", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String edtextE = edE.getText().toString();
                String edtextC = edC.getText().toString();
                addTextbook(edtextE, edtextC);
            }
        });
        ad.setNegativeButton("取消", null);
        ad.show();
    }

    private void addTextbook(String e, String c){
        if(e.equals("") || c.equals("")){
            Toast.makeText(getContext(), "請輸入內容！", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!e.matches("[a-zA-Z]+") || !c.matches("[\\u4e00-\\u9fa5]+")){
            Toast.makeText(getContext(), "請輸入正確的語言！", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            class AddToDB extends AsyncTask<Void, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressBar.setVisibility(View.VISIBLE);;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    progressBar.setVisibility(View.GONE);
                    try {
                        AddTextObj = new JSONObject(s);
                        Log.d("json add", "送出");

                        if (!AddTextObj.getBoolean("error")) {
                            Toast.makeText(getActivity().getApplicationContext(), AddTextObj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Can't add textbook", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("add","add json error");
                        return;
                    }
                    //刷新顯示區域
                    showTextLayout1.removeAllViewsInLayout();
                    getTopic();
                }

                @Override
                protected String doInBackground(Void... voids) {
                    RequestHandler requestHandler = new RequestHandler();
                    HashMap<String, String> params = new HashMap<>();
                    //動態取得學習單元和班級
                    params.put("unit", String.valueOf(unit));
                    params.put("class", String.valueOf(classForSearch));
                    params.put("e", e);
                    params.put("c", c);
                    params.put("category", category);
                    params.put("type", type);
                    return requestHandler.sendPostRequest(URLs.URL_ADD, params);
                }
            }

            AddToDB at = new AddToDB();
            at.execute();
        }
    }
}