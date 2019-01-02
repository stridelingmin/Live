package cn.stride1025.live;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class OCRRectView extends View {


    private Paint mLinePaint;
    private int mViewWidth;
    private int mViewHeight;
    //四个绿色边角对应的宽度
    private static final int CORNER_WIDTH = 10 ;
    //四个绿色边角对应的长度
    private static int screenRateLength = 40;
    private Paint mShadePaint;

    private static String centerText="请 将 身 份 证 对 准 区 域";
    private Paint mTextPaint;
    private int mTextWidth;
    private float mTextLenght;
    private Path mLinePath;
    private Rect mTopRect;
    private Rect mBottomRect;
    private Rect mLeftRect;
    private Rect mRightRect;

    public OCRRectView(Context context) {
        this(context,null);
    }

    public OCRRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();

    }

    private void init() {


        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);//FILL填充内部；STROKE不填充，硬笔线；FILL_AND_STROKE和FILL没有看出明显不同
        mLinePaint.setAntiAlias(true);// 消除锯齿
        mLinePaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));//设置画笔颜色
        mLinePaint.setStrokeWidth(CORNER_WIDTH);// 设置paint的外框宽度

        mShadePaint = new Paint();
        mShadePaint.setColor(getResources().getColor(android.R.color.transparent));
        mShadePaint.setAlpha(150);
        mShadePaint.setAntiAlias(true);// 消除锯齿

        mTextPaint = new TextPaint();
        mTextPaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        mTextPaint.setTextSize(60);
        mTextPaint.setAntiAlias(true);
        mTextLenght = mTextPaint.measureText(centerText);

        float descent = mTextPaint.descent();
        float ascent = mTextPaint.ascent();

        mTextWidth = (int) ( - ascent);
        mLinePath = new Path();

        Rect rect = new Rect();
        mTextPaint.getTextBounds(centerText, 0, centerText.length(), rect);
        int width = rect.width();//文字宽
        int height = rect.height();//文字高

        Log.i("tt","getTextBounds  " + width + " length " + height);

        Log.i("tt","mTextWidth" + mTextWidth + " length " + mTextLenght);
        Log.i("tt","descent" + descent + " ascent " + ascent);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mViewWidth = canvas.getWidth();
        mViewHeight = canvas.getHeight();
        Log.i("tt","ww" + mViewWidth);


        mTopRect = new Rect(0, 0, mViewWidth, mViewHeight / 6);
        mBottomRect = new Rect(0, 5*(mViewHeight/6), mViewWidth, mViewHeight);
        mLeftRect = new Rect(0, (mViewHeight/6), mViewWidth/6, 5*(mViewHeight/6));
        mRightRect = new Rect(5*(mViewWidth/6), (mViewHeight/6), mViewWidth, 5*(mViewHeight/6));

        canvas.drawRect(mTopRect,mShadePaint);
        canvas.drawRect(mBottomRect,mShadePaint);
        canvas.drawRect(mLeftRect,mShadePaint);
        canvas.drawRect(mRightRect,mShadePaint);

        mLinePath.moveTo(mViewWidth/6,mViewHeight/6+screenRateLength);
        mLinePath.lineTo(mViewWidth/6,mViewHeight/6);
        mLinePath.lineTo(mViewWidth/6+screenRateLength,mViewHeight/6);

        mLinePath.moveTo(5*(mViewWidth/6)-screenRateLength,mViewHeight/6);
        mLinePath.lineTo(5*(mViewWidth/6),mViewHeight/6);
        mLinePath.lineTo(5*(mViewWidth/6),mViewHeight/6+screenRateLength);

        mLinePath.moveTo(5*(mViewWidth/6),5*(mViewHeight/6)-screenRateLength);
        mLinePath.lineTo(5*(mViewWidth/6),5*(mViewHeight/6));
        mLinePath.lineTo(5*(mViewWidth/6)-screenRateLength,5*(mViewHeight/6));

        mLinePath.moveTo((mViewWidth/6)+screenRateLength,5*(mViewHeight/6));
        mLinePath.lineTo((mViewWidth/6),5*(mViewHeight/6));
        mLinePath.lineTo((mViewWidth/6),5*(mViewHeight/6)-screenRateLength);

        canvas.drawPath(mLinePath,mLinePaint);
        canvas.rotate(90);
        canvas.drawText(centerText,mViewHeight/2-(int)mTextLenght/2,-(mViewWidth/2)+(mTextWidth/2) ,mTextPaint);

    }
}
