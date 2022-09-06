package com.example.english_project.study;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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

import java.util.ArrayList;
import java.util.HashMap;

public class SpeakLearning extends Fragment {

    //getting the current user
    User user = SharedPrefManager.getInstance(getActivity()).getUser();
    String myClass = String.valueOf(user.getMyclass());
    //測試用 之後用按鈕送
    String unit = "1";
    String category = "listen";
    String studyType = "vocabulary";
    //測試用學生答案&counter
    String ans[] = {"ball", "d", "desk",  "tiger", "d", "do", "dog"};
    int ansN = 0;

    String nowTopic = "";

    int totalQNum, nowQNum = 0;

    private JSONObject TextObj;
    private SpeakAdapter adapter;
    private ArrayList<JSONObject> mData = new ArrayList<>();

    private RecyclerView recyclerView;
    private ImageView mic;
    private ProgressBar progressBar;
    private TextView topic;

    private static final int RECOGNIZER_RESULT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speak_learning, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        topic = view.findViewById(R.id.topicTxt);
        mic = view.findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                //測試用
//                try {
//                    JSONObject m = new JSONObject();
//                    m.put("type", false);
//                    m.put("text", ans[ansN]);
//                    adapter.addItem(m);
//                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                    checkAnswer(ans[ansN++]);
////                    setTopic();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                //錄音
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

        //連接元件
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewItem);

        //設置RecyclerView 為列表型態
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //設置格線
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        //將資料交給adapter
        adapter = new SpeakAdapter(mData);
        recyclerView.setAdapter(adapter);
        getTopic();
        return view;
    }

    public void checkAnswer(String ans) throws JSONException {
        if (ans.equals(nowTopic)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    trueFeedback();
                }
            }, 1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        setTopic();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    falseFeedback();
                }
            }, 1000);
        }
    }

    public void trueFeedback() {
        String s = "說得好！";
        sendTeacherText(s);
    }

    public void falseFeedback() {
        String s = "發音不太正確 再試一次吧！";
        sendTeacherText(s);
    }

    //先廢掉 之後再來處理
    public void showNextBut() {
        try {
            JSONObject m = new JSONObject();
            m.put("type", true);
            m.put("isText", false);
            adapter.addItem(m);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTeacherText(String s) {
        try {
            JSONObject m = new JSONObject();
            m.put("type", true);
            m.put("isText", true);
            m.put("text", s);
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
                ;
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
        if (nowQNum < totalQNum) {
            JSONObject t = TextObj.getJSONObject(String.valueOf(nowQNum));
            nowTopic = t.getString("en");
            topic.setText(nowTopic);
            nowQNum++;
            String s = "我要聽到你說： " + nowTopic;
            sendTeacherText(s);
        }
        else{
            mic.setEnabled(false);
            mic.setImageTintList(ColorStateList.valueOf((getResources().getColor(R.color.gray))));
            String s = "題目已全作答完畢";
            sendTeacherText(s);
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
}