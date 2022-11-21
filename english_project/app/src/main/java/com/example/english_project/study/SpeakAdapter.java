package com.example.english_project.study;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_project.R;
import com.example.english_project.net.User;
import com.example.english_project.study.model.MyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class SpeakAdapter extends RecyclerView.Adapter {
    public static final int TYPE_TEACHER_TXT = 0, TYPE_TEACHER_TOPIC = 1, TYPE_STUDENT_TXT = 2;

    private List<JSONObject> mData;
    User user;
    String userName;
    int resID;
    static private Context context;

    TextToSpeech tts;

    public SpeakAdapter(List<JSONObject> data, User u, Context c, int r){
        mData = data;
        user = u;
        userName = user.getName();
        context = c;
        resID = r;
    }

    //建立ViewHolder
    class TeacherTxtViewHolder extends RecyclerView.ViewHolder{
        //宣告元件
        private TextView txtItem, nameItem;
        private ImageView stuPT;
        private View imageCon;

        TeacherTxtViewHolder(View itemView){
            super(itemView);
            txtItem = (TextView) itemView.findViewById(R.id.left_txt);
            nameItem = (TextView) itemView.findViewById(R.id.left_name);
            stuPT = (ImageView) itemView.findViewById(R.id.left_image);
            imageCon = (View) itemView.findViewById(R.id.imageCon);
        }
    }

    class TeacherTopicViewHolder extends RecyclerView.ViewHolder{
        //宣告元件
        private TextView txtItem, nameItem;
        private ImageView stuPT, playBut;
        private View imageCon;

        TeacherTopicViewHolder(View itemView){
            super(itemView);
            txtItem = (TextView) itemView.findViewById(R.id.left_txt);
            nameItem = (TextView) itemView.findViewById(R.id.left_name);
            stuPT = (ImageView) itemView.findViewById(R.id.left_image);
            playBut = (ImageView) itemView.findViewById(R.id.sound_play);
            imageCon = (View) itemView.findViewById(R.id.imageCon);
        }
    }

    class StudentTxtViewHolder extends RecyclerView.ViewHolder{
        private TextView txtItem;

        StudentTxtViewHolder(View itemView){
            super(itemView);
            txtItem = (TextView) itemView.findViewById(R.id.right_txt);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        //連結項目佈局檔 teacher_text.xml
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
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
        });
        View view;
        switch(viewType){
            case TYPE_TEACHER_TXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_text, parent, false);
                return new TeacherTxtViewHolder(view);
            case TYPE_TEACHER_TOPIC:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_topictxt, parent, false);
                return new TeacherTopicViewHolder(view);
            case TYPE_STUDENT_TXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_txt, parent, false);
                return new StudentTxtViewHolder(view);
            default:
                Log.d("view holder", "switch error");
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        //設置txtItem要顯示的內容
        JSONObject m = mData.get(position);
        try {
            if(m.getBoolean("type")){
                if(!m.getBoolean("isTopic")){
                    TeacherTxtViewHolder tvh = (TeacherTxtViewHolder) holder;
                    tvh.txtItem.setText(m.getString("text"));
                    if(!m.getBoolean("lastSay")) {
                        tvh.nameItem.setText("大米");
                        tvh.stuPT.setImageResource(resID);
                        tvh.nameItem.setVisibility(View.VISIBLE);
                        tvh.imageCon.setVisibility(View.VISIBLE);
                        Log.d("adp t", "set");
                    }
                    else{
                        tvh.nameItem.setVisibility(View.INVISIBLE);
                        tvh.imageCon.setVisibility(View.INVISIBLE);
                        Log.d("adp t", "unset");
                    }
                }
                else{
                    TeacherTopicViewHolder t = (TeacherTopicViewHolder) holder;
                    t.txtItem.setText(m.getString("text"));
                    if(!m.getBoolean("lastSay")) {
                        t.nameItem.setText("大米");
                        t.stuPT.setImageResource(resID);
                        t.nameItem.setVisibility(View.VISIBLE);
                        t.imageCon.setVisibility(View.VISIBLE);
                        Log.d("adp p", "set");
                    }
                    else{
                        t.nameItem.setVisibility(View.INVISIBLE);
                        t.imageCon.setVisibility(View.INVISIBLE);
                        Log.d("adp p", "unset");
                    }
                    t.playBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String s = null;
                            try {
                                s = m.getString("sound_text");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                            Log.d("tts",s+" success");
                        }
                    });
                }
            }
            else{
                StudentTxtViewHolder svh = (StudentTxtViewHolder) holder;
                svh.txtItem.setText(m.getString("text"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        return mData.size();
    }

    public void addItem(JSONObject m) throws JSONException {
        mData.add(mData.size(), m);
        notifyItemInserted(mData.size());
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject m = mData.get(position);
        try {
            Boolean type = m.getBoolean("type"); //0 t, 1 s
            if(type){
                Boolean isTopic = m.getBoolean("isTopic");
                if(!isTopic){
                    return TYPE_TEACHER_TXT;
                }
                else{
                    return TYPE_TEACHER_TOPIC;
                }
            }
            else{
                return TYPE_STUDENT_TXT;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

}
