package com.moment.gallery;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;

import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.moment.gallery.Utils.Utils;
import com.moment.gallery.base.ImageAdapter;
import com.moment.gallery.common.DataHelper;
import com.moment.gallery.common.ImageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {


    private static final int PIC_FOR_READY = 1;
    private static final int NONE_PIC = 2;
    private static final int REQUEST_FILE = 1000;
    private static final int PIC_FOR_UPDATE = 3;
    private TextView progress_circular;
    private ListView mLvItems;
    private List<String> imageUrls;
    private List<String> folderNames;
    private List<Integer> counts;
    private String mMd5;
    private final String fileUrl = getExternalStorageDirectory().getAbsolutePath() + "/DCIM";

    private boolean isChecked = false;

    ImageHelper imageHelper;

    DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XXPermissions.setScopedStorage(true);

        progress_circular = findViewById(R.id.progress_circular);
        mLvItems = findViewById(R.id.lv_items);


        checkPermission();

        /**
         * 检查本地是否有上次打开存储的记录
         * 若有，则直接构建；否或无图片资源，则等待；
         */
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        if (sharedPreferences.getInt("Nums", 0) != 0) {
            dataHelper = new DataHelper(imageUrls, folderNames, counts, MainActivity.this);
            folderNames = dataHelper.getFolderNames();
            imageUrls = dataHelper.getImageUrls();
            counts = dataHelper.getCounts();
            mMd5 = dataHelper.getMd5();
            initAdapter();

        }

        /**
         * 点击列表的元素，携带该文件夹所有的图片、文件名进到下一个页面
         */
        mLvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Path = fileUrl + "/" + folderNames.get(position);
                ArrayList<String> images = (ArrayList<String>) imageHelper.getImageNames(Path);
                Intent intent = new Intent(MainActivity.this, FileActivity.class);
                intent.putStringArrayListExtra("images", images);
                intent.putExtra("folderUri", fileUrl + "/" + folderNames.get(position));
                intent.putExtra("fileName", folderNames.get(position));
                startActivityForResult(intent, REQUEST_FILE);
//                startActivity(intent);
            }
        });

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
                            handler.sendEmptyMessage(NONE_PIC);
                        }
                    }
                });

    }


    //创建线程
    private void imageThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                folderNames = imageHelper.getFolderNames();
                folderNames = imageHelper.getScanFolder();
                imageUrls = imageHelper.getThumbNail();
                counts = imageHelper.getCount();
                String md5 = Utils.md5(counts.toString());
                if (folderNames.size() <= 0) {
                    handler.sendEmptyMessage(NONE_PIC);
                } else {
                    if (!md5.equals(mMd5)) {
                        handler.sendEmptyMessage(PIC_FOR_READY);
                    }
                }
//                Log.d("yeor", "run: "+ (counts.retainAll(dataHelper.getCounts())));
                dataHelper = new DataHelper(imageUrls, folderNames, counts, MainActivity.this);
                dataHelper.WriteSharedPreferences();
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
                    handler.removeMessages(NONE_PIC);
                    break;
                case PIC_FOR_UPDATE:
                    initAdapter();
                    break;
            }
        }
    };



    private void initAdapter() {
        ImageAdapter imageAdapter = new ImageAdapter(this, imageUrls, folderNames, counts);
        mLvItems.setAdapter(imageAdapter);
        progress_circular.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_FILE) {
            Log.d("onActivityResult", "onActivityResult: 1000");
//            imageThread();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}