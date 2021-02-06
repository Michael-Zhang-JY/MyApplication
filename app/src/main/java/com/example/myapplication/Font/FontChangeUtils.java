package com.example.myapplication.Font;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class FontChangeUtils {

    private final static String PINGFANG = "fonts/pingfangbold.ttf";

    public static void replaceCustomFont(View rootView){
        if (rootView == null){
            return;
        }
        if (rootView instanceof TextView){
            TextView textView = (TextView) rootView;
            int fontStyle = Typeface.NORMAL;
            if (textView.getTypeface() != null){
                fontStyle = textView.getTypeface().getStyle();
            }
            Typeface customFont = Typeface.createFromAsset(rootView.getContext().getAssets(), PINGFANG);
            textView.setTypeface(customFont, fontStyle);
        }
    }
}
