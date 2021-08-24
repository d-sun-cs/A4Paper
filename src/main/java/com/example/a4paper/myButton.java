
package com.example.a4paper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class myButton extends androidx.appcompat.widget.AppCompatButton {

    /*********属性*********/
    //<editor-fold desc="属性">
    private boolean clickormove = true;//点击或拖动，点击为true，拖动为false
    private int downX, downY;//按下时的X，Y坐标
    //位置信息
    int left;
    int top;
    int right;
    int bottom;
    int id;
    int time = 0;   //按钮的点击次数
    /*    boolean clicked = false;       //判断按钮是否已生成统计*/
    String word;//英语单词本身
    String interpret;//英语单词翻译
    //TextToSpeech tts;
    //</editor-fold>

    public myButton(Context context) {
        super(context);
    }

    public void initView(Context context, int screenWidth, int screenHeight,
                         String word, int id, Timer timer, String interpret,
                         TextToSpeech tts) {
        setOnTouchListener(new OnTouchListener() {//设置按钮被触摸的事件，主要是实现拖动功能
            int lastX, lastY; // 记录移动的最后的位置
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long now = System.currentTimeMillis();
                long lastTime = timer.getTime();
                if (now - lastTime <= 5000) return false;
                int ea = event.getAction();//获取事件类型
                switch (ea) {
                    case MotionEvent.ACTION_DOWN: // 按下事件
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        downX = lastX;
                        downY = lastY;
                        break;
                    case MotionEvent.ACTION_MOVE: // 拖动事件
// 移动中动态设置位置
                        int dx = (int) event.getRawX() - lastX;//位移量X
                        int dy = (int) event.getRawY() - lastY;//位移量Y
                        left = v.getLeft() + dx;
                        top = v.getTop() + dy;
                        right = v.getRight() + dx;
                        bottom = v.getBottom() + dy;
//++限定按钮被拖动的范围
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if (top < 155) {
                            top = 155;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > screenHeight - 155) {//可能出现适配的问题
                            bottom = screenHeight - 155;
                            top = bottom - v.getHeight();
                        }
//--限定按钮被拖动的范围
                        /*RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(right - left, bottom - top);
                        layoutParams.setMargins(left, top, screenWidth - right, screenHeight - bottom);
                        v.setLayoutParams(layoutParams);*/
                        v.layout(left, top, right, bottom);
// 记录当前的位置

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP: // 弹起事件
//判断是单击事件或是拖动事件，位移量大于5则断定为拖动事件
                        if (Math.abs((int) (event.getRawX() - downX)) > 5
                                || Math.abs((int) (event.getRawY() - downY)) > 5)
                            clickormove = false;
                        else
                            clickormove = true;
                        break;
                }
                return false;
            }
        });

        setOnClickListener(new OnClickListener() {//设置按钮被点击的监听器
            @Override
            public void onClick(View v) {
                //计算时间，防止同时点击多个按钮
                long now = System.currentTimeMillis();
                long lastTime = timer.getTime();
                int keepingTime = 3000;//放大后保持的时间
                //Toast.makeText(context, "" + v.getY(), Toast.LENGTH_SHORT).show(); //之后调试可能还会用到
                if (clickormove && now - lastTime > 2500 + keepingTime) {//修改此参数可以修改保持时间
                    timer.setTime(now);
                    //该单词被点击的次数+1
                    time = time + 1;

                    //<editor-fold desc="设置动画和音频效果">
                    v.animate().x(screenWidth / 2 - v.getWidth() / 2).y(screenHeight / 2 - v.getHeight() / 2)
                            .setDuration(500)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {//位移动画结束时
                                    //播放音频

                                    if (MainActivity.readChinese) {
                                        int result = tts.setLanguage(Locale.CHINESE);
                                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                                        {
                                            Toast.makeText(context, "不让读", Toast.LENGTH_SHORT).show();
                                        }
                                        String chineseInterpret = interpret.replace("n.", "")
                                                .replace("a.", "")
                                                .replace("vt.", "")
                                                .replace("vi.", "")
                                                .replace("prep.", "")
                                                .replace("ad.", "");
                                        tts.setSpeechRate(1.2f);
                                        tts.speak(chineseInterpret, TextToSpeech.QUEUE_FLUSH, null);
                                        tts.speak(chineseInterpret, TextToSpeech.QUEUE_ADD, null);
                                    } else {
                                        tts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
                                        tts.speak(word, TextToSpeech.QUEUE_ADD, null);
                                        tts.speak(word, TextToSpeech.QUEUE_ADD, null);
                                        //显示中文
                                        Toast t;
                                        t = Toast.makeText(context, interpret, Toast.LENGTH_LONG);
                                        t.setGravity(Gravity.CENTER, 0, 350);
                                        LinearLayout linearLayout = (LinearLayout) t.getView();
                                        TextView tv = (TextView) linearLayout.getChildAt(0);
                                        tv.setTextSize(25);
                                        t.show();
                                    }
                                }
                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            }).start();
                    ObjectAnimator ScaleY1 = ObjectAnimator.ofFloat(v, "scaleY", 1f, 4f);
                    ObjectAnimator ScaleX1 = ObjectAnimator.ofFloat(v, "scaleX", 1f, 4f);
                    ObjectAnimator ScaleY2 = ObjectAnimator.ofFloat(v, "scaleY", 4f, 1f);
                    ObjectAnimator ScaleX2 = ObjectAnimator.ofFloat(v, "scaleX", 4f, 1f);

                    AnimatorSet animatorSet1 = new AnimatorSet();
                    animatorSet1.play(ScaleY1).with(ScaleX1);
                    animatorSet1.setDuration(500);
                    animatorSet1.setStartDelay(500);
                    animatorSet1.start();

                    AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorSet2.play(ScaleY2).with(ScaleX2);
                    animatorSet2.setStartDelay(1000 + keepingTime);//修改此参数可以修改保持时间
                    animatorSet2.setDuration(500);
                    animatorSet2.start();

                    float curTranslationX = v.getTranslationX();
                    float curTranslationY = v.getTranslationY();
                    ObjectAnimator X = ObjectAnimator.ofFloat(v, "TranslationX", curTranslationX);
                    ObjectAnimator Y = ObjectAnimator.ofFloat(v, "TranslationY", curTranslationY);
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    animatorSet3.play(X).with(Y);
                    animatorSet3.setStartDelay(1000 + keepingTime);//修改此参数可以修改保持时间
                    animatorSet3.setDuration(500);
                    animatorSet3.start();
                    //</editor-fold>
                }
            }
        });

        setOnLongClickListener(v -> {
            if (clickormove) {
                Toast.makeText(context, "功能有待开发", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        /*********其他配置*********/
        //<editor-fold desc="其他配置">
        this.id = id;
        setText(word);
        setAllCaps(false);
        this.word = word;
        this.interpret = interpret;
        //</editor-fold>
    }
}
