package com.example.english_project.study;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.english_project.R;
import com.example.english_project.study.model.MyModel;

import java.io.BufferedReader;
import java.util.List;
import java.util.Locale;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    //存放數據
    List<MyModel> myModelList;
    static private Context context;
    TextToSpeech tts;

    public MyAdapter(Context context, List<MyModel> datalist){
        this.context = context;
        this.myModelList = datalist;
    }


    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //布局加載器
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        tts = new TextToSpeech(MyAdapter.context, new TextToSpeech.OnInitListener() {
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
        return new ViewHolder(view);
    }


    //位置對應的數據與holder進行綁定
    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        MyModel myModel = myModelList.get(position);
        holder.itemView.setTag(position);
        if(myModel.getType() == MyModel.SEND){
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftOnlyContent.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightNameTextView.setText(myModel.getName());
            holder.rightContentTextView.setText(myModel.getContent());
            holder.rightImageView.setImageResource(myModel.getImgId());
        }
        else if(myModel.getType() == MyModel.RECEIVE){
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftOnlyContent.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftNameTextView.setText(myModel.getName());
            holder.leftContentTextView.setText(myModel.getContent());
            holder.leftImageView.setImageResource(myModel.getImgId());
            holder.leftPlay.setVisibility(View.VISIBLE);
        }
        else if(myModel.getType() == MyModel.RECEIVE_2){
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.leftOnlyContent.setVisibility(View.VISIBLE);
            holder.leftText.setText(myModel.getContent());
        }
        else if(myModel.getType() == MyModel.RECEIVE_3){
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftOnlyContent.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftNameTextView.setText(myModel.getName());
            holder.leftContentTextView.setText(myModel.getContent());
            holder.leftImageView.setImageResource(myModel.getImgId());
            holder.leftPlay.setVisibility(View.GONE);
        }



        holder.leftPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = holder.leftContentTextView.getText().toString();
                tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                Log.d("tts",s+" success");
            }
        });
    }

    //數據長度
    @Override
    public int getItemCount() {
        return myModelList.size();
    }


    //緩存頁面布局
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView leftImageView;
        ImageView leftPlay;
        TextView leftNameTextView;
        TextView leftContentTextView;
        LinearLayout leftLayout;

        ImageView rightImageView;
        TextView rightNameTextView;
        TextView rightContentTextView;
        LinearLayout rightLayout;

        LinearLayout leftOnlyContent;
        TextView leftText;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            leftImageView = (ImageView) itemView.findViewById(R.id.left_image);
            leftPlay = (ImageView) itemView.findViewById(R.id.left_play);
            leftNameTextView = (TextView) itemView.findViewById(R.id.left_name);
            leftContentTextView = (TextView) itemView.findViewById(R.id.left_content);
            leftLayout = (LinearLayout) itemView.findViewById(R.id.left_bubble);

            rightImageView = (ImageView) itemView.findViewById(R.id.right_image);
            rightNameTextView = (TextView) itemView.findViewById(R.id.right_name);
            rightContentTextView = (TextView) itemView.findViewById(R.id.right_content);
            rightLayout = (LinearLayout) itemView.findViewById(R.id.right_bubble);

            leftOnlyContent = (LinearLayout) itemView.findViewById(R.id.left_onlyContent);
            leftText = (TextView) itemView.findViewById(R.id.left_text);

        }
    }

}
