package com.example.english_project;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Menu_Study extends Fragment implements TextToSpeech.OnInitListener {
    ImageView mic, audio;
    EditText inputAns;
    TextView topicC, topicE, fb;
    Button next;
    ProgressBar progressBar;

    TextToSpeech tts;
    private static final int RECOGNIZER_RESULT = 1;

    int QNum, TotalQNum;
    JSONObject TopicObj;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.show_study,container,false);

        mic = view.findViewById(R.id.mic);
        audio = view.findViewById(R.id.audio);
        topicC = view.findViewById(R.id.topicC);
        topicE = view.findViewById(R.id.topicE);
        next = view.findViewById(R.id.next);
        inputAns = view.findViewById(R.id.inputAns);
        fb = view.findViewById(R.id.feedback);
        progressBar = view.findViewById(R.id.progressBar);

        tts = new TextToSpeech(getContext(), this);

        mic.setOnClickListener(micClick);
        audio.setOnClickListener(audioClick);
        next.setOnClickListener(nextclick);

//        方便測試 先封起來
//        next.setEnabled(false);
//        next.setVisibility(View.INVISIBLE);

        QNum=0;
        getTopic();

        return view;
    }

    View.OnClickListener micClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            audio.setEnabled(false);
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
            audio.setEnabled(true);
        }
    };

    View.OnClickListener nextclick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            QNum++;
//            getTopic();
            if(QNum<TotalQNum){
                try{
                    setTopic(QNum);
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d("study frag","topic json error");
                };
            }
            if(QNum==TotalQNum-1){
                next.setText("題目已全作答完畢");
                next.setEnabled(false);
            }
        }
    };
    private void setTopic(int n) throws JSONException {
        JSONObject topicJson = TopicObj.getJSONObject(String.valueOf(n));

        topicC.setText(topicJson.getString("ch"));
        topicE.setText(topicJson.getString("en"));
        Log.d("setT", topicJson.getString("ch") +' '+ topicJson.getString("en"));
//        Log.d("test", String.valueOf(TotalQNum));
    }

    View.OnClickListener audioClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mic.setEnabled(false);
            String s = topicE.getText().toString();
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
            mic.setEnabled(true);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if( requestCode==RECOGNIZER_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String s = matches.get(0);
            inputAns.setText(s);

            if(s.equals(topicE.getText().toString())){
                fb.setText("發音正確！ 可以練習下一題嘍～");
                fb.setTextColor(this.getResources().getColor(R.color.blue));
                next.setEnabled(true);
            }
            else{
                fb.setText("發音可能不太對 再練習一下吧！");
                fb.setTextColor(this.getResources().getColor(R.color.red));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getTopic() {
        class GetTopic extends AsyncTask<Void, Void, String> {
//            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try {
//                    Log.d("json", String.valueOf(QNum));
                    TopicObj = new JSONObject(s);
                    Log.d("json", "拿到題庫6");

                    if (!TopicObj.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), TopicObj.getString("message"), Toast.LENGTH_SHORT).show();
//                      取得題目數量
                        TotalQNum = TopicObj.getInt("rownum");
//                        JSONObject topicJson = obj.getJSONObject("topic");
//
//                        String topicCh = topicJson.getString("ch");
//                        String topicEn = topicJson.getString("en");
//                        topicC.setText(topicCh);
//                        topicE.setText(topicEn);
//                        Log.d("test", String.valueOf(TotalQNum));
//
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
                    setTopic(QNum);
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
                params.put("class", "301");
                params.put("category", "listen");
                params.put("type", "vocabulary");
                return requestHandler.sendPostRequest(URLs.URL_STUDY, params);
            }
        }

        GetTopic gt = new GetTopic();
        gt.execute();
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.ENGLISH);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.d("文字轉語音",  "不支援");
            }
        }
        else{
            Log.d("文字轉語音",  "初始化失敗");
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
