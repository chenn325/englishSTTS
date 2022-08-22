package com.example.english_project.teacher;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

    int totalQNum, classForSearch, unit, nowSettingDate;
    String category, type, date;
    boolean fabDmode;

    ProgressBar progressBar;
    LinearLayout showTextLayout1;
    Button search, butSetDateS, butSetDateE;
    Spinner SpGrade, SpClass, SPUnit, SPCategory, SPType;
    FloatingActionButton fabA, fabD;
    TextView showDateS, showDateE;
    DatePickerDialog.OnDateSetListener datePicker;
    Calendar calendar = Calendar.getInstance();
    CheckBox[] cbArray;

    JSONObject TextObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nowSettingDate = 0;
        fabDmode = false;
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
                date = sdf.format(calendar.getTime());
                String[] dateS = date.split("-");
                int dateI = Integer.parseInt(dateS[0])*10000+Integer.parseInt(dateS[1])*100+Integer.parseInt(dateS[2]);
                if(nowSettingDate==0) { //start date
                    String[] endDate = (showDateE.getText().toString()).split("-");
                    int endDI = Integer.parseInt(endDate[0])*10000+Integer.parseInt(endDate[1])*100+Integer.parseInt(endDate[2]);
                    if(dateI > endDI) {
                        Toast.makeText(getActivity().getApplicationContext(), "開始日期須比結束日期早", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        changeDateToDB(date, "true");
                    }
                }
                else { //end date
                    String[] startDate = (showDateS.getText().toString()).split("-");
                    int startDI = Integer.parseInt(startDate[0])*10000+Integer.parseInt(startDate[1])*100+Integer.parseInt(startDate[2]);
                    if(dateI < startDI) {
                        Toast.makeText(getActivity().getApplicationContext(), "結束日期須比開始日期晚", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        changeDateToDB(date, "false");
                    }
                }
            }
        };

        SPCategory = view.findViewById(R.id.SPcategory);
        SPType = view.findViewById(R.id.SPtype);
        fabA = view.findViewById(R.id.fabAdd);
        fabD = view.findViewById(R.id.fabDelete);

        int pg = SpGrade.getSelectedItemPosition() + 3;
        int pc = SpClass.getSelectedItemPosition() + 1;
        classForSearch = pg*100 +pc;
        unit = Integer.parseInt(SPUnit.getSelectedItem().toString());
        category = SPCategory.getSelectedItem().toString();
        type = SPType.getSelectedItem().toString();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnClick();
            }
        });
        fabA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { fabAOnClick(); }
        });
        fabD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { fabDOnClick(); }
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
        testSet();
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
                    TextObj = new JSONObject(s);
                    if (!TextObj.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), TextObj.getString("message"), Toast.LENGTH_SHORT).show();
