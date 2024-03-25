package com.example.a5502;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import org.osmdroid.config.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.example.a5502.spinner_adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public
class MainActivity extends AppCompatActivity {
    private final String[] options = {"更多功能:", "新增地區", "儲存的地區", "地圖", "語言"};
    private final String[] location = {"台中市","台北市","新竹縣","宜蘭縣","台南市","屏東縣"};
    private final String[] options_en = {"more:","add area","saved area","map","language"};
    private final int[] image = {R.drawable.setting,R.drawable.add,R.drawable.store,R.drawable.map,R.drawable.world};
    private Spinner sp;
    private TextView mx,mn,tem,title;
    private Button btn,nt,bf,weather;
    int now_area = 0,area_total = 1,t = 0;
    boolean bl = false,bl2 = false;
    private MapView mapView;
    private Marker marker;
    String sql_area[] = new String[6];
    private HorizontalScrollView  hv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = findViewById(R.id.spinner2);
        hv = findViewById(R.id.hw);
        mx = findViewById(R.id.max_tem);
        mn = findViewById(R.id.min_tem);
        tem = findViewById(R.id.tem);
        btn = findViewById(R.id.button);
        nt = findViewById(R.id.next_nv);
        bf = findViewById(R.id.before_nv);
        title = findViewById(R.id.textView);
        weather = findViewById(R.id.weather);

        SQLiteDatabase db = openOrCreateDatabase("db3.db", Context.MODE_PRIVATE,null);
        SQLiteDatabase dbt = openOrCreateDatabase("111.db", Context.MODE_PRIVATE,null);
        String create = "CREATE TABLE IF NOT EXISTS table01(_id INTEGER PRIMARY KEY, s1 TEXT, s2 TEXT)";
        String create02 = "CREATE TABLE IF NOT EXISTS wa(_id INTEGER PRIMARY KEY, area TEXT, wth TEXT, fwth TEXT, faqi TEXT, ht TEXT, maxt TEXT, mint TEXT)";
        db.execSQL(create);
        dbt.execSQL(create02);

        Cursor cursor =  db.rawQuery("SELECT * FROM table01", null);
        boolean isEmpty = true;
        if (cursor != null) {
            if (cursor.getCount() > 0) {isEmpty = false;}
            cursor.close();
        }
        if(isEmpty){
            String s = "INSERT INTO table01(s1) VALUES ('" + String.valueOf(location[0]) + "')";
            db.execSQL(s);

            for(int i=1; i<6; i++){
                s = "INSERT INTO table01(s2) VALUES ('" + String.valueOf(location[i]) + "')";
                db.execSQL(s);
            }
        }

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM table01 WHERE s1 IS NOT NULL",null); //計算s1內一共有幾行(用COUNT(*))
        if(c != null && c.moveToFirst()){ //判斷c是否為空，並將結果(c)移動到結果集的第一項
            area_total = c.getInt(0); //獲取結果集的第一項
            c.close(); //關閉c，釋放資源
        }
        nt.setBackgroundResource(R.drawable.next_no);
        bf.setBackgroundResource(R.drawable.before_no);


        if(t == area_total-1) nt.setBackgroundResource(R.drawable.next_no);
        if(t < area_total-1) nt.setBackgroundResource(R.drawable.next);

        //db.execSQL("DELETE FROM table01");

