package com.zou.fastlibrary.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.zou.fastlibrary.R;


public class ClearWriteEditText extends AppCompatEditText implements View.OnFocusChangeListener , TextWatcher {
    int borderWidth = 1;// 默认1dimpx
    boolean isHasBorder = false;

    int borderColor;// 线条的颜色，默认与字的颜色相同
    int bgColor;// 背景的颜色，默认是透明的
    int mRadius = 3;// 默认3

    int mColorText;

    private Rect rect = new Rect();// 方角

    // 四个角落是否是全是圆角
    boolean isTopLeftCorner = true;
    boolean isBottomLeftCorner = true;
    boolean isTopRightCorner = true;
    boolean isBottomRightCorner = true;

    int mColorBgEnableFalse;
    int mColorTextEnableFalse;
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;

    public ClearWriteEditText(Context context) {
        this(context, null);
    }

    public ClearWriteEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
        borderWidth = getDimen720Px(context, borderWidth);
        mRadius = getDimen720Px(context, mRadius);
        mColorText=mColorTextEnableFalse=borderColor = getCurrentTextColor();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearWriteEditText);
        isHasBorder = mTypedArray.getBoolean(R.styleable.ClearWriteEditText_edBorder, isHasBorder);// 默认无边框
        borderWidth = mTypedArray.getDimensionPixelSize(R.styleable.ClearWriteEditText_edBorderWidth, borderWidth);
        mRadius = mTypedArray.getDimensionPixelSize(R.styleable.ClearWriteEditText_edRadius, mRadius);

        borderColor = mTypedArray.getColor(R.styleable.ClearWriteEditText_edBorderColor, borderColor);

        bgColor = isHasBorder ? Color.TRANSPARENT : Color.RED;
        int bgColor = this.bgColor;
        Log.d("edcolor",mRadius+"");
        this.bgColor = mTypedArray.getColor(R.styleable.ClearWriteEditText_edBgColor, this.bgColor);
        // 四个角落是否全是圆角,默认全是真的
        isTopLeftCorner = mTypedArray.getBoolean(R.styleable.ClearWriteEditText_edTopLeftCorner, isTopLeftCorner);
        isBottomLeftCorner = mTypedArray.getBoolean(R.styleable.ClearWriteEditText_edBottomLeftCorner, isBottomLeftCorner);
        isTopRightCorner = mTypedArray.getBoolean(R.styleable.ClearWriteEditText_edTopRightCorner, isTopRightCorner);
        isBottomRightCorner = mTypedArray.getBoolean(R.styleable.ClearWriteEditText_edBottomRightCorner, isBottomRightCorner);

        mColorBgEnableFalse=mTypedArray.getColor(R.styleable.ClearWriteEditText_edEnableFalseBgColor, this.bgColor);
        mColorTextEnableFalse=mTypedArray.getColor(R.styleable.ClearWriteEditText_edEnableFalseTextColor, mColorTextEnableFalse);
        mTypedArray.recycle();
       // this.setGravity(Gravity.CENTER);// 全部居中显示
        this.setEnabled(isEnabled());
    }

    public ClearWriteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        mClearDrawable = getResources().getDrawable(R.drawable.clear);
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
        this.setOnFocusChangeListener(this);
        this.addTextChangedListener(this);

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
    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        setClearIconVisible(s.length() > 0);
    }


    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchable = event.getX() > (getWidth()
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.startAnimation(shakeAnimation(3));
    }



    /**
     * 晃动动画
     * @param counts 半秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(500);
        return translateAnimation;
    }

    public Drawable getClearDrawable() {
        return mClearDrawable;
    }

    public void setClearDrawable(Drawable mClearDrawable) {
        this.mClearDrawable = mClearDrawable;
    }
}
