package com.example.english_project.study;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.english_project.R;
import com.example.english_project.net.*;
import com.example.english_project.study.model.MyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ListenLearning extends Fragment implements OnInitListener {

    private ProgressBar progressBar;
    private int rowNum = 0;
    private int unit, myclass;
    private String category, type;
    private TextToSpeech tts;
    private String myText;

    JSONObject obj;
    private int currentNum = 0; //題號

    List<MyModel> myModelList = new ArrayList<MyModel>();
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    View view;
    User user;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listen_learning, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recyclerView);
        initRecycler();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        tts = new TextToSpeech(getContext(),this);
        user = SharedPrefManager.getInstance(getActivity()).getUser();

        //待設定
        myclass = user.getMyclass();
        unit = 1;
        category = "listen";
        type = "vocabulary";
        GetText();

        Button sendBtn = (Button) view.findViewById(R.id.send_btn);
        EditText editText = (EditText) view.findViewById(R.id.content_input);

        //receiveMessage2("listen question");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString();
                if(!msg.isEmpty()){
                    sendMessage(msg);
                    reply(msg);
                    editText.setText("");

                    try {
                        compare(myText, msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Cant be empty! ", Toast.LENGTH_SHORT);
                }
            }
        });

        return view;
    }

    private void GetText(){

        class getText extends AsyncTask<Void, Void, String> {

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
                    obj = new JSONObject(s);
                    Log.d("json", "get ListenText");

                    if (!obj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        rowNum = obj.getInt("row");
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "Can't get ListenText", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ListenLearning frag","ListenLearning json error");
                }
                //set Text
                try{
                    setText(rowNum);
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d("ListenLearning frag","ListenLearning json error");
                };

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("unit", String.valueOf(unit));
                params.put("myclass", String.valueOf(myclass));
                params.put("category", category);
                params.put("type", type);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_ListenLearning, params);
            }
        }

        getText ul = new getText();
        ul.execute();
    }

    private void initRecycler() {
        myModelList.clear();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        myAdapter = new MyAdapter(getContext(),myModelList);
        recyclerView.setAdapter(myAdapter);
    }

    void sendMessage(String message){
        MyModel myModel = new MyModel(R.drawable.ic_baseline_emoji_people_24, user.getName(), message, MyModel.SEND);
        myModelList.add(myModel);
        myAdapter.notifyItemInserted(myModelList.size() - 1);
        recyclerView.scrollToPosition(myModelList.size() - 1);
    }

    void reply(String msg){
        String rMsg="";
        switch(msg){
            case "hello":
                rMsg = "hello! How are you?";
                break;
            case "How old are you?":
                rMsg = "22";
                break;
        }
        if(!rMsg.isEmpty()){
            receiveMessage(3,rMsg);
        }
    }

    void receiveMessage(int type, String message){
        MyModel myModel = myModel = new MyModel(message, MyModel.RECEIVE);;
        if(type==1){
            myModel = new MyModel(currentNum, R.drawable.ic_baseline_emoji_people2_24, "大米", message, MyModel.RECEIVE);
        }
        else if(type==2){
            myModel = new MyModel(message, MyModel.RECEIVE_2);
        }
        else if(type==3){
            myModel = new MyModel(R.drawable.ic_baseline_emoji_people2_24, "大米", message, MyModel.RECEIVE_3);
        }
        myModelList.add(myModel);

        myAdapter.notifyItemInserted(myModelList.size()-1);
        recyclerView.scrollToPosition(myModelList.size()-1);
    }


    public void setText(int rowNum) throws JSONException {
        JSONObject t = obj.getJSONObject(String.valueOf(currentNum));
        myText = t.getString("en");
        receiveMessage(1, t.getString("en"));
        Log.d("text", t.getString("en"));
        if(currentNum < rowNum)
            currentNum++;
    }

    public void compare(String myText, String msg) throws JSONException {
        if(msg.equals(myText)){
            receiveMessage(3,"you are right!");
            receiveMessage(2,"next question");
            Log.d("msg", "correct");
            setText(rowNum);
        }
        else{
            receiveMessage(3,"wrong answer");
        }
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
}