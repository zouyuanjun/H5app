package com.zou.fastlibrary.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.zou.fastlibrary.R;

public class BGLinearlayout extends LinearLayout {
    int strokecolor;
    float strokesize;
    float mRadius;
    int bgdividecolor;
    float bgdividesize;
    float bgdividepaddingleft;
    float bgdividepaddingright;

    int bgcolor;
    private Rect rect = new Rect();// 方角

    public BGLinearlayout(Context context) {
        super(context);
    }

    public BGLinearlayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initview(context,attrs);
    }

    public BGLinearlayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview(context,attrs);
    }

    public void initview(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGLinearlayout);
        int totalAttributes = typedArray.getIndexCount();
        for (int i = 0; i < totalAttributes; i++) {
            int index = typedArray.getIndex(i);
            if (index == R.styleable.BGLinearlayout_bgdividecolor) {
                bgdividecolor = typedArray.getColor(index, Color.WHITE);

            } else if (index == R.styleable.BGLinearlayout_bgdividePaddingLeft) {
                bgdividepaddingleft = typedArray.getDimension(index, 0);
            }else if (index == R.styleable.BGLinearlayout_bgdividePaddingRight) {
                bgdividepaddingright = typedArray.getDimension(index, 0);
            }else if (index == R.styleable.BGLinearlayout_bgdividesize) {
                bgdividesize = typedArray.getDimension(index, 0);
            }else if (index == R.styleable.BGLinearlayout_bgradius) {
                mRadius = typedArray.getDimension(index, 0);
            }else if (index == R.styleable.BGLinearlayout_strokecolor) {
                strokecolor = typedArray.getColor(index, 0);
            }else if (index == R.styleable.BGLinearlayout_strokesize) {
                strokesize = typedArray.getDimension(index, 0);
            }else if (index == R.styleable.BGLinearlayout_bgcolor) {
                bgcolor = typedArray.getColor(index, 0);
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("宽度大小",getWidth()+"");
        float ffVar[] = getOutterRadii();
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(ffVar, null, null));
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(bgcolor);
        shapeDrawable.setBounds(rect);
        shapeDrawable.draw(canvas);
        //是否有描边
        if (strokesize>0){
            RectF rectF = new RectF(strokesize, strokesize, strokesize, strokesize);
            ShapeDrawable shapeDrawable2 = new ShapeDrawable(new RoundRectShape(ffVar, rectF, ffVar));
            Paint paint2 = shapeDrawable2.getPaint();
            paint2.setColor(strokecolor);
            shapeDrawable2.setBounds(rect);
            shapeDrawable2.draw(canvas);
        }
        //是否有分割线
        if (bgdividesize>0){
            ShapeDrawable shapeDrawable2 = new ShapeDrawable(new RectShape());
            Paint paint2 = shapeDrawable2.getPaint();
            paint2.setColor(bgdividecolor);
            rect.top=rect.bottom-(int) bgdividesize;
            rect.left=rect.left+(int) bgdividepaddingleft;
            rect.right=rect.right-(int) bgdividepaddingright;
            shapeDrawable2.setBounds(rect);
            shapeDrawable2.draw(canvas);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.left = 0;
        rect.top = 0;
        rect.bottom = h;
        rect.right = w;
    }

    private float[] getOutterRadii() {
        float fRandis[] = { mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius };
        Log.d("圆角大小",mRadius+"");
//        if (isTopLeftCorner == false) {
//            fRandis[0] = 0;
//            fRandis[1] = 0;
//        }
//        if (isTopRightCorner == false) {
//            fRandis[2] = 0;
//            fRandis[3] = 0;
//        }
//        if (isBottomLeftCorner == false) {
//            fRandis[4] = 0;
//            fRandis[5] = 0;
//        }
//        if (isBottomRightCorner == false) {
//            fRandis[6] = 0;
//            fRandis[7] = 0;
//        }
        return fRandis;
    }
    public void setBgcolor(int color){
        bgcolor=color;
        invalidate();
    }

    public void setStrokecolor(int strokecolor) {
        this.strokecolor = strokecolor;
        invalidate();
    }
}
