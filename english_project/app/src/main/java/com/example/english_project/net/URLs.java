package com.example.english_project.net;

public class URLs {
    private static final String ROOT_URL = "http://172.20.10.2/englishSTTS/Api.php?apicall=";
//    private static final String ROOT_URL = "http://192.168.137.1/englishSTTS/Api.php?apicall=";

    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_STUDY = ROOT_URL + "study";
    public static final String URL_ADD = ROOT_URL + "addTextbook";
    public static final String URL_HISTORY = ROOT_URL + "readStudentHistory";
    public static final String URL_DELETE = ROOT_URL + "deleteTextbook";
    public static final String URL_CHANGEDATE = ROOT_URL + "changeDate";
    public static final String URL_CHANGEPARTNER = ROOT_URL + "changePartner";
    public static final String URL_TESTSET = ROOT_URL + "testSet";
    public static final String URL_SETSCHEDULE = ROOT_URL + "setSchedule"; //study學習進度

    public static final String URL_ListenLearning = ROOT_URL + "ListenLearning";
}