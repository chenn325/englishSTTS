package com.example.tts_test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.*;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity2 extends Activity implements TextToSpeech.OnInitListener{

    ImageView audio;
    EditText text;
    Button sw;
    TextToSpeech tts;
    private static final int RECOGNIZER_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setTitle("文字轉語音");

        audio = findViewById(R.id.audio);
        text = findViewById(R.id.text);
        sw = findViewById(R.id.switchBut);

        tts = new TextToSpeech(this, this);

        //speech to text but
        audio.setOnClickListener(audioClick);
        sw.setOnClickListener(swclick);

    }

    public void onInit(int status){
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

    View.OnClickListener audioClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String s = text.getText().toString();
//            String s = "hello";
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);

        }
    };

    View.OnClickListener swclick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent i = new Intent();
            i.setClass(MainActivity2.this, MainActivity.class);
            startActivity(i);
        }
    };

}