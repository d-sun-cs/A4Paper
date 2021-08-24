package com.example.a4paper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


/**
 * version 4.0
 */

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    /*********单词按钮*********/
    //<editor-fold desc="单词按钮">
    public static final int MAXBTNNUM = 20;
    public static final int MAXGROUPNUM = 10;
    public static int[] buttonNums = new int[MAXGROUPNUM];
    public static myButton[] btn = new myButton[MAXGROUPNUM * MAXBTNNUM];
    public static int buttonNum;//没什么大作用了，也就是加载数据时当个计数器；其主要作用已经由数组buttonNums替代
    public static List<String> papers = new ArrayList<>();
    public static int nowIndex = 0;//从nowIndex * MAXBTNNUM开始到nowIndex * MAXBTNNUM + buttonNums[index]是当前纸上的按钮
    //</editor-fold>

    /*********界面属性*********/
    //<editor-fold desc="界面属性">
    private View content;//界面的ViewTree
    private int screenWidth, screenHeight;//ViewTree的宽和高
    private boolean hasMeasured = false;//ViewTree是否已被测量过，是为true，否为false
    BottomNavigationView bnView;//底部导航栏
    //</editor-fold>

    /****DrawerLayout****/
    //<editor-fold desc="可拖拽侧边栏">
    private DrawerLayout mDrawerLayout;
    //</editor-fold>

    /****数据库****/
    //<editor-fold desc="数据库">
    private DBOpenHelper dbOpenHelper;   //定义DBOpenHelper
    //</editor-fold>

    /****其他辅助工具****/
    //<editor-fold desc="其他辅助工具">
    Timer timer;//计时器，防止同时按下多个按钮而造成混乱
    TextToSpeech tts;//用于语音播放
    static boolean readChinese = false;

    List<WordItem> words = new ArrayList<>();
    Button deleteWord;
    boolean[] isChecked = new boolean[MAXBTNNUM];

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//我也不知道有啥用
    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /****初始化一些变量****/
        //<editor-fold desc="初始化一些变量">
        dbOpenHelper = new DBOpenHelper(MainActivity.this, "dict.db", null, 1);
        timer = new Timer();
        tts = new TextToSpeech(this, this);
        /**加载papers**/
        //TODO
        SharedPreferences pref2 = getSharedPreferences("buttonNums", MODE_PRIVATE);
        for (int i = 0; i < buttonNums.length; i++) {
            buttonNums[i] = pref2.getInt(String.valueOf(i), 0);
        }
        papers.clear();
        SharedPreferences pref3 = getSharedPreferences("paperName", MODE_PRIVATE);
        for (int i = 0; i < MAXGROUPNUM; i++) {
            String getValue = pref3.getString(String.valueOf(i), "啥也妹有");
            if ("啥也妹有".equals(getValue)) {
                break;
            }
            papers.add(getValue);
        }
        //</editor-fold>

        /****配置ToolBar****/
        //<editor-fold desc="配置顶部栏">
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //</editor-fold>

        /****配置DrawerLayout****/
        //<editor-fold desc="配置可拖拽侧边栏的整体布局">
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //允许通过滑动收回侧边栏
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //禁止通过滑动打开侧边栏（与拖动按钮冲突）
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //布局所有按钮
                /****/
                for (int i = 0; i < MAXGROUPNUM * MAXBTNNUM; i++) {
                    if (btn[i] == null) {
                        continue;
                    }
                    if (btn[i].left == 0 && btn[i].top == 0 && btn[i].right == 0 && btn[i].bottom == 0) {
                        btn[i].layout(btn[i].left = screenWidth - btn[i].getWidth(), btn[i].top = screenHeight - btn[i].getHeight() - 155,
                                btn[i].right = screenWidth, btn[i].bottom = screenHeight - 155);
                        continue;
                    }
                    btn[i].layout(btn[i].left, btn[i].top, btn[i].right, btn[i].bottom);
                }
                for (int i = 0; i < MAXGROUPNUM * MAXBTNNUM; i++) {
                    if (btn[i] == null) {
                        continue;
                    }
                    btn[i].setVisibility(View.INVISIBLE);
                    btn[i].setClickable(false);
                }
                for (int i = nowIndex * MAXBTNNUM; i < nowIndex * MAXBTNNUM + buttonNums[nowIndex]; i++) {
                    btn[i].setVisibility(View.VISIBLE);
                    btn[i].setClickable(true);
                }
            }
        });
        mDrawerLayout.openDrawer(Gravity.LEFT);//让侧边栏默认处于打开状态
        //</editor-fold>

        /*********界面属性的测量与相关设置*********/
        //<editor-fold desc="界面属性的测量与相关设置">
        content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);//获取界面的ViewTree根节点View
        DisplayMetrics dm = getResources().getDisplayMetrics();//获取显示屏属性
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        ViewTreeObserver vto = content.getViewTreeObserver();//获取ViewTree的监听器
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!hasMeasured) {
                    screenHeight = content.getMeasuredHeight();//获取ViewTree的高度
                    hasMeasured = true;//设置为true，使其不再被测量。
                }
                return true;//如果返回false，界面将为空。
            }
        });
        //</editor-fold>

        /****初始化Navigation****/
        //<editor-fold desc="设置侧边栏内部内容">
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        int j = 0;//循环变量
        do {
            //产生随机数
            Random random = new Random();
            int number = random.nextInt(1828);
            int i = 1;//让i从小到大，直到i和随机数相等
            Cursor cursor = dbOpenHelper.getReadableDatabase().query("dict", null
                    , null, null, null, null, null);//查询数据库中的所有单词
            while (cursor.moveToNext()) {  // 遍历Cursor结果集
                if (number == i) { //i碰上随机数了
                    menu.add(j, j, j, cursor.getString(1));//添加该单词到侧边栏中
                    break;
                }
                i++;//让i从小到大，直到i和随机数相等
            }
            j++;
        } while (j < 5);//循环5次，随机添加5个单词
        menu.add(5, 5, 5, "换一换");//添加“换一换”选项
        //</editor-fold>

        /****设置侧边栏item被点击事件****/
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //<editor-fold desc="配置“换一换”选项">
                int tag = item.getItemId();
                if (tag == 5) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("更换一组单词");
                    dialog.setMessage("您确定要更换这5个单词吗？");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定更换", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int j = 0;//与上面初始化Navigation的原理相同
                            do {
                                Random random = new Random();
                                int number = random.nextInt(1828);
                                int i = 0;
                                Cursor cursor = dbOpenHelper.getReadableDatabase().query("dict", null
                                        , null, null, null, null, null);
                                while (cursor.moveToNext()) {  // 遍历Cursor结果集
                                    if (number == i) {
                                        menu.getItem(j).setTitle(cursor.getString(1));
                                        break;
                                    }
                                    i++;
                                }
                                j++;
                            } while (j < 5);
                        }
                    });
                    dialog.setNegativeButton("暂不更换", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                    return true;
                }
                //</editor-fold>

                //<editor-fold desc="配置单词选项">
                String word = item.getTitle().toString();
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("确认界面");
                //这以下是查询单词的解释
                CharSequence key = item.getTitle();
                String interpret = "";
                Cursor cursor0 = dbOpenHelper.getReadableDatabase().query("dict", null
                        , "word = ?", new String[]{key.toString()}, null, null, null);
                if (cursor0.moveToFirst()) {
                    interpret = cursor0.getString(cursor0.getColumnIndex("detail"));
                }
                dialog.setMessage("该单词的翻译为:  " + interpret + '\n' + "您确定要添加该单词吗？");
                //这以上是查询单词的解释
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (buttonNums[nowIndex] == MAXBTNNUM) {
                            View popupView = getLayoutInflater().inflate(R.layout.simple_list_item, null);
                            TextView tv = popupView.findViewById(R.id.paperName);
                            tv.setText("           这页已经添加" + MAXBTNNUM + "个单词了呢!" +
                                    "\n              删除一些已经学会的单词" +
                                    "\n             或者进入其他页面再添加吧!" +
                                    "\n       （点击其他位置收起该文字提醒）");//别删字符串里的空格！！
                            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
                            popupWindow.showAtLocation(MainActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                            return;
                        }
                        //添加单词按钮
                        buttonNums[nowIndex]++;
                        btn[nowIndex * MAXBTNNUM + buttonNums[nowIndex] - 1] = new myButton(MainActivity.this);
                        CharSequence key = item.getTitle();//key是在数据库中查询要用到的键值
                        String interpret = "";//保存中文翻译
                        Cursor cursor0 = dbOpenHelper.getReadableDatabase().query("dict", null
                                , "word = ?", new String[]{key.toString()}, null, null, null);
                        if (cursor0.moveToFirst()) {
                            interpret = cursor0.getString(cursor0.getColumnIndex("detail"));
                        }


                        btn[nowIndex * MAXBTNNUM + buttonNums[nowIndex] - 1].initView(MainActivity.this, screenWidth, screenHeight,
                                word, nowIndex * MAXBTNNUM + buttonNums[nowIndex] - 1, timer, interpret, tts);
                        RelativeLayout relativeLayout = findViewById(R.id.layout);
                        relativeLayout.addView(btn[nowIndex * MAXBTNNUM + buttonNums[nowIndex] - 1]);

                        //在原位置生成新单词
                        Random random = new Random();
                        int number = random.nextInt(1828);
                        int i = 0;
                        Cursor cursor = dbOpenHelper.getReadableDatabase().query("dict", null
                                , null, null, null, null, null);
                        while (cursor.moveToNext()) {  // 遍历Cursor结果集
                            if (number == i) {
                                CharSequence newTitle = cursor.getString(1);
                                item.setTitle(newTitle);
                                break;
                            }
                            i++;
                        }
                    }
                });
                dialog.setNegativeButton("暂不添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return true;
                //</editor-fold>
            }
        });

        /****配置BottomNavigationView****/
        //<editor-fold desc="配置底部导航栏">
        bnView = findViewById(R.id.bottom_nav_view);
        bnView.setOnNavigationItemSelectedListener(item -> {
            //计算时间，在播放动画时，禁止点击该部分的按钮，防止混乱
            long now = System.currentTimeMillis();
            long lastTime = timer.getTime();
            if (now - lastTime <= 5000) return false;
            switch (item.getItemId()) {//设置被点击后的活动跳转
                case R.id.tab_one:
                    break;
                case R.id.tab_two:
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tab_three:
                    Intent intent3 = new Intent(MainActivity.this, StatisticsActivity.class);
                    //传输数据
                    Bundle bundle = new Bundle();
                    String[] strings = new String[buttonNums[nowIndex]];
                    int[] times = new int[buttonNums[nowIndex]];
                    for (int i = nowIndex * MAXBTNNUM, k = 0; i < nowIndex * MAXBTNNUM + buttonNums[nowIndex]; i++, k++) {
                        strings[k] = btn[i].word;
                        times[k] = btn[i].time;
                    }
                    bundle.putStringArray("strings", strings);
                    bundle.putIntArray("times", times);
                    bundle.putInt("buttonNum", buttonNums[nowIndex]);
                    intent3.putExtras(bundle);
                    startActivity(intent3);
                    break;
                case R.id.tab_four:
                    if (papers.isEmpty()) {
                        papers.add("paper1");
                    }
                    View popupView = getLayoutInflater().inflate(R.layout.popup_view, null);
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < papers.size(); i++) {
                        if (i == nowIndex) {
                            sb.append(papers.get(i)).append("（当前所在页面）").append(" ");
                        } else {
                            sb.append(papers.get(i)).append("（点击进入对应页面）").append(" ");
                        }
                    }
                    String[] paperNames = sb.toString().split(" ");
                    ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(MainActivity.this,
                            R.layout.simple_list_item, paperNames);
                    ListView listView = popupView.findViewById(R.id.list);
                    listView.setAdapter(nameAdapter);
                    PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.showAtLocation(MainActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == nowIndex) {
                                popupWindow.dismiss();
                                return;
                            }
                            for (int i = 0; i < MAXGROUPNUM * MAXBTNNUM; i++) {
                                if (btn[i] == null) {
                                    continue;
                                }
                                btn[i].setVisibility(View.INVISIBLE);
                                btn[i].setClickable(false);
                            }
                            for (int i = position * MAXBTNNUM; i < position * MAXBTNNUM + buttonNums[position]; i++) {
                                btn[i].setVisibility(View.VISIBLE);
                                btn[i].setClickable(true);
                            }
                            nowIndex = position;
                            popupWindow.dismiss();
                        }
                    });
                    Button addPaper = popupView.findViewById(R.id.deleteWord);
                    addPaper.setText("点击新建一页A4纸");
                    addPaper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (papers.size() == MAXGROUPNUM) {
                                View popupView = getLayoutInflater().inflate(R.layout.simple_list_item, null);
                                TextView tv = popupView.findViewById(R.id.paperName);
                                tv.setText("\"都tm10张了，还新建，你想背多少单调，这也太卷了吧！\"" +
                                        "\n    （点击其他位置可以收起该文字提醒）");
                                PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                                popupWindow.showAtLocation(MainActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                                return;
                            }
                            popupWindow.dismiss();
                            sb.append("paper")
                                    .append(papers.size() + 1)
                                    .append("（点击进入对应页面）")
                                    .append(" ");
                            String[] paperNames = sb.toString().split(" ");
                            ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(MainActivity.this,
                                    R.layout.simple_list_item, paperNames);
                            ListView listView = popupView.findViewById(R.id.list);
                            listView.setAdapter(nameAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (position == nowIndex) {
                                        popupWindow.dismiss();
                                        return;
                                    }
                                    for (int i = 0; i < MAXGROUPNUM * MAXBTNNUM; i++) {
                                        if (btn[i] == null) {
                                            continue;
                                        }
                                        btn[i].setVisibility(View.INVISIBLE);
                                        btn[i].setClickable(false);
                                    }
                                    for (int i = position * MAXBTNNUM; i < position * MAXBTNNUM + buttonNums[position]; i++) {
                                        btn[i].setVisibility(View.VISIBLE);
                                        btn[i].setClickable(true);
                                    }
                                    nowIndex = position;
                                    popupWindow.dismiss();
                                }
                            });
                            popupWindow.showAtLocation(MainActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                            papers.add("paper" + (papers.size() + 1));
                        }
                    });
                    break;
            }
            return false;
        });
        //</editor-fold>

        loadData();//加载保存的数据
    }


    //<editor-fold desc="配置顶部导航栏">
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        MenuItem item = menu.findItem(R.id.settings);
        SpannableString string = new SpannableString(item.getTitle());
        /**注意修改颜色**/
        string.setSpan(new ForegroundColorSpan(Color.WHITE), 0, string.length(), 0);
        item.setTitle(string);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long now = System.currentTimeMillis();
        long lastTime = timer.getTime();
        if (now - lastTime <= 5000) return false;
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backup:
                timer.setTime(System.currentTimeMillis() - 2800);
                DataSupport.deleteAll(WordButton.class);
                for (int i = 0; i < 200; i++) {
                    saveData(i);
                }
                Toast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                View popupView = getLayoutInflater().inflate(R.layout.popup_view, null);
                initWords();
                WAdapter adapter = new WAdapter(MainActivity.this, R.layout.w_item, words);
                ListView listView = (ListView) popupView.findViewById(R.id.list);
                listView.setAdapter(adapter);
                listView.setItemsCanFocus(true);// 让item得到焦点，【注意】必须将ListView的item布局中的Checkbox控件失去焦点
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);// 设置多选模式
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ViewGroup viewGroup = (ViewGroup) view;
                        CheckBox checkBox = (CheckBox) viewGroup.getChildAt(0);
                        if (checkBox.isChecked()) {
                            isChecked[position] = false;
                            checkBox.setChecked(false);
                        } else {
                            isChecked[position] = true;
                            checkBox.setChecked(true);
                        }
                    }
                });
                PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(MainActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                //popupWindow.showAsDropDown(findViewById(R.id.toolbar));

                deleteWord = (Button) popupView.findViewById(R.id.deleteWord);
                deleteWord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = nowIndex * MAXBTNNUM, k = 0; i < nowIndex * MAXBTNNUM + buttonNums[nowIndex]; i++, k++) {
                            if (isChecked[k]) {
                                //Toast.makeText(MainActivity.this, "" + i, Toast.LENGTH_SHORT).show();
                                btn[i].setVisibility(View.INVISIBLE);
                                btn[i].setClickable(false);

                                for (int j = i + 1; j < buttonNums[nowIndex]; j++) {
                                    btn[j - 1] = btn[j];
                                }
                                buttonNums[nowIndex]--;
                            }
                        }
                        for (int i = 0; i < MAXBTNNUM; i++) {
                            isChecked[i] = false;
                        }
                        popupWindow.dismiss();
                    }
                });
                break;
            case R.id.settings:
                readChinese = readChinese ? false : true;
                break;
            default:
        }
        return true;
    }

    private void initWords() {
        words.removeAll(words);
        for (int i = nowIndex * MAXBTNNUM; i < nowIndex * MAXBTNNUM + buttonNums[nowIndex]; i++) {
            WordItem wordItem = new WordItem(btn[i].word);
            words.add(wordItem);
        }
    }
    //</editor-fold>


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override//界面被动刷新时会调用该方法
    protected void onResume() {
        super.onResume();
        //让侧边栏展开
        mDrawerLayout.openDrawer(Gravity.LEFT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //保存数据
        DataSupport.deleteAll(WordButton.class);
        for (int i = 0; i < MAXGROUPNUM * MAXBTNNUM; i++) {
            saveData(i);
        }
        SharedPreferences.Editor editor = getSharedPreferences("wordData", MODE_PRIVATE).edit();
        for (int i = 0; i < MAXGROUPNUM * MAXBTNNUM; i++) {
            if (btn[i] == null) {
                editor.putInt(String.valueOf(-i), 0);
            } else {
                editor.putInt(String.valueOf(i), btn[i].time);
            }
        }
        editor.apply();
        SharedPreferences.Editor editor2 = getSharedPreferences("buttonNums", MODE_PRIVATE).edit();
        for (int i = 0; i < buttonNums.length; i++) {
            editor2.putInt(String.valueOf(i), buttonNums[i]);
        }
        editor2.apply();
        SharedPreferences.Editor editor3 = getSharedPreferences("paperName", MODE_PRIVATE).edit();
        for (int i = 0; i < papers.size(); i++) {
            editor3.putString(String.valueOf(i), papers.get(i));
        }
        editor3.apply();
        //关闭语音播放器
        if (tts != null) {
            tts.shutdown();
        }
        buttonNum = 0;
    }

    //<editor-fold desc="保存数据时用到的方法">
    public void saveData(int i) {
        if (btn[i] == null) {
            WordButton wordButton = new WordButton();
            wordButton.setId(-i);
            wordButton.save();
            return;
        }
        WordButton wordButton = new WordButton();
        wordButton.setId(i);
        wordButton.setWord(btn[i].word);
        wordButton.setLeft(btn[i].left);
        wordButton.setTop(btn[i].top);
        wordButton.setRight(btn[i].right);
        wordButton.setBottom(btn[i].bottom);
        wordButton.setInterpret(btn[i].interpret);
        //wordButton.setTime(btn[i].time);
        wordButton.save();
    }
    //</editor-fold>

    //<editor-fold desc="加载数据时用到的方法">
    public void loadData() {
        List<WordButton> wordButtons = DataSupport.findAll(WordButton.class);
        for (WordButton wordButton : wordButtons) {
            buttonNum++;
            if (wordButton.getId() < 0) {
                btn[buttonNum - 1] = null;
                continue;
            }
            btn[buttonNum - 1] = new myButton(MainActivity.this);
            btn[buttonNum - 1].initView(MainActivity.this, screenWidth, screenHeight, wordButton.getWord(),
                    wordButton.getId(), timer, wordButton.getInterpret(), tts);
            RelativeLayout relativeLayout = findViewById(R.id.layout);
            relativeLayout.addView(btn[buttonNum - 1]);

            btn[buttonNum - 1].left = wordButton.getLeft();
            btn[buttonNum - 1].top = wordButton.getTop();
            btn[buttonNum - 1].right = wordButton.getRight();
            btn[buttonNum - 1].bottom = wordButton.getBottom();
            //btn[buttonNum - 1].time = wordButton.getTime();
            SharedPreferences pref = getSharedPreferences("wordData", MODE_PRIVATE);
            btn[buttonNum - 1].time = pref.getInt(String.valueOf(buttonNum - 1), 0);
        }
    }
    //</editor-fold>

    //<editor-fold desc="初始化语音播放器">
    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {
            //指定当前朗读的是英文，如果不是给予提示
            int result = tts.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(MainActivity.this, "不让读", Toast.LENGTH_SHORT).show();
            }
            //tts.setPitch(0.1f);
            tts.setSpeechRate(0.9f);
        }
    }
    //</editor-fold>
}