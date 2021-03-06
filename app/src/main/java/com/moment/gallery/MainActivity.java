package com.moment.gallery;

import android.content.*;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
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
import com.moment.gallery.base.ImageAdapter;
import com.moment.gallery.common.GalleryHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int PIC_FOR_READY = 1;
    private static final int NONE_PIC = 2;
    private static final int REQUEST_FILE = 1000;
    private static final int PIC_FOR_UPDATE = 3;
    private static final int DELETE_FILE = 4;
    private static final String TAG = "MainActivity";
    private TextView progress_circular;
    private ListView mLvItems;
    private List<Integer> counts;


    private List<GalleryHelper.ImageFile> images = new ArrayList<>();


    GalleryHelper galleryHelper;
    ImageAdapter imageAdapter;

    private int goFilePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XXPermissions.setScopedStorage(true);

        progress_circular = findViewById(R.id.progress_circular);
        mLvItems = findViewById(R.id.lv_items);

        checkPermission();

        Log.d(TAG, "onCreate: " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//        Log.d(TAG, "------------- " + galleryHelper.getCountBucketNames().keySet());

        /**
         * 点击列表的元素，携带该文件夹所有的图片、文件名进到下一个页面
         */
        mLvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String folderName = images.get(position).getFileName();
                Intent intent = new Intent(MainActivity.this, FileActivity.class);
                intent.putExtra("folderName", folderName);
                goFilePosition = position;
                Log.d(TAG, "onItemClick: " + images.get(position));
                startActivityForResult(intent, REQUEST_FILE);

            }
        });
//        mLvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                showListDialog(position);
//                return true;
//            }
//        });


    }


    //检查权限
    private void checkPermission() {
        XXPermissions.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
//                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Toast.makeText(MainActivity.this, "获取读写权限成功", Toast.LENGTH_SHORT).show();
                            galleryHelper = GalleryHelper.getInstance(MainActivity.this);

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
                galleryHelper.getImageList();
                galleryHelper.getImageInFileList();
                images = galleryHelper.getImageFileList();
                counts = galleryHelper.getCountList();
                imageAdapter = new ImageAdapter(MainActivity.this, images, counts);
                handler.sendEmptyMessage(PIC_FOR_READY);
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
        imageAdapter.notifyDataSetChanged();
        mLvItems.setAdapter(imageAdapter);
        progress_circular.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_FILE) {
            int countDeleteImages = data.getIntExtra("countDeleteImages", 0);
            Log.d(TAG, "onActivityResult: " + countDeleteImages);
            if (countDeleteImages > 0) {
                int cnt = counts.get(goFilePosition).intValue() - countDeleteImages;
                if (cnt > 0) {
                    counts.set(goFilePosition, cnt);
                } else {
                    counts.remove(goFilePosition);
                    images.remove(goFilePosition);
                    handler.sendEmptyMessage(PIC_FOR_READY);


                }

            }
            imageAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
