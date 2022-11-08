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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class SpeakTest extends Fragment {

    //getting the current user
    User user = SharedPrefManager.getInstance(getActivity()).getUser();
    String myClass = String.valueOf(user.getMyclass());
    //測試用 之後用按鈕送
    String unit = "1";
    String category = "listen";
    String studyType = "vocabulary";
    //測試用學生答案&counter
    String ans[] = {"apple", "ball", "c", "cat", "d", "desk", "tiger", "fox", "bubble"};
    int ansN = 0;
    int count=0;
    public int[] answer;

    String nowTopic = "";

    int totalQNum, nowQNum = -1;

    private JSONObject TextObj;
    private SpeakAdapter adapter;
    private ArrayList<JSONObject> mData = new ArrayList<>();

    private RecyclerView recyclerView;
    private ImageView mic;
    private ProgressBar progressBar;
//    private TextView topic;

    private static final int RECOGNIZER_RESULT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speak_learning, container, false);

        progressBar = view.findViewById(R.id.progressBar);
//        topic = view.findViewById(R.id.topicTxt);
        mic = view.findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //測試用
                try {
                    JSONObject m = new JSONObject();
                    m.put("type", false);
                    m.put("text", ans[ansN]);
                    adapter.addItem(m);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    checkAnswer(ans[ansN++]);
//                  setTopic();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //錄音
//                try {
//                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
//                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speech to text");
////                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
//                    startActivityForResult(intent, RECOGNIZER_RESULT);
//                }catch(ActivityNotFoundException e){
//                    Log.d("MainActivity", "沒谷哥哥ㄌㄚ");
//                    AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
//                    ad.setMessage("您未安裝google軟體\n請安裝後再試一次！");
//                    ad.setPositiveButton("好", null);
//                    ad.show();
//                }
            }
        });

        //連接元件
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewItem);

        //設置RecyclerView 為列表型態
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //設置格線
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        //將資料交給adapter
        adapter = new SpeakAdapter(mData, user, getContext());
        recyclerView.setAdapter(adapter);
        getTopic();
        Log.d("study test", "study test hello");
        return view;
    }

    public void checkAnswer(String userAns) throws JSONException {
        final String fUserAns;
        if(isLetter(userAns.charAt(0))) { fUserAns = userAns.toLowerCase(); }
            else { fUserAns = userAns; }
        if (fUserAns.equals(nowTopic)) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    trueFeedback();
                    sendTeacherText("正確！");
//                }
//            }, 1000);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    try {
                        setTopic();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                }
//            }, 2000);
        } else {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    falseFeedback();
                    answer[nowQNum]++;
                    switch(answer[nowQNum]){
                        case 1://重複錯誤
                            sendTeacherSound("你的答案發音比較像" + fUserAns, fUserAns);
                            sendTeacherText("再試一次吧！");
                            break;
                        case 2://提問引導
                            sendTeacherText("要不要試著回想學習模式時教的發音呢？");
                            sendTeacherText("最後你的答案是？");
                            break;
                        case 3://明確校正
                            sendTeacherText("正確的發音應該是" + nowTopic);
                            //加入tts
                            sendTeacherText("讓我們進入下一題吧！");
                            try {
                                setTopic();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
//                }
//            }, 1000);
        }
    }

    public void sendTeacherText(String s) {
        try {
            JSONObject m = new JSONObject();
            m.put("type", true);
            m.put("isTopic", false);
            m.put("text", s);
            adapter.addItem(m);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTeacherSound(String s, String userAns) {//sendTeacherTopic
        try {
            JSONObject m = new JSONObject();
            m.put("type", true);
            m.put("isTopic", true);
            m.put("text", s);
            m.put("sound_text", userAns);
            adapter.addItem(m);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendStudentText(String s) {
        try {
            JSONObject m = new JSONObject();
            m.put("type", false);
            m.put("text", s);
            adapter.addItem(m);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
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
                        answer = new int[totalQNum];
                        for(int i=0; i<totalQNum; i++){
                            answer[0] = 0;
                        }
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
                    sendTeacherText("總共有"+totalQNum+"題");
                    sendTeacherText("測驗開始！");
                    setTopic();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("study frag", "topic json error");
                }
                ;
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

    public void setTopic() throws JSONException {
        nowQNum++;
        if (nowQNum < totalQNum) {
            JSONObject t = TextObj.getJSONObject(String.valueOf(nowQNum));
            nowTopic = t.getString("en");
//            String s = "我要聽到你說： " + nowTopic;
//            String s = nowTopic;
            sendTeacherText("第"+(nowQNum+1)+"題");
            sendTeacherText(nowTopic);
        }
        else{
            mic.setEnabled(false);
            mic.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.gray))));
            sendTeacherText("恭喜你完成測驗!");
            //算分
            sendTeacherText("本次成績為： " + String.valueOf(Cal()));
            for(int i=0; i<totalQNum; i++){
                if(answer[i]!=0){
                    UploadError( (TextObj.getJSONObject(String.valueOf(i))).getString("en") );
                }
            }
        }
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

    private int Cal(){

        int score = 0; //總分

        float per_score = 100/totalQNum; //一題的分數
        BigDecimal bd = new BigDecimal((double) per_score);
        bd = bd.setScale(2,4); //取後兩位，四捨五入
        per_score = bd.floatValue();
        if(per_score*totalQNum<100)
            score += 100 - per_score*totalQNum;

        for(int i=0; i<totalQNum; i++){
            float temp=0;
            switch(answer[i]){
                case 0:
                    score += per_score;
                    break;
                case 1:
                    score += per_score*2/3;
                    break;
                case 2:
                    score += per_score*1/3;
                    break;
                default: //3就0分
                    break;
            }
        }
        Log.d("score", String.valueOf(score));

        int finalScore = score;
        class cal extends AsyncTask<Void, Void, String> {

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
                    JSONObject obj = new JSONObject(s);
                    Log.d("json", "LC");

                    if (!obj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                        rowNum = obj.getInt("row");
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "Can't cal", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("history_LC frag","LC json error");
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
                params.put("score", String.valueOf(finalScore));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_HISTORY_LC, params);
            }
        }

        cal ul = new cal();
        ul.execute();

        return score;
    }
    //錯題上傳給老師
    private void UploadError(String errorText){

        class uploadError extends AsyncTask<Void, Void, String> {

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
                    JSONObject UpLoadobj = new JSONObject(s);

                    if (!UpLoadobj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), UpLoadobj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "Can't upload", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("UploadError frag","UploadError json error");
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(user.getId()));
                params.put("category", category);
                params.put("type", studyType);
                params.put("unit", String.valueOf(unit));
                params.put("en", errorText);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_UPLOADERROR, params);
            }
        }

        uploadError ul = new uploadError();
        ul.execute();

    }
}