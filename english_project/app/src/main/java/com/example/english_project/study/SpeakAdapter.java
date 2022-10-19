package com.example.english_project.study;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SpeakAdapter extends RecyclerView.Adapter {
    public static final int TYPE_TEACHER_TXT = 0, TYPE_TEACHER_NEXT = 1, TYPE_STUDENT_TXT = 2;

    private List<JSONObject> mData;
    User user;
    String userName;
    int count = 0;


    public SpeakAdapter(List<JSONObject> data, User u){
        mData = data;
        user = u;
        userName = user.getName();
    }

    //建立ViewHolder
    class TeacherTxtViewHolder extends RecyclerView.ViewHolder{
        //宣告元件
        private TextView txtItem, nameItem;
        private ImageView stuPT;

        TeacherTxtViewHolder(View itemView){
            super(itemView);
            txtItem = (TextView) itemView.findViewById(R.id.left_txt);
            nameItem = (TextView) itemView.findViewById(R.id.left_name);
            stuPT = (ImageView) itemView.findViewById(R.id.left_image);
        }
    }

    class TeacherNextViewHolder extends RecyclerView.ViewHolder{
        private Button butItem;

        TeacherNextViewHolder(View itemView){
            super(itemView);
            butItem = (Button) itemView.findViewById(R.id.butItem);
        }

    }

    class StudentTxtViewHolder extends RecyclerView.ViewHolder{
        private TextView txtItem, nameItem;
        private ImageView stuPT;

        StudentTxtViewHolder(View itemView){
            super(itemView);
            txtItem = (TextView) itemView.findViewById(R.id.right_txt);
            nameItem = (TextView) itemView.findViewById(R.id.right_name);
            stuPT = (ImageView) itemView.findViewById(R.id.right_image);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        //連結項目佈局檔 teacher_text.xml
//        Log.d("", "C " + count++);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_text, parent, false);
        View view;
        switch(viewType){
            case TYPE_TEACHER_TXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_text, parent, false);
                return new TeacherTxtViewHolder(view);
            case TYPE_TEACHER_NEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_next, parent, false);
                return new TeacherNextViewHolder(view);
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
                if(m.getBoolean("isText")){
                    TeacherTxtViewHolder tvh = (TeacherTxtViewHolder) holder;
                    tvh.txtItem.setText(m.getString("text"));
                    tvh.nameItem.setText("大米");
                    tvh.stuPT.setImageResource(R.drawable.ic_baseline_emoji_people2_24);
                }
                else{
                    TeacherNextViewHolder tnvh = (TeacherNextViewHolder) holder;
                    tnvh.butItem.setText("Next topic?");
                }
            }
            else{
                StudentTxtViewHolder svh = (StudentTxtViewHolder) holder;
                svh.txtItem.setText(m.getString("text"));
                svh.nameItem.setText(userName);
                svh.stuPT.setImageResource(R.drawable.ic_baseline_emoji_people_24);
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
//        JSONObject m = new JSONObject();
        //先裝學生（？
//        m.put("type", false);
//        m.put("text", "hello");
        mData.add(mData.size(), m);
        notifyItemInserted(mData.size());
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject m = mData.get(position);
        try {
            Boolean type = m.getBoolean("type"); //0 t, 1 s
            if(type){
                Boolean isText = m.getBoolean("isText");
                if(isText){
                    return TYPE_TEACHER_TXT;
                }
                else{
                    return TYPE_TEACHER_NEXT;
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
