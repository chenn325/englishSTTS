package com.example.english_project.student;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.HashMap;


public class StudentChoosePartner extends Fragment {
    Button chooseBut1, chooseBut2, chooseBut3, confirm_button;
    ProgressBar progressBar;
    int choose = 0;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_partners,container,false);

        chooseBut1 = (Button) view.findViewById(R.id.chooseBut1);
        chooseBut2 = (Button) view.findViewById(R.id.chooseBut2);
        chooseBut3 = (Button) view.findViewById(R.id.chooseBut3);
        confirm_button = (Button) view.findViewById(R.id.confirm_button);
        progressBar = view.findViewById(R.id.progressBar);
        ImageView imageView = (ImageView)getActivity().findViewById(R.id.ImagePartner);


        chooseBut1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choose!=0){
                    chooseBut2.setText("CHOOSE");
                    chooseBut2.setEnabled(true);
                    chooseBut3.setText("CHOOSE");
                    chooseBut3.setEnabled(true);
                }
                chooseBut1.setText("選擇他!");
                chooseBut1.setEnabled(false);
                choose = 1;

                Log.d("choose", "1");
            }
        });

        chooseBut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choose!=0){
                    chooseBut1.setText("CHOOSE");
                    chooseBut1.setEnabled(true);
                    chooseBut3.setText("CHOOSE");
                    chooseBut3.setEnabled(true);
                }
                chooseBut2.setText("選擇他!");
                chooseBut2.setEnabled(false);
                choose = 2;

                Log.d("choose", "2");
            }
        });

        chooseBut3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choose!=0){
                    chooseBut1.setText("CHOOSE");
                    chooseBut1.setEnabled(true);
                    chooseBut2.setText("CHOOSE");
                    chooseBut2.setEnabled(true);
                }
                chooseBut3.setText("選擇他!");
                chooseBut3.setEnabled(false);
                choose = 3;

                Log.d("choose", "3");
            }
        });


        confirm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                confirm_button.setText("已更改");
                confirm_button.setEnabled(false);
                chooseBut1.setEnabled(false);
                chooseBut2.setEnabled(false);
                chooseBut3.setEnabled(false);
                if(choose==1)
                    imageView.setImageResource(R.drawable.girl1);
                else if(choose==2)
                    imageView.setImageResource(R.drawable.girl2);
                else if(choose==3)
                    imageView.setImageResource(R.drawable.boy3);

                User user = SharedPrefManager.getInstance(getActivity()).getUser();
                changePartner(user.getId(), choose);

            }
        });
        return view;
    }

    private void changePartner(int id, int partner){
        class ChangePartner extends AsyncTask<Void, Void, String> {

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
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Log.d("partner frag", "change success");
                    }
                    else {
                        Toast.makeText(getActivity().getApplicationContext(), "Can't change partner", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("partner frag","change partner json error");
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();

                params.put("id", String.valueOf(id));
                params.put("partner", String.valueOf(partner));

                return requestHandler.sendPostRequest(URLs.URL_CHANGEPARTNER ,params);
            }
        }
        ChangePartner cp = new ChangePartner();
        cp.execute();
    }


}