        spinner_adapter adapter = new spinner_adapter(MainActivity.this, R.layout.spinner, options, image);
        sp.setAdapter(adapter);
        Locale locale = Locale.getDefault(); //取得目前的語言
        String now_lan = locale.getLanguage(); //把取道的語言轉為字串(英文會變成en)
        if(now_lan.equals("en")) adapter.updateData(options_en,image); //判斷語言，更新spinner的內容
        else adapter.updateData(options,image);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    add();
                }
                else if(position == 2){
                    saved();
                }
                else if(position == 3){
                    map_show();
                }
                else if(position == 4){
                    lan(); //切換語言介面
                    //Toast.makeText(MainActivity.this,ch + "" ,Toast.LENGTH_SHORT).show();
                }
                sp.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                show_data_02();
            }
        });

        nt.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {//t 目前在的位置 //area_total sql共有幾個東西
                if(t+1 <= area_total-1){
                    t+=1;
                }
                if(t == area_total-1) nt.setBackgroundResource(R.drawable.next_no);
                else if(t < area_total-1) nt.setBackgroundResource(R.drawable.next);
                if(t > 0) bf.setBackgroundResource(R.drawable.before);
                show_data();
            }
        });

        bf.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                if(t-1 >= 0){
                    t-=1;
                }
                if(t == 0) bf.setBackgroundResource(R.drawable.before_no);
                else if(t > 0) bf.setBackgroundResource(R.drawable.before);
                if(t < area_total-1) nt.setBackgroundResource(R.drawable.next);
                show_data();
            }
        });
        show_data();
    }

    public void add(){ //新增地區
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this); //LayoutInflater可以動態加載布局資源，MainActivity是被加載的對象
        final View v = inflater.inflate(R.layout.saved_area,null); //將要加載的對象轉成View，以便於使用
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true); //可以透過點擊對話框外區域離開此區域
        builder.setView(v); //設定好要顯示的介面
        AlertDialog alertDialog = builder.show(); //創建元件?
        alertDialog.show(); //顯示
        ListView lst = v.findViewById(R.id.ListView01);
        Button btn = v.findViewById(R.id.exit);

        try { //程式開啟時，檢查sql內有沒有資料，有的話則顯示內容
            SQLiteDatabase db = openOrCreateDatabase("db3.db",Context.MODE_PRIVATE,null); //開啟sql
            Cursor c = db.rawQuery("SELECT * FROM table01 WHERE s2 IS NOT NULL",null);
            if(c != null && c.getCount() > 0){ //如果裡面有資料的話就顯示sql的資料
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                        c,
                        new String[]{"s2"},
                        new int[]{android.R.id.text1}
                );
                lst.setAdapter(adapter);
            }
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"error",Toast.LENGTH_SHORT).show();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                alertDialog.dismiss(); //退出AlertDialog
            }
        });

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public
            void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    SQLiteDatabase db = openOrCreateDatabase("db3.db",Context.MODE_PRIVATE,null); //開啟sql
                    Cursor c = db.rawQuery("SELECT * FROM table01 WHERE s2 IS NOT NULL",null);
                    c.moveToPosition(position); //將游標移動到position的位置(就是你點的位置)
                    @SuppressLint("Range") String text = c.getString(c.getColumnIndex("s2")); //取得c中指定列名為 s2 的資料
                    //Range 範圍相關的紅色警告
                    db.execSQL("INSERT INTO table01(s1) values('" + text + "')"); //新增資料到s1(已新增的地區)資料庫
                    long temp = db.delete("table01","s2=?",new String[]{text}); // <-- new String[]{text} 不太懂，但直接text會錯誤
                    //                     table名稱(要用"")      要刪除的(s2是列名，?代表下一個參數會提供值)
                    //用temp存delete的回傳值

                    Toast.makeText(MainActivity.this,"已新增",Toast.LENGTH_SHORT).show();
                    // area_total++;
                    area_total++;
                    t = 0;

                    if(t < area_total) nt.setBackgroundResource(R.drawable.next);
                    if(t == 0) bf.setBackgroundResource(R.drawable.before_no);

                    show_data();
                    alertDialog.dismiss(); //退出AlertDialog
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"error about add",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saved(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this); //LayoutInflater可以動態加載布局資源，MainActivity是被加載的對象
        final View v = layoutInflater.inflate(R.layout.saved_area,null); //將要加載的對象轉成View，以便於使用
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true); //可以透過點擊對話框外區域離開此區域
        builder.setView(v); //設定好要顯示的介面
        AlertDialog alertDialog = builder.show();
        alertDialog.show();
        ListView lst = v.findViewById(R.id.ListView01);
        Button btn = v.findViewById(R.id.exit);

        try{ //程式開啟時，檢查sql內有沒有資料，有的話則顯示內容
            SQLiteDatabase db = openOrCreateDatabase("db3.db",Context.MODE_PRIVATE,null);
            Cursor c = db.rawQuery("SELECT * FROM table01 WHERE s1 IS NOT NULL",null);
            if(c != null && c.getCount() > 0){ //如果裡面有資料的話就顯示sql的資料
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                        c,new String[]{"s1"},new int[]{android.R.id.text1});
                lst.setAdapter(adapter);
            }
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"error02",Toast.LENGTH_SHORT).show();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //要記得不是setOnLongClickListener
            @Override
            public
            boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    SQLiteDatabase db = openOrCreateDatabase("db3.db",Context.MODE_PRIVATE,null);
                    Cursor c = db.rawQuery("SELECT * FROM table01 WHERE s1 IS NOT NULL",null);

                    if(c != null && c.getCount() > 1){ //記得加條件，讓避免儲存的地區最少要有一個
                        c.moveToPosition(position); //將游標移動到position的位置(就是你點的位置)
                        @SuppressLint("Range") String text = c.getString(c.getColumnIndex("s1")); //取得c中指定列名為 s2 的資料
                        db.execSQL("INSERT INTO table01(s2) values('" + text + "')");
                        long temp = db.delete("table01","s1=?",new String[]{text});
                        //                     table名稱(要用"")      要刪除的(s2是列名，?代表下一個參數會提供值)
                        //用temp存delete的回傳值

                        Toast.makeText(MainActivity.this,"已刪除",Toast.LENGTH_SHORT).show();
                        //  area_total--;
                        area_total--;
                        t = 0;

                        if(t == 0) bf.setBackgroundResource(R.drawable.before_no);
                        if(t == area_total-1) nt.setBackgroundResource(R.drawable.next_no);
                        else if(t < area_total) nt.setBackgroundResource(R.drawable.next);

                        show_data();
                        alertDialog.dismiss();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"至少要留有一項",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"error03",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        /*lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public
            void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase dbt = openOrCreateDatabase("db3.db",Context.MODE_PRIVATE,null);
                Cursor c = dbt.rawQuery("SELECT * FROM table01 WHERE s1 IS NOT NULL",null);
                if(c != null && c.getCount() > 1){
                    c.moveToPosition(position);
                    @SuppressLint("Range") String text = c.getString(c.getColumnIndex("s1"));

                   for(int i=0; i<6; i++){
                        if(text.equals(location[i])){
                            now_area = i;
                            Toast.makeText(MainActivity.this,text + " ",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                else{ Toast.makeText(MainActivity.this,"error04",Toast.LENGTH_SHORT).show(); }

                //now_area = (int)id;

                show_data();
                alertDialog.dismiss();
            }
        });*/
    }

    public void map_show(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this); //LayoutInflater可以動態加載布局資源，MainActivity是被加載的對象
        final View v = layoutInflater.inflate(R.layout.map_weather,null); //將要加載的對象轉成View，以便於使用
        //R.layout.map_weather是指定的layout文件
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true); //可以透過點擊對話框外區域離開此區域
        builder.setView(v); //設定好要顯示的介面
        AlertDialog alertDialog = builder.show();
        alertDialog.show();
        String s = "",s2 = "";
        double x = 0,y = 0;
        Configuration.getInstance().setUserAgentValue(getPackageName());

        mapView = v.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(9.0);
        mapView.getController().setCenter(new GeoPoint(24.40, 121.10));

        Drawable temp = getResources().getDrawable(R.drawable.cloud);
        for(int i=0; i<area_total; i++){
            if(sql_area[i].equals("台北市")){
                bl2 = true; s = "台北市"; s2 = "多雲";
                temp = getResources().getDrawable(R.drawable.cloud);
                x = 25.09108; y = 121.5598;
            }
            else if(sql_area[i].equals("台中市")){
                bl2 = true; s = "台中市"; s2 = "晴天";
                temp = getResources().getDrawable(R.drawable.sun);
                x = 24.23321; y = 120.9417;
            }
            else if(sql_area[i].equals("新竹縣")){
                bl2 = true; s = "新竹縣"; s2 = "陰時多雲";
                temp = getResources().getDrawable(R.drawable.cloud_02);
                x = 24.80395; y = 121.1252;
            }
            else if(sql_area[i].equals("宜蘭縣")){
                bl2 = true; s = "宜蘭縣"; s2 = "多雲陣雨";
                temp = getResources().getDrawable(R.drawable.rain);
                x = 24.69295; y = 121.7195;
            }
            else if(sql_area[i].equals("屏東縣")){
                bl2 = true; s = "屏東縣"; s2 = "晴時多雲";
                temp = getResources().getDrawable(R.drawable.cloud_02);
                x = 22.54951; y = 120.62;
            }
            else if(sql_area[i].equals("台南市")){
                bl2 = true; s = "台南市"; s2 = "晴天";
                temp = getResources().getDrawable(R.drawable.sun);
                x = 23.1417; y = 120.2513;
            }

            if(bl2){
                GeoPoint point = new GeoPoint(x, y);
                //GeoPoint是osmdroid類的內容，用於表示地圖地理座標
                marker = new Marker(mapView); //創建一個標記對象，標記在mapView內
                marker.setPosition(point); //將要標記的位置設置到之前創建的地理座標點，也就是point
                marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM); //設置標記點擊後標點會出現在畫面的哪裡
                //Marker.ANCHOR_CENTER 設置為水平中心
                marker.setTitle(s); //設定點擊後出現文字框的標題
                marker.setSnippet(s2); //設定點擊後出現文字框的內容

                marker.setIcon(temp);

                mapView.getOverlays().add(marker);

                mapView.invalidate();
                //Toast.makeText(MainActivity.this,"aa",Toast.LENGTH_SHORT).show();
                bl2 = false;
            }
        }
    }

    @SuppressLint({"SetTextI18n"})
    public void show_data(){
        LinearLayout lr = findViewById(R.id.lh);
        lr.removeAllViews();
        hv.setScrollX(0);

        raw();

        for(int i=0; i<6; i++){
            if(location[i].equals(sql_area[t])){ //t是箭頭目前指向資料庫的位置
                now_area = i;
                break;
            }
        }

        SQLiteDatabase dbt = openOrCreateDatabase("111.db",Context.MODE_PRIVATE,null);
        Cursor c = dbt.rawQuery("SELECT * FROM wa LIMIT 1 OFFSET " + now_area,null);
        //Cursor c = dbt.rawQuery("SELECT * FROM wa WHERE ht IS NOT NULL",null);

        if(c != null && c.moveToFirst()){ //跟之前一樣
            @SuppressLint("Range") String str[] = (c.getString(c.getColumnIndex("ht"))).split(", ");
            @SuppressLint("Range") String str2[] = (c.getString(c.getColumnIndex("wth"))).split(", ");
            @SuppressLint("Range") String s = (c.getString(c.getColumnIndex("area")));
            c.close();
            int max = Integer.MIN_VALUE,min = Integer.MAX_VALUE;

            for(int i=0; i<str.length; i++){
                max = Math.max(max,Integer.parseInt(str[i]));
                min = Math.min(min,Integer.parseInt(str[i]));
            }
            mx.setText(max + "°");
            mn.setText(" / " + min + "°");
            tem.setText(" " + String.valueOf(str[0]) + "°");
            title.setText(s);
            //weather.setText(String.valueOf(str2[0]));
            switch(str2[0]){
                case "多雲":
                    weather.setBackgroundResource(R.drawable.cloud);
                    break;
                case "陰時多雲":
                    weather.setBackgroundResource(R.drawable.cloud_02);
                    break;
                case "晴時多雲":
                    weather.setBackgroundResource(R.drawable.cloud_02);
                    break;
                case "多雲陣雨":
                    weather.setBackgroundResource(R.drawable.rain);
                    break;
                case "晴天":
                    weather.setBackgroundResource(R.drawable.sun);
                    break;
                case "暴風雪":
                    weather.setBackgroundResource(R.drawable.snow);
                    break;
                default:
                    Toast.makeText(MainActivity.this,"error055",Toast.LENGTH_SHORT).show();
                    break;
            }

            for(int i=0; i<24; i++){
                TextView temp = new TextView(this);
                temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT));

                if(i == 0) temp.setText("現在\n" + str[i] + "°");
                else{temp.setText(String.format("%02d", i) + ":00\n" + str[i] + "°"); }

                temp.setGravity(Gravity.CENTER);
                temp.setTextColor(getResources().getColor(R.color.white));
                temp.setTextSize(20);
                temp.setPadding(30,0,30,0);
                temp.setId(getResources().getIdentifier("h" + i, "id", getPackageName()));
                lr.addView(temp);
            }
        }
        else{
            c.close();
        }
    }

    public void show_data_02(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View v = layoutInflater.inflate(R.layout.tendays_weather,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setView(v);
        AlertDialog dialog = builder.show();
        dialog.show();

        Button btn = v.findViewById(R.id.back);
        HorizontalScrollView hs = v.findViewById(R.id.hsv);
        LinearLayout l = v.findViewById(R.id.lt);
        l.removeAllViews();
        hs.setScrollX(0);

        SQLiteDatabase dbt = openOrCreateDatabase("111.db",Context.MODE_PRIVATE,null);
        Cursor c = dbt.rawQuery("SELECT * FROM wa LIMIT 1 OFFSET " + now_area,null);

        if(c != null && c.moveToFirst()){
            @SuppressLint("Range") String s1[] = c.getString(c.getColumnIndex("fwth")).split(", ");
            @SuppressLint("Range") String s2[] = c.getString(c.getColumnIndex("faqi")).split(", ");
            @SuppressLint("Range") String s3[] = c.getString(c.getColumnIndex("maxt")).split(", ");
            @SuppressLint("Range") String s4[] = c.getString(c.getColumnIndex("mint")).split(", ");
            c.close();

            for(int i=0; i<10; i++){
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT));

                if(i == 0) textView.setText("現在\n" + s1[i] + "\n空氣指標\n" + s2[i] + "\n最高溫\n" + s3[i] + "\n最低溫\n" + s4[i]);
                else textView.setText(i + "天後\n" + s1[i] + "\n空氣指標\n" + s2[i] + "\n最高溫\n" + s3[i] + "\n最低溫\n" + s4[i]);

                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(20);
                textView.setPadding(30,0,30,0);
                textView.setId(getResources().getIdentifier("h" + i, "id", getPackageName()));
                l.addView(textView);
            }
        }
        else c.close();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @SuppressLint("Range")
    public void raw(){
        SQLiteDatabase dbt = openOrCreateDatabase("db3.db",Context.MODE_PRIVATE,null);
        Cursor c = dbt.rawQuery("SELECT * FROM table01 WHERE s1 IS NOT NULL",null);
        if(c != null && c.getCount() > 0){
            for(int i=0; i<area_total; i++){
                c.moveToPosition(i);

                try{
                    sql_area[i] = c.getString(c.getColumnIndex("s1"));
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"error055",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{ c.close();}
    }

    public void lan(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View v = layoutInflater.inflate(R.layout.language_change,null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setView(v);
        AlertDialog dialog = builder.show();
        dialog.show();

        Button en = v.findViewById(R.id.en),zh = v.findViewById(R.id.zh),ex = v.findViewById(R.id.exit);
        en.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                Locale locale = new Locale("en"); //設定語言
                Locale.setDefault(locale); //設置當前語言
                android.content.res.Configuration androidConfig = getResources().getConfiguration(); //用getResources()獲取當前設備的配置訊息，在用getConfiguration()返回一個Configuratio
                //下一行需要使用到setLocale()功能，而此功能又需要 import android.content.res.Configuration;
                //但地圖需要用到的 import org.osmdroid.config.Configuration; 無法與 import android.content.res.Configuration; 同時宣告
                //所以在這邊直接宣告一個android.content.res.Configuration類型的物件androidConfig，這樣就可以使用 import android.content.res.Configuration; 內的函式
                androidConfig.setLocale(locale); //與config.locale = en;差不多
                getResources().updateConfiguration(androidConfig,getResources().getDisplayMetrics());//在程式能夠運行的情況下更換語言
                //getResources() 用來獲取當前應用程序的資源，例如布局文件
                //updateConfiguration() 更新應用程序的訊息
                //getDisplayMetrics() 回傳一個DisplayMetrics，確保更新後能夠正確適配當前設備
                recreate(); //銷毀當前活動並重新創建(會重新啟動生命週期，也會重新回到onCreate)，可用於刷新介面、更新配置
                //若給全域變數增加static靜態成員，則不會在重新創建，值也不會改變
                dialog.dismiss();
            }
        });
        zh.setOnClickListener(new View.OnClickListener() { //跟en的差不多
            @Override
            public
            void onClick(View v) {
                Locale locale = new Locale("zh","TW");
                Locale.setDefault(locale);
                android.content.res.Configuration androidConfig = getResources().getConfiguration();
                androidConfig.setLocale(locale);
                getResources().updateConfiguration(androidConfig,getResources().getDisplayMetrics());

                recreate();
                dialog.dismiss();
            }
        });
        ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}