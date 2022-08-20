package com.example.english_project.teacher;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class TeacherTextbook extends Fragment {

    int TotalQNum, classForSearch, unit, nowSettingDate;
    String category, type, time, date;

    ProgressBar progressBar;
    LinearLayout showTextLayout1;
    Button search, butSetDateS, butSetDateE;
    Spinner SpGrade, SpClass, SPUnit, SPYear, SPMonth, SPDay, SPCategory, SPType;
    FloatingActionButton fab1;

    TextView showDateS, showDateE;
    DatePickerDialog.OnDateSetListener datePicker;
    Calendar calendar = Calendar.getInstance();

    JSONObject AddTextObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nowSettingDate = 0;
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
//        SPYear = view.findViewById(R.id.SPyear);
//        SPMonth = view.findViewById(R.id.SPmonth);
//        SPDay = view.findViewById(R.id.SPday);
        butSetDateS = view.findViewById(R.id.setDateS);
        butSetDateE = view.findViewById(R.id.setDateE);
        showDateS = view.findViewById(R.id.showDateS);
        showDateE = view.findViewById(R.id.showDateE);
        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
//                showDateS.setText(sdf.format(calendar.getTime()));
                date = sdf.format(calendar.getTime());
                String[] dateS = date.split("-");
                int dateI = Integer.parseInt(dateS[0])*10000+Integer.parseInt(dateS[1])*100+Integer.parseInt(dateS[2]);
                if(nowSettingDate==0) { //start date
                    String[] endDate = (showDateE.getText().toString()).split("-");
                    int endDI = Integer.parseInt(endDate[0])*10000+Integer.parseInt(endDate[1])*100+Integer.parseInt(endDate[2]);
                    Log.d("set d", endDI + " " + dateI);
                    if(dateI > endDI) {
                        Toast.makeText(getActivity().getApplicationContext(), "開始日期須比結束日期早", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "設置成功！", Toast.LENGTH_SHORT).show();
                        showDateS.setText(date);
                    }
                }
                else { //end date
                    String[] startDate = (showDateS.getText().toString()).split("-");
                    int startDI = Integer.parseInt(startDate[0])*10000+Integer.parseInt(startDate[1])*100+Integer.parseInt(startDate[2]);
                    Log.d("set d", startDI + " " + dateI);
                    if(dateI < startDI) {
                        Toast.makeText(getActivity().getApplicationContext(), "結束日期須比開始日期晚", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "設置成功！", Toast.LENGTH_SHORT).show();
                        showDateE.setText(date);
                    }
                }
            }
        };

        SPCategory = view.findViewById(R.id.SPcategory);
        SPType = view.findViewById(R.id.SPtype);
        fab1 = view.findViewById(R.id.fab);

        int pg = SpGrade.getSelectedItemPosition() + 3;
        int pc = SpClass.getSelectedItemPosition() + 1;
        classForSearch = pg*100 +pc;
        unit = Integer.parseInt(SPUnit.getSelectedItem().toString());
//        time = SPYear.getSelectedItem().toString() + '-' + SPYear.getSelectedItem().toString() + '-' +SPDay.getSelectedItem().toString();
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
        butSetDateS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(0);
            }
        });
        butSetDateE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(1);
            }
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
                String sd="", ed="";
                try {
                    AddTextObj = new JSONObject(s);
//                    Log.d("json", "拿到題庫6");

                    if (!AddTextObj.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), AddTextObj.getString("message"), Toast.LENGTH_SHORT).show();
//                      取得題目數量
                        TotalQNum = AddTextObj.getInt("rownum");
                        if(!AddTextObj.getBoolean("dateGetError")){
                            sd = AddTextObj.getString("startDate");
                            ed = AddTextObj.getString("endDate");
                        }
                    } else {
                        Log.d("toast", "oh");
                        Toast.makeText(getActivity().getApplicationContext(), "Can't get topic", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("study frag","study json error");
                    return;
                }
                //設置題目
                try{
                    setTopic(TotalQNum);
                    setDateFromDB(sd, ed);
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
                params.put("unit", String.valueOf(unit));
                params.put("class", String.valueOf(classForSearch));
                params.put("category", category);
                params.put("type", type);

                return requestHandler.sendPostRequest(URLs.URL_STUDY, params);
            }
        }

        GetTopic gt = new GetTopic();
        gt.execute();
    }

    private void setTopic(int n) throws JSONException{
        for (int i=0; i<n; i++){
//            Log.d("add text", "add");
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
        unit = Integer.parseInt(SPUnit.getSelectedItem().toString());
        //time = SPYear.getSelectedItem().toString() + '-' + SPYear.getSelectedItem().toString() + '-' +SPDay.getSelectedItem().toString();
        category = SPCategory.getSelectedItem().toString();
        type = SPType.getSelectedItem().toString();

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
    private void setDate(int i){
        DatePickerDialog d = new DatePickerDialog(getActivity(), datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        nowSettingDate = i;
        d.show();
    }
    private void setDateFromDB(String s, String e){
        showDateS.setText(s);
        showDateE.setText(e);
    }
}