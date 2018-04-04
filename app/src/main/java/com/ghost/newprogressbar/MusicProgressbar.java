package com.ghost.newprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * 作者：Ghost
 * 时间：2018/4/4
 * 功能：音乐播放进度条
 * <p>
 * 1.圆点  圆点颜色
 * 2.已播放部分
 * 3.未播放部分
 */
public class MusicProgressbar extends ProgressBar {

    private static final int DEFAULT_LINE_HEIGHT = 1;//DP
    private static final int DEFAULT_PLAY_COLOR = 0XFFFC00D;
    private static final int DEFAULT_UN_PLAY_COLOR = 0XFFD3D6DA;
    private static final int DEFAULT_CIRCLE_RADIUS = 2;//DP

    private int mLineHeight = dpTopx(DEFAULT_LINE_HEIGHT);
    private int mPlayColor = DEFAULT_PLAY_COLOR;
    private int mUnPlayColor = DEFAULT_UN_PLAY_COLOR;
    private int mCircleRadius = dpTopx(DEFAULT_CIRCLE_RADIUS);

    private Paint mPaint = new Paint();


    public MusicProgressbar(Context context) {
        this(context, null);
    }

    public MusicProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取我们的自定义属性
        getStyledAttrs(attrs);
    }

    private void getStyledAttrs(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.MusicProgressbar);

        mLineHeight = (int) typedArray.getDimension
                (R.styleable.MusicProgressbar_line_height, mLineHeight);
        mPlayColor = typedArray.getColor
                (R.styleable.MusicProgressbar_play_color, mPlayColor);
        mUnPlayColor = typedArray.getColor
                (R.styleable.MusicProgressbar_un_play_color, mUnPlayColor);
        mCircleRadius = (int) typedArray.getDimension
                (R.styleable.MusicProgressbar_circle_radius, mCircleRadius);
        //设置抗锯齿
        mPaint.setAntiAlias(true);

        typedArray.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //确定我们的绘制区域
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        //是否需要去绘制加载的进度条
        boolean noNeedPlay = false;

        //进度条肯定有一个当前的进度，与一 个最大的进度
        float radio = getProgress() * 1.0f / getMax();

        float progressX = radio * getWidth();

        int circleWidth = 2 * mCircleRadius;

        if (progressX + circleWidth > getWidth()) {
            progressX = getWidth() - circleWidth;
            noNeedPlay = true;
        }

        mPaint.setColor(mPlayColor);
        canvas.drawCircle(progressX + mCircleRadius, 0, mCircleRadius, mPaint);

        if (progressX > 0) {
            mPaint.setColor(mPlayColor);
            //设置线宽
            mPaint.setStrokeWidth(mLineHeight);
            canvas.drawLine(0, 0, progressX, 0, mPaint);
        }


        if (!noNeedPlay) {
            float start = progressX;
            mPaint.setColor(mUnPlayColor);
            //设置线宽
            mPaint.setStrokeWidth(mLineHeight);
            canvas.drawLine(progressX + circleWidth, 0, getWidth(), 0, mPaint);

        }


        canvas.restore();
    }

    /**
     * dp转px
     *
     * @param dpVal dp的值
     * @return px的值
     */
    private int dpTopx(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                getResources().getDisplayMetrics());
    }
}
