package com.example.english_project.study;

import static android.app.Activity.RESULT_OK;

import static java.lang.Character.isLetter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;
import com.example.english_project.student.StudentMainActivity;
import com.example.english_project.student.StudentStudy;
import com.example.english_project.study.model.MyModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SpeakLearning extends Fragment {

    //getting the current user
    User user = SharedPrefManager.getInstance(getActivity()).getUser();
    String myClass = String.valueOf(user.getMyclass());
    //測試用 之後用按鈕送
    String unit = "1";
    String category = "listen";
    String studyType = "vocabulary";
    String partnerImage = "girl1";
    //測試用學生答案&counter
    String ans[] = {"apple", "ball", "cat", "date", "duck", "dark", "tiger", "fox", "bubble"};
    int ansN = 0;
    int resID = 0;
    String nowTopic = "";
    Boolean lastSaying = false;  //1 teacher, 0 student
    int delay_time = 1000;

    int totalQNum, nowQNum = 0;

    private JSONObject TextObj;
    private SpeakAdapter adapter;
    private ArrayList<JSONObject> mData = new ArrayList<>();
    private List<MyModel> myModelList = new ArrayList<MyModel>();

    private RecyclerView recyclerView;
    private ImageView mic;
    private ProgressBar progressBar;
    private LinearLayout butArea;
    private Button backBtn;

    private static final int RECOGNIZER_RESULT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speak_learning, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        butArea = view.findViewById(R.id.buttonArea);
        backBtn = (Button) view.findViewById(R.id.backBtn);
        mic = view.findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //測試用 DON'T DELETE ME!!
//                sendStudentText(ans[ansN]);
//                try {
//                    checkAnswer(ans[ansN++]);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                //錄音 DON'T DELETE ME!!
                try {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speech to text");
                    startActivityForResult(intent, RECOGNIZER_RESULT);
                }catch(ActivityNotFoundException e){
                    Log.d("MainActivity", "沒谷哥哥ㄌㄚ");
                    AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                    ad.setMessage("您未安裝google軟體\n請安裝後再試一次！");
                    ad.setPositiveButton("好", null);
                    ad.show();
                }
            }
        });

        mic.setEnabled(false);
        mic.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.gray))));

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                studentMainActivity.changeFragment(new StudentStudy());
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        //設定partner頭像
        switch(user.getPartner()){
            case 1:
                partnerImage = "girl1";
                break;
            case 2:
                partnerImage = "girl2";
                break;
            case 3:
                partnerImage = "boy3";
                break;
        }
        resID = getResources().getIdentifier(partnerImage , "drawable", getActivity().getPackageName());
        //連接元件
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewItem);

        //設置RecyclerView 為列表型態
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //將資料交給adapter
        adapter = new SpeakAdapter(mData, user, getContext(), resID);
        recyclerView.setAdapter(adapter);
        getTopic();
        //隱藏menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        return view;
    }

    public void checkAnswer(String ans) throws JSONException {
        mic.setEnabled(false);
        mic.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.gray))));
        int time = delay_time;
        if(isLetter(ans.charAt(0))) {
            ans = ans.toLowerCase();
        }
        if (ans.equals(nowTopic)) {
            sendTeacherText("說得好！", time);
            time+=delay_time;
            try {
                setTopic(time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            sendTeacherText("發音不太正確 再試一次吧！", time);
            setMicEnable(time);
        }
    }

    public void setMicEnable(int t){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mic.setEnabled(true);
                mic.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.black))));
            }
        }, t);
    }

    public void sendTeacherText(String s, int t) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject m = new JSONObject();
                    m.put("type", true);
                    m.put("isTopic", false);
                    m.put("text", s);
                    m.put("lastSay", lastSaying);
                    Log.d("photo test", "t t "+lastSaying);
                    adapter.addItem(m);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    lastSaying = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, t);
    }

    public void sendTeacherTopic(String s, int t) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject m = new JSONObject();
                    m.put("type", true);
                    m.put("isTopic", true);
                    m.put("text", s);
                    m.put("sound_text", nowTopic);
                    m.put("lastSay", lastSaying);
                    Log.d("photo test", "t p "+lastSaying);
                    adapter.addItem(m);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    lastSaying = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, t);
    }

    public void sendStudentText(String s) {
        try {
            JSONObject m = new JSONObject();
            m.put("type", false);
            m.put("text", s);
            m.put("lastSay", lastSaying);
            Log.d("photo test", "s  "+lastSaying);
            adapter.addItem(m);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            lastSaying = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getTopic() {
        class GetTopic extends AsyncTask<Void, Void, String> {
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
                    if (!TextObj.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), TextObj.getString("message"), Toast.LENGTH_SHORT).show();
                        totalQNum = TextObj.getInt("rownum");
                        Log.d("get topic", "totalQNum = " + totalQNum);
                    } else {
                        Log.d("get topic", "oh");
                        Toast.makeText(getActivity().getApplicationContext(), "There are no topic", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("study frag", "study json error");
                    return;
                }
                //設置題目
                try {
                    int time = 100;
                    setTopic(time);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("study frag", "topic json error");
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("unit", unit);
                params.put("class", myClass);
                params.put("category", category);
                params.put("type", studyType);

                return requestHandler.sendPostRequest(URLs.URL_STUDY, params);
            }
        }

        GetTopic gt = new GetTopic();
        gt.execute();
    }

    public void setTopic(int t) throws JSONException {
        if (nowQNum < totalQNum) {
            JSONObject tObj = TextObj.getJSONObject(String.valueOf(nowQNum));
            nowTopic = tObj.getString("en");
            if(nowQNum!=0) {    sendTeacherText("下一個單字", t);   }
            else {   sendTeacherText("跟著我一起念吧", t);     }
            t+=delay_time;
            sendTeacherTopic(nowTopic, t);
            setMicEnable(t);
            nowQNum++;
        }
        else{
            butArea.removeAllViewsInLayout();
            Button exitBtn = new Button(getContext());
            exitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                    studentMainActivity.changeFragment(new StudentStudy());
                }
            });
            exitBtn.setText("EXIT");
            exitBtn.setTextSize(25);
            exitBtn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            butArea.addView(exitBtn);
            sendTeacherText("題目已全作答完畢", t);
            t+=delay_time;
            sendTeacherText("請按下方EXIT鍵離開", t);
            plus();
        }
    }

    //listen_p次數+1
    private void plus(){

        class Plus extends AsyncTask<Void, Void, String> {

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
                    JSONObject tempObj = new JSONObject(s);
                    Log.d("json", "LP");

                    if (!tempObj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), tempObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "Can't plus", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("history_LP frag","LP json error");
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user.getId()));
                params.put("unit", String.valueOf(unit));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_HISTORY_SP, params);
            }
        }

        Plus p = new Plus();
        p.execute();
    }

    public void setInfo(int myClass, int unit , String category, String type){
        this.myClass = String.valueOf(myClass);
        this.unit = String.valueOf(unit);
        this.category = category;
        this.studyType = type;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if( requestCode==RECOGNIZER_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String s = matches.get(0);
            sendStudentText(s);
            try {
                checkAnswer(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}