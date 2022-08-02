package com.example.tts_test;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.*;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    ImageView mic, audio;
    EditText inputAns;
    TextView topicC, topicE, fb;
    Button next;

    TextToSpeech tts;
    private static final int RECOGNIZER_RESULT = 1;

    //暫時題庫，未來用資料庫代替
    String topicCDb[] = {"狗", "貓", "兔子"};
    String topicEDb[] = {"dog", "cat", "rabbit"};
    int QNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mic = findViewById(R.id.mic);
        audio = findViewById(R.id.audio);
        topicC = findViewById(R.id.topicC);
        topicE = findViewById(R.id.topicE);
        next = findViewById(R.id.next);
        inputAns = findViewById(R.id.inputAns);
        fb = findViewById(R.id.feedback);

        tts = new TextToSpeech(this, this);

        mic.setOnClickListener(micClick);
        audio.setOnClickListener(audioClick);
        next.setOnClickListener(nextclick);

        next.setEnabled(false);
        next.setVisibility(View.INVISIBLE);

        QNum=0;
        topicC.setText(topicCDb[QNum]);
        topicE.setText(topicEDb[QNum]);
        QNum++;
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
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
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
            topicC.setText(topicCDb[QNum]);
            topicE.setText(topicEDb[QNum++]);
            if(QNum>=topicCDb.length){
                next.setText("題目已全作答完畢");
                next.setEnabled(false);
            }
        }
    };

    View.OnClickListener audioClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mic.setEnabled(false);
            String s = topicE.getText().toString();
//            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
            mic.setEnabled(true);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

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