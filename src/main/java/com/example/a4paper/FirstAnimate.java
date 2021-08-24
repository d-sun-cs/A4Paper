package com.example.a4paper;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FirstAnimate extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取首页动画布局
        setContentView(R.layout.activity_first_animate);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha); //加载开场动画
        this.findViewById(R.id.first_picture).setAnimation(animation);  //获取相应动画

//        setFeatureDrawableAlpha(R.id.first_picture, 0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            //开场结束后自动跳入主界面
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(FirstAnimate.this, MainActivity.class);
                startActivity(intent);
                FirstAnimate.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
