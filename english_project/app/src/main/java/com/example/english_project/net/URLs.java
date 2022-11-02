package com.example.english_project.net;

public class URLs {
    //private static final String ROOT_URL = "http://192.168.0.167/englishSTTS/Api.php?apicall=";
    private static final String ROOT_URL = "http://192.168.137.1/englishSTTS/Api.php?apicall=";

    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_STUDY = ROOT_URL + "study";
    public static final String URL_ADD = ROOT_URL + "addTextbook";
    public static final String URL_HISTORY = ROOT_URL + "readStudentHistory";
    public static final String URL_DELETE = ROOT_URL + "deleteTextbook";
    public static final String URL_CHANGEDATE = ROOT_URL + "changeDate";
    public static final String URL_CHANGEPARTNER = ROOT_URL + "changePartner";
    public static final String URL_TESTSET = ROOT_URL + "testSet";
    public static final String URL_SETNEWTESTBOOK = ROOT_URL + "setNewTextbook";
    public static final String URL_SETSCHEDULE = ROOT_URL + "setSchedule"; //study學習進度
    public static final String URL_SETTODAYTEXT = ROOT_URL + "todayText";
    public static final String URL_HISTORY_LP = ROOT_URL + "history_LP"; //listen_p++
    public static final String URL_HISTORY_LC = ROOT_URL + "history_LC"; //listen_c++
    public static final String URL_UPLOADERROR = ROOT_URL + "uploadError"; //上傳錯題
    public static final String URL_DOWNLOADERROR = ROOT_URL + "downloadError"; //顯示錯題
    public static final String URL_INITSTUDENTHISTORY = ROOT_URL + "initStudentHistory"; //初始化學生學習記錄

    public static final String URL_ListenLearning = ROOT_URL + "ListenLearning";
}