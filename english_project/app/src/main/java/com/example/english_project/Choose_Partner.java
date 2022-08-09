package com.example.english_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class Choose_Partner extends Fragment {
    Button chooseBut1, chooseBut2, confirm_button;
    int choose = 0;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_partners,container,false);

        chooseBut1 = (Button) view.findViewById(R.id.chooseBut1);
        chooseBut2 = (Button) view.findViewById(R.id.chooseBut2);
        confirm_button = (Button) view.findViewById(R.id.confirm_button);
        ImageView imageView = (ImageView)getActivity().findViewById(R.id.ImagePartner);

        chooseBut1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choose!=0){
                    chooseBut2.setText("CHOOSE");
                    chooseBut2.setEnabled(true);
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
                }
                chooseBut2.setText("選擇他!");
                chooseBut2.setEnabled(false);
                choose = 2;

                Log.d("choose", "2");
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                confirm_button.setText("已更改");
                confirm_button.setEnabled(false);
                chooseBut1.setEnabled(false);
                chooseBut2.setEnabled(false);
                if(choose==1)
                    imageView.setImageResource(R.drawable.ic_baseline_emoji_people_24);
                else if(choose==2)
                    imageView.setImageResource(R.drawable.ic_baseline_emoji_people2_24);
            }
        });
        return view;
    }
}
