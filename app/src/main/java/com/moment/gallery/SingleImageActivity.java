package com.moment.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SingleImageActivity extends AppCompatActivity {
    private static final int TITLE_BAR = 1;
    private static final int STOP = 2;
    private ArrayList<String> images = new ArrayList<>();
    private String folderUri;
    private String singleImage;

    private ImageView mIvSingleImage;
    private TextView mTvTitle;

    private static boolean isShow = true;
    private RelativeLayout mRlSingleImage;
//    private GridView mGvThumbnail;

//    ImagesInFileAdapter imageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);

        mIvSingleImage = findViewById(R.id.iv_singleImage);
        mTvTitle = findViewById(R.id.tv_title);
        mRlSingleImage = findViewById(R.id.rl_singleImage);
//        mGvThumbnail = findViewById(R.id.gv_thumbnail);

        images = getIntent().getStringArrayListExtra("images");
        singleImage = getIntent().getStringExtra("singleImage");
        folderUri = getIntent().getStringExtra("folderUri");
        Log.d("SingleImageActivity", singleImage.toString());
        Log.d("SingleImageActivity", folderUri.toString());

        mIvSingleImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mIvSingleImage.setImageURI(Uri.parse(folderUri + "/" + singleImage));
        mTvTitle.setText(singleImage);

//        imageAdapter = new ImagesInFileAdapter(this, images, folderUri);
//
//        mGvThumbnail.setAdapter(imageAdapter);


        mIvSingleImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        //手指按下事件
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        //屏幕上已经有一个点被按住了 第二个点被按下时触发该事件

                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        //屏幕上已经有两个点按住 再松开一个点时触发该事件

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //手指移动时触发事件

                        break;
                    case MotionEvent.ACTION_UP:
                        //手指松开时触发事件
                        handler.sendEmptyMessage(TITLE_BAR);
                        break;
                }
                return true;
            }

        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TITLE_BAR:
                    if (isShow) {
                        HideTitle();
                    } else {
                        ShowTitle();
                    }
                    removeMessages(TITLE_BAR);
                    break;
                case STOP:
                    removeMessages(TITLE_BAR);
                    removeMessages(STOP);
                    break;
            }
        }
    };

    private void ShowTitle() {
        mTvTitle.setVisibility(View.VISIBLE);
        isShow = true;
    }

    private void HideTitle() {
        mTvTitle.setVisibility(View.GONE);
        isShow = false;
    }

}
