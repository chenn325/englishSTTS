package com.example.stts_project.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stts_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class fra1 extends Fragment {

    Button login;
    EditText acc, pass;
    LinearLayout loginView, mainlayout;

    String result = "";
    String accText = "", passText = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fra1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        login = (Button) getView().findViewById(R.id.login);
        acc = (EditText) getView().findViewById(R.id.account);
        pass = (EditText) getView().findViewById(R.id.password);
        loginView = (LinearLayout) getView().findViewById(R.id.loginView);
        mainlayout = (LinearLayout) getView().findViewById(R.id.mainLayout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accText = acc.getText().toString();
                passText = pass.getText().toString();
                Thread thread = new Thread(loginThread);
                thread.start();
            }
        });
    }

    private Runnable loginThread = new Runnable() {
        @Override
        public void run() {
            try{
                //註解看fra3.java
                Log.d("", "connecting");
                URL url = new URL("http://172.20.10.2/GetData.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.connect();
                Log.d("", "success");
                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    String box = "";
                    String line = null;
                    while((line = bufReader.readLine()) != null) {
                        box += line + "\n";
                    }
                    inputStream.close();
                    result = box;
                }
            }catch (Exception e){
                result = e.toString();
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        testLogin();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void testLogin() throws JSONException{
        JSONArray jsa = new JSONArray(result);
        Boolean test = Boolean.FALSE;
//        Log.d("", accText);
//        Log.d("", passText);
        for(int i=0; i<jsa.length(); i++){
            JSONObject jso = jsa.getJSONObject(i);
//            Log.d("", jso.getString("name"));
//            Log.d("", jso.getString("password"));
            if(accText.equals(jso.getString("name")) && passText.equals(jso.getString("password"))){
                test = Boolean.TRUE;
                break;
            }
        }

        if(test == Boolean.TRUE){   //sucess
            Log.d("", "login sucess!");
            AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
            ad.setMessage("登入成功！");
            ad.setPositiveButton("OK", null);
            ad.show();
            loginView.setVisibility(View.GONE);
            TextView tv = new TextView(getContext());
            tv.setText("您好！用戶 "+accText);
            tv.setTextColor(Color.rgb(0,0,0));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            mainlayout.addView(tv);
        }
        else{
            Log.d("", "login fail!");
            AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
            ad.setMessage("登入失敗！\n帳號或密碼錯誤 請再試一次");
            ad.setPositiveButton("OK", null);
            ad.show();
            acc.setText("");
            pass.setText("");
        }
    }

}