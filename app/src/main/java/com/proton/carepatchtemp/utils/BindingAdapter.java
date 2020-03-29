package com.proton.carepatchtemp.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by wangmengsi on 2017/9/7.
 */

public class BindingAdapter {
    @android.databinding.BindingAdapter({"android:src"})
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);
    }

    @android.databinding.BindingAdapter({"android:imageURI"})
    public static void setImageURI(SimpleDraweeView view, String url) {
        view.setImageURI(url);
    }

    @android.databinding.BindingAdapter({"android:imageURI"})
    public static void setImageURI(SimpleDraweeView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @android.databinding.BindingAdapter({"android:textColor"})
    public static void setTextColor(TextView view, String textColor) {
        view.setTextColor(Color.parseColor(textColor));
    }
}
