package com.example.english_project.student;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.english_project.R;
import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Random;

public class StudentHome extends Fragment {

    private TextView todayText;
    private ProgressBar progressBar;
    private int randomID, rowNum;
    private JSONObject obj;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home,container,false);
        ImageView imagePartner = (ImageView) view.findViewById(R.id.ImagePartner);
        todayText = (TextView) view.findViewById(R.id.todayText);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        randomID = new Random().nextInt(3) + 1;
        GetText();
        //getting the current user
        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        int partner = user.getPartner();
        switch (partner){
            case 1:
                imagePartner.setImageResource(R.drawable.girl1);
                break;
            case 2:
                imagePartner.setImageResource(R.drawable.girl2);
                break;
            case 3:
                imagePartner.setImageResource(R.drawable.boy3);
                break;
        }

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
                    Log.d("json", "get todayText");

                    if (!obj.getBoolean("error")){
                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        rowNum = obj.getInt("row");
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "Can't get todayText", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("TodayText frag","todayText json error1");
                }
                //set Text
                try{
                    setText(rowNum);
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d("TodayText frag","todayText json error2");
                };

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(randomID));
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_SETTODAYTEXT, params);
            }
        }

        getText ul = new getText();
        ul.execute();
    }

    public void setText(int rowNum) throws JSONException {
        if(rowNum > 0) {
            JSONObject t = obj.getJSONObject("text");
            todayText.setText(t.getString("text"));
        }
    }

}
