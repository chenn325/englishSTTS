package com.example.english_project.study;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.english_project.student.StudentMainActivity;
import com.example.english_project.student.StudentStudy;
import com.example.english_project.R;
import com.example.english_project.net.*;
import com.example.english_project.student.StudentTest;
import com.example.english_project.study.model.MyModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ListenTest extends Fragment implements OnInitListener {

    private ProgressBar progressBar;

    //default
    private int rowNum = 0;
    private int unit = 1, myclass = 301;
    private String category = "listen", type = "vocabulary";
    private String partnerImage;
    private int resID; //紀錄imageId

    private TextToSpeech tts;
    private String myText;
    private Button sendBtn, backBtn;
    private EditText editText;
    public int[] answer;
    JSONObject obj;
    private int currentNum = 0; //題號

    List<MyModel> myModelList = new ArrayList<MyModel>();
    RecyclerView recyclerView;
    ListenAdapter listenAdapter;
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
        myclass = user.getMyclass();
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
            default:
                partnerImage = "girl1";
                break;
        }
        resID = getResources().getIdentifier(partnerImage , "drawable", getActivity().getPackageName());
        GetText();

        sendBtn = (Button) view.findViewById(R.id.send_btn);
        backBtn = (Button) view.findViewById(R.id.backBtn);
        editText = (EditText) view.findViewById(R.id.content_input);

        //receiveMessage2("listen question");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sendBtn.getText().equals("EXIT")){
                    StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                    studentMainActivity.changeFragment(new StudentStudy());
                }
                String msg = editText.getText().toString();
                if(!msg.isEmpty()){
                    sendMessage(msg);
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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentMainActivity studentMainActivity = (StudentMainActivity)getActivity();
                studentMainActivity.changeFragment(new StudentTest());
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);

        return view;
    }

    public void setInfo(int myClass, int Unit , String Category, String Type){
        myclass = myClass;
        unit = Unit;
        category = Category;
        type = Type;
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
                        answer = new int[rowNum];
                        for(int i=0; i<rowNum; i++){
                            answer[0] = 0;
                        }
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

        listenAdapter = new ListenAdapter(getContext(),myModelList,"test");
        recyclerView.setAdapter(listenAdapter);
    }

    void sendMessage(String message){
        MyModel myModel = new MyModel(resID, user.getName(), message, MyModel.SEND);
        myModelList.add(myModel);
        listenAdapter.notifyItemInserted(myModelList.size() - 1);
        recyclerView.scrollToPosition(myModelList.size() - 1);
    }

    //接收
    void receiveMessage(int type, String message){
        MyModel myModel = new MyModel(message, MyModel.RECEIVE);
        if(type==1){
            myModel = new MyModel(resID, "大米", message, MyModel.RECEIVE);
        }
        else if(type==2){
            myModel = new MyModel(message, MyModel.RECEIVE_2);
        }
        else if(type==3){
            myModel = new MyModel(resID, "大米", message, MyModel.RECEIVE_3);
        }
        myModelList.add(myModel);
        Log.d("image", String.valueOf(resID));
        listenAdapter.notifyItemInserted(myModelList.size()-1);
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

    //漸進提示
    public void compare(String myText, String msg) throws JSONException {

        if(msg.equals(myText)){
            receiveMessage(3,"正確!");
            if(currentNum == rowNum){
                receiveMessage(3, "恭喜你完成測驗!");
                //算成績
                receiveMessage(2, "本次成績為: "+String.valueOf(Cal()));
                sendBtn.setText("EXIT");
                for(int i=0; i<rowNum; i++){
                    if(answer[i]!=0){
                        JSONObject t = obj.getJSONObject(String.valueOf(i));
                        String errorText = t.getString("en");
                        UploadError(errorText);
                    }
                }
            }else{
                receiveMessage(2,"next question");
                setText(rowNum);
            }
        }
        else{
            answer[currentNum]++;
            switch(answer[currentNum]){
                case 1://重複錯誤
                    receiveMessage(3,"你的答案之發音為");
                    receiveMessage(1,msg);
                    receiveMessage(2,"再試一次吧!");
                    break;
                case 2://提問引導
                    receiveMessage(3,"這題單字的長度是"+myText.length());
                    receiveMessage(2,"最後你的答案是?");
                    break;
                case 3://明確校正
                    receiveMessage(3,"可惜!最後答案是"+myText);
                    receiveMessage(2,"讓我們進入下一題吧");
                    setText(rowNum);
                    break;
            }
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

    //listen_c分數
    //直接return score
    private int Cal(){

        int score = 0; //總分

        float per_score = 100/rowNum; //一題的分數
        BigDecimal bd = new BigDecimal((double) per_score);
        bd = bd.setScale(2,4); //取後兩位，四捨五入
        per_score = bd.floatValue();
        if(per_score*rowNum<100)
            score += 100 - per_score*rowNum;

        for(int i=0; i<rowNum; i++){
            switch(answer[i]){
                case 0:
                    score += per_score;
                    break;
                case 1:
                    score += per_score*2/3;
                    break;
                case 2:
                    score += per_score/3;
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
                    obj = new JSONObject(s);
                    Log.d("json", "LC");

                    if (!obj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        rowNum = obj.getInt("row");
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
                params.put("type", type);
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