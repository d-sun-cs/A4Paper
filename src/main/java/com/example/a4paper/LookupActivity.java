package com.example.a4paper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LookupActivity extends AppCompatActivity {

    DBOpenHelper dbOpenHelper = new DBOpenHelper(LookupActivity.this, "notebook.db", null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);

        ListView listView = (ListView) findViewById(R.id.lookup_listView);

        Cursor cursor = dbOpenHelper.getReadableDatabase().query("notebook",null
                ,null,null,null,null,null);
        ArrayList<Map<String, String>> resultList = new ArrayList<Map<String, String>>();   //创建ArrayList对象，用于保存查询出的结果
        while (cursor.moveToNext()) {  // 遍历Cursor结果集
            Map<String, String> map = new HashMap<>();  // 将结果集中的数据存入HashMap中
            // 取出查询记录中第2列、第3列的值
            map.put("word", cursor.getString(1));
            map.put("interpret", cursor.getString(2));
            resultList.add(map);                        //将查询出的数据存入ArrayList中
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(LookupActivity.this, resultList,
                R.layout.fragment_one_item,
                new String[]{"word", "interpret"}, new int[]{
                R.id.item_word, R.id.item_detail});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                Toast.makeText(LookupActivity.this, map.get("word").toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
