package com.example.a4paper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    /*private List<word_data> wordList= new ArrayList<>();*/
    BottomNavigationView bnView;//底部导航栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        //接收数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String[] strings = null;
        int[] times = null;
        int buttonNum = 0;
        strings = bundle.getStringArray("strings");
        times = bundle.getIntArray("times");
        buttonNum = bundle.getInt("buttonNum");


        //初始化统计表
        List<word_data> wordList = new ArrayList<>();
        for (int i = 0; i < buttonNum; i++) {
            wordList.add(new word_data(strings[i], times[i]));
        }

        //设置适配器
        WordAdapter adapter = new WordAdapter(StatisticsActivity.this, R.layout.word_item, wordList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        /****配置BottomNavigationView****/
        bnView = findViewById(R.id.bottom_nav_view);
        bnView.setSelectedItemId(R.id.tab_three);
        bnView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tab_three:
                    break;
                case R.id.tab_one:
                    Intent intent1 = new Intent(StatisticsActivity.this, MainActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.tab_two:
                    Intent intent2 = new Intent(StatisticsActivity.this, SearchActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.tab_four:
                    View popupView = getLayoutInflater().inflate(R.layout.simple_list_item, null);
                    TextView tv = popupView.findViewById(R.id.paperName);
                    tv.setText("请点击左下角进入\"A4纸\"界面使用再此功能\n    （点击其他位置可以收起该文字提醒）");
                    PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.showAtLocation(StatisticsActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                    break;
            }
            return false;
        });

        TextView textView = findViewById(R.id.remind);
        if (buttonNum == 0) {
            textView.setText("当前页面还没有添加单词呢！\n添加些单词再来吧！");
        }
    }

    /*private void init_word(){
        Bundle bundle = getIntent().getExtras();
        int buttonNum = bundle.getInt("buttonNum");
        for(int i =0; i < buttonNum; i++){
            wordList.add(strings[i]);
        }
    }*/

}