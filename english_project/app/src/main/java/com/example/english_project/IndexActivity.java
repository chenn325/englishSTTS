package com.example.english_project;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.english_project.net.RequestHandler;
import com.example.english_project.net.SharedPrefManager;
import com.example.english_project.net.URLs;
import com.example.english_project.net.User;
import com.example.english_project.student.StudentMainActivity;
import com.example.english_project.teacher.TeacherMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class IndexActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    TextView textViewHint;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        //if the user is already logged in we will directly start the profile activity
        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            String identity = SharedPrefManager.getIdentity();
            if(identity.equals("teacher")){
                startActivity(new Intent(this, TeacherMainActivity.class));
            }
            else{
                startActivity(new Intent(this, StudentMainActivity.class));
            }
            return;
        }

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewHint = (TextView) findViewById(R.id.textViewHint);

        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registerUser();
            }
        });

        findViewById(R.id.textViewLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on login
                //we will open the login screen
                finish();
                startActivity(new Intent(IndexActivity.this, LoginActivity.class));
            }
        });
    }

    private void registerUser(){
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        //first we will do the validations
        if(TextUtils.isEmpty(username)){
            editTextUsername.setError("Please enter username");
            editTextUsername.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            editTextPassword.setError("Enter a password");
            editTextPassword.requestFocus();
            return;
        }

        //if it passes all the validations

        class RegisterUser extends AsyncTask<Void, Void, String> {
            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids){
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                //display the progress bar while user registers on the server
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try{

                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Log.d("IndexActivity",obj.getString("message"));
                        textViewHint.setText(obj.getString("message"));
                        //getting the user from response
                        JSONObject userJSON = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJSON.getInt("id"),
                                userJSON.getString("username")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    } else {
                        Log.d("IndexActivity",obj.getString("message"));
                        textViewHint.setText(obj.getString("message"));
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    Log.d("IndexActivity","Index json error");
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，
     * 来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}