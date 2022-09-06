package com.example.english_project.study.model;

public class MyModel {
    public static final int SEND = 0;
    public static final int RECEIVE = 1;
    public static final int RECEIVE_2 = 2;  //只有文字
    public static final int RECEIVE_3 = 3;  //沒有播放鈕

    private int imgId = 0;
    private String name = "test";
    private String content = "test";


    //收發類型
    private int type = 0;
    public MyModel(int imgId, String name, String content, int type){
        this.imgId = imgId;
        this.name = name;
        this.content = content;
        this.type = type;
    }
    //onlyText
    public MyModel(String content, int type){
        this.content = content;
        this.type = type;
    }

    public int getImgId(){
        return imgId;
    }

    public String getName(){
        return name;
    }

    public String getContent(){
        return content;
    }

    public int getType(){
        return type;
    }
}
