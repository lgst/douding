package com.ddgj.dd.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.ddgj.dd.R;


/**
 * Created by Administrator on 2015/6/16.
 */
public class ClearEditText extends EditText {
    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;
    private boolean isVisible;
    private boolean isShowClear;

    /**
     * ClearEditText无参的构造方法会默认回调父类无参的构造器，父类无无参的构造器，实现父类有参的构造器
     * @param context
     * java代码new时使用
     */
    public ClearEditText(Context context) {
        this(context, null);
    }

    /**
     * xml用的
     * @param context
     * @param attrs   属性
     */
    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs,android.R.attr.editTextStyle);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr   风格
     */
    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //获取EditText左边的图片，EditText的图片默认按左上右下设的
        mDrawableLeft = getCompoundDrawables()[0];
        if (null == mDrawableLeft) {
            mDrawableLeft= getResources().getDrawable(R.mipmap.login_left_user_name);
        }
        //获取EditText右边的图片
        mDrawableRight = getCompoundDrawables()[2];
        if (null == mDrawableRight) {
            mDrawableRight = getResources().getDrawable(R.mipmap.ic_highlight_remove_grey600_24dp);
        }
        //设置四个方向的图标，方向为左上右下
        setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, null,null, null);
        //给ClearEditText设置文字改变的监听事件
        addTextChangedListener(new TextWatcher() {
            //文字改变之前，当触碰到EditText时
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //文字改变的时候。参数:1.输入的内容2.文字变化开始的位置3.被替换的文字的长度4.总共有多少字
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)){
                    setRightVisible(false);
                }else {
//                    ContactsUtil.getInstance(getContext()).getContactsInfo();
                    setRightVisible(true);
                }
//                setRightVisible(!TextUtils.isEmpty(s));
            }
            //文字改变之后
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void setRightVisible(boolean isVisible) {
        //设置四个方向的图标
//        setCompoundDrawables(mDrawableLeft, null, isVisible ? mDrawableRight : null, null);
        setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, null, isVisible ? mDrawableRight : null, null);
        isShowClear = isVisible;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        if (isShowClear && ((x > getWidth() - getPaddingRight() - mDrawableRight.getIntrinsicWidth() && x < getWidth() - getPaddingRight()))) {
            if (null != onTouch) {
                onTouch.onTouch();
            }
            setText("");
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 释放资源，由gc自动回调
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mDrawableRight = null;
        mDrawableLeft = null;
    }

    private OnTouch onTouch;

    public void setOnTouch(OnTouch onTouch) {
        this.onTouch = onTouch;
    }

    public interface OnTouch {
        void onTouch();
    }

}
