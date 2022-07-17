package com.example.tts_test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    ImageView mic;
    EditText text;
    Button sw;
    private static final int RECOGNIZER_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("語音轉文字");

        mic = findViewById(R.id.mic);
        text = findViewById(R.id.text);
        sw = findViewById(R.id.switchBut);

        //speech to text but
        mic.setOnClickListener(micClick);
        sw.setOnClickListener(swclick);

    }

    public void onInit(int status){

    }

    View.OnClickListener micClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("MainActivity", "按");
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
        }
    };

    View.OnClickListener swclick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent i = new Intent();
            i.setClass(MainActivity.this, MainActivity2.class);
            startActivity(i);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if( requestCode==RECOGNIZER_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            text.setText(matches.get(0).toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}