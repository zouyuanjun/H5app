package com.zou.fastlibrary.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.zou.fastlibrary.R;


public class ShapeCornerBgView extends AppCompatTextView {
    int borderWidth = 1;// 默认1dimpx
    boolean isHasBorder = false;

    int borderColor;// 线条的颜色，默认与字的颜色相同
    int bgColor;// 背景的颜色，默认是透明的
    int mRadius = 3;// 默认3

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    int mColorText;

    private Rect rect = new Rect();// 方角

    // 四个角落是否是全是圆角
    boolean isTopLeftCorner = true;
    boolean isBottomLeftCorner = true;
    boolean isTopRightCorner = true;
    boolean isBottomRightCorner = true;

    int mColorBgEnableFalse;
    int mColorTextEnableFalse;

    public ShapeCornerBgView(Context context, AttributeSet attrs) {
        super(context, attrs);

        borderWidth = getDimen720Px(context, borderWidth);
        mRadius = getDimen720Px(context, mRadius);
        mColorText=mColorTextEnableFalse=borderColor = getCurrentTextColor();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeCornerBgView);
        isHasBorder = mTypedArray.getBoolean(R.styleable.ShapeCornerBgView_appBorder, isHasBorder);// 默认无边框
        borderWidth = mTypedArray.getDimensionPixelSize(R.styleable.ShapeCornerBgView_appBorderWidth, borderWidth);
        mRadius = mTypedArray.getDimensionPixelSize(R.styleable.ShapeCornerBgView_appRadius, mRadius);

        borderColor = mTypedArray.getColor(R.styleable.ShapeCornerBgView_appBorderColor, borderColor);

        bgColor = isHasBorder ? Color.TRANSPARENT : Color.RED;

        bgColor = mTypedArray.getColor(R.styleable.ShapeCornerBgView_appBgColor, bgColor);
        // 四个角落是否全是圆角,默认全是真的
        isTopLeftCorner = mTypedArray.getBoolean(R.styleable.ShapeCornerBgView_appTopLeftCorner, isTopLeftCorner);
        isBottomLeftCorner = mTypedArray.getBoolean(R.styleable.ShapeCornerBgView_appBottomLeftCorner, isBottomLeftCorner);
        isTopRightCorner = mTypedArray.getBoolean(R.styleable.ShapeCornerBgView_appTopRightCorner, isTopRightCorner);
        isBottomRightCorner = mTypedArray.getBoolean(R.styleable.ShapeCornerBgView_appBottomRightCorner, isBottomRightCorner);

        mColorBgEnableFalse=mTypedArray.getColor(R.styleable.ShapeCornerBgView_appEnableFalseBgColor, bgColor);
        mColorTextEnableFalse=mTypedArray.getColor(R.styleable.ShapeCornerBgView_appEnableFalseTextColor, mColorTextEnableFalse);
        mTypedArray.recycle();
        this.setGravity(Gravity.CENTER);// 全部居中显示
        this.setEnabled(isEnabled());
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() == 0) // 没初始化完成不需要绘制
            return;
        float ffVar[] = getOutterRadii();
        // 先画背景
        if (bgColor != Color.TRANSPARENT) {// 透明就不用画了
            int color = bgColor;
            if (isEnabled() == false) {
                color = mColorBgEnableFalse;
            }
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(ffVar, null, null));
            Paint paint = shapeDrawable.getPaint();
            paint.setColor(color);
            shapeDrawable.setBounds(rect);
            shapeDrawable.draw(canvas);
        }
        // 有边框
        if (isHasBorder) {
            RectF rectF = new RectF(borderWidth, borderWidth, borderWidth, borderWidth);
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(ffVar, rectF, ffVar));
            Paint paint = shapeDrawable.getPaint();
            paint.setColor(borderColor);
            shapeDrawable.setBounds(rect);
            shapeDrawable.draw(canvas);
        }
        super.onDraw(canvas);
    }

    //获得圆角的度数
    private float[] getOutterRadii() {
        float fRandis[] = { mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius };

        if (isTopLeftCorner == false) {
            fRandis[0] = 0;
            fRandis[1] = 0;
        }
        if (isTopRightCorner == false) {
            fRandis[2] = 0;
            fRandis[3] = 0;
        }
        if (isBottomLeftCorner == false) {
            fRandis[4] = 0;
            fRandis[5] = 0;
        }
        if (isBottomRightCorner == false) {
            fRandis[6] = 0;
            fRandis[7] = 0;
        }
        return fRandis;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        rect.left = 0;
        rect.top = 0;
        rect.bottom = h;
        rect.right = w;
    }

    // 设置是否可用更改颜色
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setTextColor(enabled ? mColorText : mColorTextEnableFalse);
        invalidate();
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    public  int getDimen720Px(Context context, int dimen) {
        float dp = dimen * 1080f / 750 / 3;
        return dp2px(context, dp);
    }
    private int dp2px(Context context, float defaultVal) {
        float applyDimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultVal, context.getResources().getDisplayMetrics());

        return (int)applyDimension ;
    }
}