//                      取得題目數量
                        totalQNum = TextObj.getInt("rownum");
                        if(!TextObj.getBoolean("dateGetError")){
                            sd = TextObj.getString("startDate");
                            ed = TextObj.getString("endDate");
                        }
                    } else {
                        Log.d("get topic", "oh");
                        Toast.makeText(getActivity().getApplicationContext(), "There are no topic", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("study frag","study json error");
                    return;
                }
                //設置題目
                try{
                    setTopic();
                    setDateView(sd, ed);
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

    private void setTopic() throws JSONException{
        cbArray = new CheckBox[totalQNum];
        for (int i = 0; i< totalQNum; i++){
            JSONObject t = TextObj.getJSONObject(String.valueOf(i));
            CheckBox cb = new CheckBox(getContext());
            cb.setText( (i+1) + ". " + t.getString("en") + " " + t.getString("ch") );
            cb.setTextColor(getResources().getColor(R.color.black));
            cb.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            cb.setButtonTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            cb.setEnabled(false);
            cbArray[i] = cb;
            showTextLayout1.addView(cb);
        }
    }

    private void searchOnClick(){
        int pg = SpGrade.getSelectedItemPosition() + 3;
        int pc = SpClass.getSelectedItemPosition() + 1;
        classForSearch = pg*100 +pc;
        unit = Integer.parseInt(SPUnit.getSelectedItem().toString());
        category = SPCategory.getSelectedItem().toString();
        type = SPType.getSelectedItem().toString();

        //先檢查是否有該班級單元日期資料 沒有則創建 有則進行顯示修改
        testSet();
        showTextLayout1.removeAllViewsInLayout();
        getTopic();
    }

    private void fabAOnClick(){
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

    private void fabDOnClick(){
        //mode false:未點選 true:點選
        if(!fabDmode) {
            for (int i = 0; i < totalQNum; i++) {
                cbArray[i].setButtonTintList(ColorStateList.valueOf((getResources().getColor(R.color.dark_green))));
                cbArray[i].setEnabled(true);
            }
            fabD.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_green)));
            fabD.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            fabDmode = true;
        }
        else{
            boolean changed = false;
            for(int i=0; i<totalQNum; i++){
                if(cbArray[i].isChecked()){
                    changed = true;
                    showTextLayout1.removeView(cbArray[i]);
                    deleteFromDb(i);
                    Log.d("remove text", i + " is removed");
                }
                cbArray[i].setButtonTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                cbArray[i].setEnabled(false);
            }
            if(changed) {
                showTextLayout1.removeAllViewsInLayout();
                getTopic();
            }
            fabD.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            fabD.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            fabDmode = false;
        }
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
                        TextObj = new JSONObject(s);
                        Log.d("json add", "送出");

                        if (!TextObj.getBoolean("error")) {
                            Toast.makeText(getActivity().getApplicationContext(), TextObj.getString("message"), Toast.LENGTH_SHORT).show();
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

    private void changeDateToDB(String date, String select){
        class ChangeDateToDB extends AsyncTask<Void, Void, String>{

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
                    JSONObject changeDateJson = new JSONObject(s);
                    if (!changeDateJson.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), changeDateJson.getString("message"), Toast.LENGTH_SHORT).show();
                        if(select.equals("true"))   {   showDateS.setText(date);    }
                        else    {   showDateE.setText(date);    }
                    } else {
                        Log.d("toast", "oh");
                        Toast.makeText(getActivity().getApplicationContext(), "Can't change date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("teacher textbook frag","change date json error");
                    return;
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                //動態取得學習單元和班級
                params.put("unit", String.valueOf(unit));
                params.put("class", String.valueOf(classForSearch));
                params.put("date", date);
                params.put("select", select);

                return requestHandler.sendPostRequest(URLs.URL_CHANGEDATE, params);
            }
        }
        ChangeDateToDB cdtd = new ChangeDateToDB();
        cdtd.execute();
    }

    private void setDateView(String s, String e){
        showDateS.setText(s);
        showDateE.setText(e);
    }

    private void deleteFromDb(int i) {
        class DeleteFromDb extends AsyncTask<Void, Void, String> {
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
                    JSONObject DeleteTextObj = new JSONObject(s);
                    if (!DeleteTextObj.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), DeleteTextObj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("toast", "oh");
                        Toast.makeText(getActivity().getApplicationContext(), "Can't delete topic", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("delete topic frag","delete json error");
                    return;
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("unit", String.valueOf(unit));
                params.put("class", String.valueOf(classForSearch));

                try {
                    JSONObject t = TextObj.getJSONObject(String.valueOf(i));
                    String e = t.getString("en");
                    String c = t.getString("ch");
                    params.put("e", e);
                    params.put("c", c);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                return requestHandler.sendPostRequest(URLs.URL_DELETE, params);
            }
        }

        DeleteFromDb dfd = new DeleteFromDb();
        dfd.execute();
    }

    private void testSet(){
        class TestSet extends AsyncTask<Void, Void, String> {

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
                    JSONObject testSetObj = new JSONObject(s);
                    Toast.makeText(getActivity().getApplicationContext(), testSetObj.getString("message"), Toast.LENGTH_SHORT).show();
//                    Log.d("test set", "");
                    if (testSetObj.getBoolean("error")) {
                        Log.d("test set", "has set");
                        return;
                    }
                    else{
                        setDateView(testSetObj.getString("startDate"), testSetObj.getString("endDate"));
                        Log.d("test set", "set successful");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("test set","test set json error");
                    return;
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("unit", String.valueOf(unit));
                params.put("class", String.valueOf(classForSearch));
                return requestHandler.sendPostRequest(URLs.URL_TESTSET, params);
            }
        }

        TestSet ts = new TestSet();
        ts.execute();
    }
}