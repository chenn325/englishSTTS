package com.example.english_project.teacher;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TeacherTextbook extends Fragment {

    int TotalQNum, classForSearch;

    ProgressBar progressBar;
    LinearLayout showTextLayout1;
    Button search;
    Spinner SpGrade, SpClass;

    JSONObject TopicObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classForSearch = 301;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_textbook, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        showTextLayout1 = view.findViewById(R.id.showTextbook1);
        search = view.findViewById(R.id.ButSearch);
        SpGrade = view.findViewById(R.id.SPgrade);
        SpClass = view.findViewById(R.id.SPclass);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnClick();
            }
        });
        getTopic();
        return view;
    }

    private void getTopic() {
        class GetTopic extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
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
                    setTopic(TotalQNum);
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
                params.put("classnum", String.valueOf(classForSearch));
                return requestHandler.sendPostRequest(URLs.URL_STUDY, params);
            }
        }

        GetTopic gt = new GetTopic();
        gt.execute();
    }

    private void setTopic(int n) throws JSONException{
        for (int i=0; i<n; i++){
            Log.d("add text", "add");
            JSONObject t = TopicObj.getJSONObject(String.valueOf(i));
            TextView tv = new TextView(getContext());
            tv.setText( (i+1) + ". " + t.getString("en"));
            tv.setTextColor(getResources().getColor(R.color.black));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            showTextLayout1.addView(tv);

        }
    }

    private void searchOnClick(){
        int pg = SpGrade.getSelectedItemPosition() + 3;
        int pc = SpClass.getSelectedItemPosition() + 1;
        classForSearch = pg*100 +pc;

        showTextLayout1.removeAllViewsInLayout();
        getTopic();
    }
}