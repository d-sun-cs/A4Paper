package com.example.a4paper;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class  NumberProgressView extends View {
    /**
     * 进度条画笔的宽度（dp）
     */
    private int paintProgressWidth = 3;

    /**
     * 文字百分比的字体大小（sp）
     */
    private int paintTextSize = 10;
    /**
     * 左侧已完成进度条的颜色
     */
    private int paintLeftColor = 0xff67aae4;
    /**
     * 右侧未完成进度条的颜色
     */
    private int paintRightColor = 0xffaaaaaa;
    /**
     * 百分比文字的颜色
     */
    private int paintTextColor = 0xffff0077;
    /**
     * Contxt
     */
    private Context context;
    /**
     * 主线程传过来进程 0 - 100
     */
    private int progress;
    /**
     * 得到自定义视图的宽度
     */
    private int viewWidth;
    /**
     * 得到自定义视图的Y轴中心点
     */
    private int viewCenterY;
    /**
     * 画左边已完成进度条的画笔
     */
    private Paint paintleft = new Paint();
    /**
     * 画右边未完成进度条的画笔
     */
    private Paint paintRight = new Paint();
    /**
     * 画中间的百分比文字的画笔
     */
    private Paint paintText = new Paint();
    /**
     * 要画的文字的宽度
     */
    private int textWidth;
    /**
     * 画文字时底部的坐标
     */
    private float textBottomY;
    /**
     * 包裹文字的矩形
     */
    private Rect rect = new Rect();
    /**
     * 文字总共移动的长度（即从0%到100%文字左侧移动的长度）
     */
    private int totalMovedLength;
    public NumberProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
// 构造器中初始化数据
        initData();
    }
   /**
    *工具方法
    */

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 初始化数据
     */
    private void initData() {
//设置进度条画笔的宽度
        int paintProgressWidthPx = dip2px(context, paintProgressWidth);
//设置百分比文字的尺寸
        int paintTextSizePx = sp2px(context, paintTextSize);
// 已完成进度条画笔的属性
        paintleft.setColor(paintLeftColor);
        paintleft.setStrokeWidth(paintProgressWidthPx);
        paintleft.setAntiAlias(true);
        paintleft.setStyle(Paint.Style.FILL);
// 未完成进度条画笔的属性
        paintRight.setColor(paintRightColor);
        paintRight.setStrokeWidth(paintProgressWidthPx);
        paintRight.setAntiAlias(true);
        paintRight.setStyle(Paint.Style.FILL);
// 百分比文字画笔的属性
        paintText.setColor(paintTextColor);
        paintText.setTextSize(paintTextSizePx);
        paintText.setAntiAlias(true);
        paintText.setTypeface(Typeface.DEFAULT_BOLD);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getWidthAndHeight();
    }
    /**
     * 得到视图等的高度宽度尺寸数据
     */
    private void getWidthAndHeight() {
//得到包围文字的矩形的宽高
        paintText.getTextBounds("000%", 0, "000%".length(), rect);
        textWidth = rect.width();
        textBottomY = viewCenterY + rect.height() / 2;
//得到自定义视图的高度
        int viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();
        viewCenterY = viewHeight / 2;
        totalMovedLength = viewWidth - textWidth;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//得到float型进度
        float progressFloat = progress / 100.0f;
//当前文字移动的长度
        float currentMovedLentgh = totalMovedLength * progressFloat*3.333333f;
//画左侧已经完成的进度条，长度为从Veiw左端到文字的左侧
        canvas.drawLine(0, viewCenterY, currentMovedLentgh, viewCenterY, paintleft);
//画右侧未完成的进度条，这个进度条的长度不是严格按照百分比来缩放的，因为文字的长度会变化，所以它的长度缩放比例也会变化
        if (progress < 10) {
            canvas.drawLine(currentMovedLentgh + textWidth * 0.5f, viewCenterY, viewWidth, viewCenterY, paintRight);
        } else if (progress < 100) {
            canvas.drawLine(currentMovedLentgh + textWidth * 0.75f, viewCenterY, viewWidth, viewCenterY, paintRight);
        } else {
            canvas.drawLine(currentMovedLentgh + textWidth, viewCenterY, viewWidth, viewCenterY, paintRight);
        }
//画文字(注意：文字要最后画，因为文字和进度条可能会有重合部分，所以要最后画文字，用文字盖住重合的部分)
        canvas.drawText(progress + "", currentMovedLentgh, textBottomY, paintText);
    }
    /**
     * @param progress 外部传进来的当前进度
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}
