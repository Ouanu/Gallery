package com.moment.gallery;
import android.os.*;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.moment.gallery.base.ImageAdapter;
import com.moment.gallery.common.ImageHelper;

import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {


    private static final int PIC_FOR_READY = 1;
    private static final int NONE_PIC = 2;
    private TextView progress_circular;
    private ListView mLvItems;
    private List<String> imageUrls;
    private List<String> folderNames;
    private List<Integer> counts;
    private final String fileUrl = getExternalStorageDirectory().getAbsolutePath() + "/DCIM";

    private boolean isChecked = false;

    ImageHelper imageHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XXPermissions.setScopedStorage(true);

        progress_circular = findViewById(R.id.progress_circular);
        mLvItems = findViewById(R.id.lv_items);


        checkPermission();

    }

    //检查权限
    private void checkPermission() {
        XXPermissions.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Toast.makeText(MainActivity.this, "获取读写权限成功", Toast.LENGTH_SHORT).show();
                            imageHelper = new ImageHelper(fileUrl);
                            imageThread();
                        } else {
                            Toast.makeText(MainActivity.this, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(MainActivity.this, "被永久拒绝授权，请手动授予读写权限", Toast.LENGTH_SHORT).show();
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            Toast.makeText(MainActivity.this, "获取读写权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //创建线程
    private void imageThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                folderNames = imageHelper.getFolderNames();
                imageUrls = imageHelper.getThumbNail();
                counts = imageHelper.getCount();
                Log.d("imageUrls", imageHelper.getThumbNail().toString());
                Log.d("folderNames", folderNames.toString());
                Log.d("counts", counts.toString());
                if (folderNames.size() <= 0) {
                    handler.sendEmptyMessage(NONE_PIC);
                } else {
                    handler.sendEmptyMessage(PIC_FOR_READY);
                }
            }
        }).start();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PIC_FOR_READY:
                    initAdapter();
                    handler.removeMessages(PIC_FOR_READY);
                    break;
                case NONE_PIC:
                    progress_circular.setText("没有找到图片，也可能没有获取文件读写权限");
                    handler.removeMessages(PIC_FOR_READY);
                    break;
            }
        }
    };

    private void initAdapter() {
        ImageAdapter imageAdapter = new ImageAdapter(this, imageUrls, folderNames, counts);
        mLvItems.setAdapter(imageAdapter);
        progress_circular.setVisibility(View.GONE);
    }


}