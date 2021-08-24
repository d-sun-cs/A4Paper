package com.example.a4paper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    BottomNavigationView bnView;//底部导航栏
    private DBOpenHelper dbOpenHelper;   //定义DBOpenHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        /****配置BottomNavigationView****/
        bnView = findViewById(R.id.bottom_nav_view);
        bnView.setSelectedItemId(R.id.tab_two);
        bnView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tab_two:break;
                case R.id.tab_one:
                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tab_three:
                    Intent intent3 = new Intent(SearchActivity.this,StatisticsActivity.class);
                    //传输数据
                    Bundle bundle = new Bundle();
                    String[] strings = new String[MainActivity.buttonNum] ;
                    int[] times = new int[MainActivity.buttonNum];
                    for( int i = 0; i < MainActivity.buttonNum ; i++){
                        strings[i] = MainActivity.btn[i].word;
                        times[i] = MainActivity.btn[i].time;
                    }
                    bundle.putStringArray("strings",strings);
                    bundle.putIntArray("times",times);
                    bundle.putInt("buttonNum",MainActivity.buttonNum);
                    intent3.putExtras(bundle);
                    startActivity(intent3);
                    break;
                case R.id.tab_four:
                    View popupView = getLayoutInflater().inflate(R.layout.simple_list_item, null);
                    TextView tv = popupView.findViewById(R.id.paperName);
                    tv.setText("请点击左下角进入\"A4纸\"界面使用再此功能\n    （点击其他位置可以收起该文字提醒）");
                    PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.showAtLocation(SearchActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                    break;
            }
            return false;
        });

        //设置六级和四级的按钮
        ImageButton cet4_btn = (ImageButton) findViewById(R.id.cet4_btn);
        ImageButton cet6_btn = (ImageButton) findViewById(R.id.cet6_btn);

        //设置cet4的按钮
        cet4_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, CET4Activity.class);  //通过Intent跳转添加生词界面
                startActivity(intent);
            }
        });

        //设置cet6的按钮
        cet6_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, CET6Activity.class);  //通过Intent跳转添加生词界面
                startActivity(intent);
            }
        });
    }
}