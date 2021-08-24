package com.example.a4paper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CET6Activity extends AppCompatActivity {

    private DBOpenHelper dbOpenHelper;   //定义DBOpenHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cet_6_activity);
        //创建DBOpenHelper对象,指定名称、版本号并保存在databases目录下
        dbOpenHelper = new DBOpenHelper(CET6Activity.this, "dict.db", null, 1);

        final ListView listView = (ListView) findViewById(R.id.result_listView6);  //获取显示结果的ListView
        final EditText etSearch = (EditText) findViewById(R.id.search_et6);          //获取查询内容的编辑框
        Button btnSearch = (Button) findViewById(R.id.search_btn6);     //获取查询按钮
        Button btn_add = (Button) findViewById(R.id.btn_add6);                    //获取跳转添加生词界面的按钮
        Button btn_back = (Button) findViewById(R.id.btn_cet6_back);  //获取返回词书界面的按钮
        btn_add.setOnClickListener(new View.OnClickListener() {   //单击添加生词按钮，实现跳转到添加生词的界面
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CET6Activity.this, AddActivity.class);  //通过Intent跳转添加生词界面
                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {  //单击查询按钮，实现查询词库中的单词
            @Override
            public void onClick(View v) {

                String key = etSearch.getText().toString();  //获取要查询的单词
                //查询单词
                Cursor cursor=dbOpenHelper.getReadableDatabase().query("dict",null
                        ,"word = ?",new String[]{key},null,null,null);
                ArrayList<Map<String, String>> resultList = new ArrayList<Map<String, String>>();   //创建ArrayList对象，用于保存查询出的结果
                while (cursor.moveToNext()) {  // 遍历Cursor结果集
                    Map<String, String> map = new HashMap<>();  // 将结果集中的数据存入HashMap中
                    // 取出查询记录中第2列、第3列的值
                    map.put("word", cursor.getString(1));
                    map.put("interpret", cursor.getString(2));
                    resultList.add(map);                        //将查询出的数据存入ArrayList中
                }

                if (resultList == null || resultList.size() == 0) {  //如果数据库中没有数据
                    // 显示提示信息，没有相关记录
// 显示提示信息，没有相关记录
                    ArrayList<Map<String, String>> failList = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<>();  // 将结果集中的数据存入HashMap中
                    // 取出查询记录中第2列、第3列的值
                    map.put("text", "抱歉未查询到相关单词");
                    failList.add(map);                        //将查询出的数据存入ArrayList中
                    SimpleAdapter simpleAdapter = new SimpleAdapter(CET6Activity.this, failList,
                            R.layout.result_fail,
                            new String[]{"text"}, new int[]{
                            R.id.result_fail_text
                    });
                    listView.setAdapter(simpleAdapter);                } else {
                    // 否则将查询的结果显示到ListView列表中
                    SimpleAdapter simpleAdapter = new SimpleAdapter(CET6Activity.this, resultList,
                            R.layout.result_main,
                            new String[]{"word", "interpret"}, new int[]{
                            R.id.result_word, R.id.result_interpret});
                    listView.setAdapter(simpleAdapter);
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击返回按钮，返回四六级词书选择界面
                Intent intent = new Intent(CET6Activity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {  //实现退出应用时，关闭数据库连接
        super.onDestroy();
        if (dbOpenHelper != null) {   //如果数据库不为空时
            dbOpenHelper.close();     //关闭数据库连接
        }
    }
}
