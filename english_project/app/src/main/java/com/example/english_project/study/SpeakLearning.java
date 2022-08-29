package com.example.english_project.study;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.english_project.R;
import com.example.english_project.SpeakAdapter;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;
import com.example.english_project.teacher.TeacherTextbook;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarEntry;

public class SpeakLearning extends Fragment {

    //getting the current user
    User user = SharedPrefManager.getInstance(getActivity()).getUser();
    String myClass = String.valueOf(user.getMyclass());
    //測試用 之後用按鈕送
    String unit = "1";


    private RecyclerView recyclerView;
    private SpeakAdapter adapter;
    private ArrayList<JSONObject> mData = new ArrayList<>();

    private ImageView mic;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speak_learning, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        mic = view.findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject m = new JSONObject();
                    m.put("type", false);
                    m.put("text", "hello");
                    adapter.addItem(m);
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    checkAnswer();
                } catch (JSONException e) {
                    e.printStackTrace();
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
        try {
            JSONObject json = new JSONObject();
            json.put("type", true);
            json.put("isText", true);
            json.put("text", "説hello");
            mData.add(json);
            adapter = new SpeakAdapter(mData);
            //設置adapter給recyclerView
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    public void checkAnswer(){
        //測試錯誤情況
        if(false){
            trueFeedback();
            showNextBut();
        }
        else{
            falseFeedback();
        }
    }

    public void trueFeedback(){
        try {
            JSONObject m = new JSONObject();
            m.put("type", true);
            m.put("isText", true);
            m.put("text", "說得好！");
            adapter.addItem(m);
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showNextBut(){
        try {
            JSONObject m = new JSONObject();
            m.put("type", true);
            m.put("isText", false);
            adapter.addItem(m);
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void falseFeedback(){
        try {
            JSONObject m = new JSONObject();
            m.put("type", true);
            m.put("isText", true);
            m.put("text", "發音不太正確 再試一次吧！");
            adapter.addItem(m);
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void getTopic() {
//        class GetTopic extends AsyncTask<Void, Void, String> {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progressBar.setVisibility(View.VISIBLE);;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                progressBar.setVisibility(View.GONE);
//                String sd="", ed="";
//                try {
//                    TextObj = new JSONObject(s);
//                    if (!TextObj.getBoolean("error")) {
//                        Toast.makeText(getActivity().getApplicationContext(), TextObj.getString("message"), Toast.LENGTH_SHORT).show();
////                      取得題目數量
//                        totalQNum = TextObj.getInt("rownum");
//                        if(!TextObj.getBoolean("dateGetError")){
//                            sd = TextObj.getString("startDate");
//                            ed = TextObj.getString("endDate");
//                        }
//                    } else {
//                        Log.d("get topic", "oh");
//                        Toast.makeText(getActivity().getApplicationContext(), "There are no topic", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.d("study frag","study json error");
//                    return;
//                }
//                //設置題目
//                try{
//                    setTopic();
//                    setDateView(sd, ed);
//                }catch (JSONException e){
//                    e.printStackTrace();
//                    Log.d("study frag","topic json error");
//                };
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                RequestHandler requestHandler = new RequestHandler();
//                HashMap<String, String> params = new HashMap<>();
//                params.put("unit", unit);
//                params.put("class", myClass);
//                params.put("category", category);
//                params.put("type", type);
//
//                return requestHandler.sendPostRequest(URLs.URL_STUDY, params);
//            }
//        }
//
//        GetTopic gt = new GetTopic();
//        gt.execute();
//    }
}