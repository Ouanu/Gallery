package com.moment.gallery;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.moment.gallery.common.GalleryHelper;

import java.util.ArrayList;
import java.util.List;

public class SingleImageActivity extends AppCompatActivity {
    private static final int TITLE_BAR = 1;
    private static final int SHOW_BAR = 2;
    private static final int HIDE_BAR = 3;

    private ImageView mIvSingleImage;
    private TextView mTvTitle;

    private static boolean isShow = true;

    private static final int SINGLE = 1;
    private static final int DOUBLE = 2;
    private static final int NONE = 0;

    private int checkFin = NONE;

    /**
     * 处理手势的变量---------------
     */
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    //第一、二个触点坐标
    private PointF fstPoint = new PointF();
    private PointF secPoint = new PointF();
    //两点距离
    private float dis = 1f;
    //两点之间的中点
    private PointF midPoint = new PointF();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);

        mIvSingleImage = findViewById(R.id.iv_singleImage);
        mTvTitle = findViewById(R.id.tv_title);

        String folderName;
        int position;
        folderName = getIntent().getStringExtra("folderName");
        position = getIntent().getIntExtra("position", 0);

        GalleryHelper galleryHelper = GalleryHelper.getInstance(this);
        List<GalleryHelper.Image> imageList = new ArrayList<>();
        imageList = galleryHelper.getImageInFolder(folderName);


        mIvSingleImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        mIvSingleImage.setImageURI(imageList.get(position).getImageNameId());
        Glide.with(this).load(imageList.get(position).getImageNameId()).into(mIvSingleImage);
        mTvTitle.setText(imageList.get(position).getImageName());

        handler.sendEmptyMessageDelayed(TITLE_BAR, 5000);

        zoomImage();

    }



    private PointF getMidPoint(MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;

        return new PointF(x, y);
    }

    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TITLE_BAR:
                    if (isShow) {
                        HideTitle();
                        isShow = false;
                    } else {
                        ShowTitle();
                        isShow = true;
                    }
                    removeMessages(TITLE_BAR);
                    break;
                case SHOW_BAR:
                    ShowTitle();
                    removeMessages(HIDE_BAR);
                    removeMessages(SHOW_BAR);
                    sendEmptyMessageDelayed(HIDE_BAR, 3000);
                    break;
                case HIDE_BAR:
                    HideTitle();
                    removeMessages(HIDE_BAR);
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

    private void zoomImage() {
        mIvSingleImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                mTvTitle.setVisibility(View.VISIBLE);
                view.setScaleType(ImageView.ScaleType.MATRIX);
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();
                Log.d("TAG", "onTouch: x= " + x + "y=" + y);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        //手指按下事件
                        matrix.set(view.getImageMatrix());
                        savedMatrix.set(matrix);
                        fstPoint.set(event.getX(), event.getY());
                        checkFin = SINGLE;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        //屏幕上已经有一个点被按住了 第二个点被按下时触发该事件
                        secPoint.set(event.getX(), event.getY());
                        dis = getDistance(event);
                        if (dis > 10f) {
                            savedMatrix.set(matrix);
                            midPoint = getMidPoint(event);
                            checkFin = DOUBLE;
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        //手指移动时触发事件
                        if (checkFin == SINGLE) {
                            //单点移动
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - fstPoint.x,
                                    event.getY() - fstPoint.y);
                            view.setImageMatrix(matrix);
                        } else if (checkFin == DOUBLE) {
                            //两点移动
                            float newDis = getDistance(event);
                            if (newDis > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDis / dis;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
//                                view.setImageMatrix(matrix);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        //屏幕上已经有两个点按住 再松开一个点时触发该事件
                        checkFin = NONE;
                        break;

                }
                view.setImageMatrix(matrix);
                handler.sendEmptyMessage(SHOW_BAR);
                return true;
            }

        });
    }

}
