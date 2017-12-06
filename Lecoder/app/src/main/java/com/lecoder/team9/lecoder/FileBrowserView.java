package com.lecoder.team9.lecoder;

/**
 * Created by leejinwone on 2017-12-06.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FileBrowserView extends LinearLayout {
    TextView title1;
    TextView title2;
    ImageView imageView;

    public FileBrowserView(Context context) {
        super(context);
        init(context);

    }

    public FileBrowserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public void init(Context context) { //itemView를 초기화 해주는 부분이다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_file_browser, this, true);

        title1 = (TextView) findViewById(R.id.textView1);
        title2 = (TextView) findViewById(R.id.textView2);
        imageView = (ImageView) findViewById(R.id.imageView1);

    }

    public void setTitle1(String name2) {
        title1.setText(name2);
    }

    public void setTitle2(String phone2) {
        title2.setText(phone2);
    }

    public void setImage(int resId) {
        imageView.setImageResource(resId);
    }
}
