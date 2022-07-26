package com.example.sql_test;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class fra3 extends Fragment {

    Button but;
    TableLayout tl;

    String result = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fra3, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        but = (Button) getView().findViewById(R.id.but);
        tl = (TableLayout) getView().findViewById(R.id.tl);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(mutiThread);
                thread.start();
            }
        });
    }

    // 建立一個執行緒執行的事件取得網路資料
    // Android 有規定，連線網際網路的動作都不能再主線程做執行
    // 畢竟如果使用者連上網路結果等太久整個系統流程就卡死了
    private Runnable mutiThread = new Runnable(){
        public void run()
        {
            try {
                Log.d("", "connecting");
                URL url = new URL("http://172.20.10.2/GetData.php");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.connect(); // 開始連線
                Log.d("", "success");

                int responseCode = connection.getResponseCode();
                // 建立取得回應的物件
                if(responseCode == HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream = connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        box += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    result = box; // 把存放用字串放到全域變數
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        setData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void setData() throws JSONException {
        tl.setStretchAllColumns(true);
        TableLayout.LayoutParams row_layout = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams view_layout = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        JSONArray jsa = new JSONArray(result);

        for(int i=0; i<jsa.length(); i++){
            JSONObject jso = jsa.getJSONObject(i);
            TableRow tr = new TableRow(getContext());
            tr.setLayoutParams(row_layout);
            tr.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView no = new TextView(getContext());
            no.setText(jso.getString("no"));
            no.setLayoutParams(view_layout);

            TextView acc = new TextView(getContext());
            acc.setText(jso.getString("name"));
            acc.setLayoutParams(view_layout);

            TextView pass = new TextView(getContext());
            pass.setText(jso.getString("password"));
            pass.setLayoutParams(view_layout);

            tr.addView(no);
            tr.addView(acc);
            tr.addView(pass);
            tl.addView(tr);
        }
    }

}