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
 * 功能：横向的进度条   3个参数的构造方法，一般我们很少去使用
 */
public class HorizontalProgressBar extends ProgressBar {


    //首先我们的给我们的自定义属性赋一些初始值。一般很多人都懒得去设置，所以很有必要
    private static final int DEFAULT_TEXT_SIZE = 10;//字体大小单位一般使用SP
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D;
    private static final int DEFAULT_UN_REACH_COLOR = 0XFFD3D6DA;
    private static final int DEFAULT_UN_REACH_HEIGHT = 2;//DP
    private static final int DEFAULT_REACH_COLOR = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_REACH_HEIGHT = 2;//DP
    private static final int DEFAULT_TEXT_OFFSET = 10;//DP

    private int mTextSize = spTopx(DEFAULT_TEXT_SIZE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mUnReachColor = DEFAULT_UN_REACH_COLOR;
    private int mUnReachHeight = dpTopx(DEFAULT_UN_REACH_HEIGHT);
    private int mReachColor = DEFAULT_REACH_COLOR;
    private int mReachHeight = dpTopx(DEFAULT_REACH_HEIGHT);
    private int mOffset = dpTopx(DEFAULT_TEXT_OFFSET);

    private Paint mPaint = new Paint();

    private int mRealWidth;

    /**
     * 1个参数的构造方法，当我们new一个控件的时候调用
     * 让我们1个参数的构造方法去调用2个参数的构造方法
     *
     * @param context 上下文
     */
    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    /**
     * 2个参数的 构造方法，可以在布局文件中进行使用
     * 让我们2个参数的构造方法去调用3个参数的构造方法
     *
     * @param context 上下文
     * @param attrs   属性
     */
    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    //初始化我们的变量
    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取我们的自定义属性
        getStyledAttrs(attrs);
    }

    //获取我们的自定义属性
    private void getStyledAttrs(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.HorizontalProgressBar);

        mTextSize = (int) typedArray.getDimension
                (R.styleable.HorizontalProgressBar_progress_text_size, mTextSize);
        mTextColor = typedArray.getColor
                (R.styleable.HorizontalProgressBar_progress_text_color, mTextColor);
        mUnReachColor = typedArray.getColor
                (R.styleable.HorizontalProgressBar_progress_un_reach_color, mUnReachColor);
        mUnReachHeight = (int) typedArray.getDimension
                (R.styleable.HorizontalProgressBar_progress_un_reach_height, mUnReachHeight);
        mReachColor = typedArray.getColor
                (R.styleable.HorizontalProgressBar_progress_reach_color, mReachColor);
        mReachHeight = (int) typedArray.getDimension
                (R.styleable.HorizontalProgressBar_progress_reach_height, mReachHeight);
        mOffset = (int) typedArray.getDimension
                (R.styleable.HorizontalProgressBar_progress_margin_size, mOffset);

        typedArray.recycle();

        //设置我们字体的大小
        mPaint.setTextSize(mTextSize);
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

    /**
     * sp转px
     *
     * @param spVal sp的值
     * @return px的值
     */
    private int spTopx(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
                getResources().getDisplayMetrics());
    }

    /**
     * 测量
     *
     * @param widthMeasureSpec  宽度空间
     * @param heightMeasureSpec 高度控件
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //宽度是一定会设置的  所以不用去管
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        //实现我们对高度的测量
        int height = measureHeight(heightMeasureSpec);

        setMeasuredDimension(width, height);

        //这里表示我们实际绘制的宽度
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int measureHeight(int heightMeasureSpec) {

        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            //精确值
            result = size;

        } else {
            //这里是我们所需要的值
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            //getPaddingTop() 上边距  getPaddingBottom()下边据 然后比较自定义View那部分最高
            result = getPaddingTop() + getPaddingBottom() + Math.max
                    (Math.max(mReachHeight, mUnReachHeight), Math.abs(textHeight));

            if (mode == MeasureSpec.AT_MOST) {
                //不能超过这个值
                result = Math.min(result, size);
            }

        }

        return result;
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //因为我们需要自己完全去控制，所以不许继承
//        super.onDraw(canvas);
        canvas.save();
        //确定我们的绘制区域
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        //是否需要去绘制加载的进度条
        boolean noNeedUnRech = false;

        //进度条肯定有一个当前的进度，与一 个最大的进度
        float radio = getProgress() * 1.0f / getMax();
        //文本的内容  就是我们进度条的进度加一个百分号
        String text = getProgress() + "%";
        //文本的宽度
        int textWidth = (int) mPaint.measureText(text);
        //已经加载的进度条的X值
        float progressX = radio * mRealWidth;
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnRech = true;
        }

        //因为我们的文字与进度条之间存在一个间距，所以我们的进度条并不能绘制的那么长
        float endX = radio * mRealWidth - mOffset / 2;

        if (endX > 0) {
            mPaint.setColor(mReachColor);
            //设置线宽
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);

        }

        //开始绘制我们的文本 draw text
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progressX, y, mPaint);

        //draw un rech 区域
        if (!noNeedUnRech) {
            float start = progressX + mOffset / 2 + textWidth;
            mPaint.setColor(mUnReachColor);
            //设置线宽
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();
    }
}
