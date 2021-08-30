package com.moment.gallery;

import android.app.AlertDialog;

import android.content.*;

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
import com.moment.gallery.common.DataHelper;
import com.moment.gallery.common.GalleryHelper;

import com.moment.gallery.common.ImageHelper;

import java.util.ArrayList;

import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {


    private static final int PIC_FOR_READY = 1;
    private static final int NONE_PIC = 2;
    private static final int REQUEST_FILE = 1000;
    private static final int PIC_FOR_UPDATE = 3;
    private static final int DELETE_FILE = 4;
    private static final String TAG = "MainActivity";
    private TextView progress_circular;
    private ListView mLvItems;
    private List<String> imageUrls;
    private List<String> folderNames;
    private List<Integer> counts;
    private String mMd5;
    private final String fileUrl = getExternalStorageDirectory().getAbsolutePath() + "/DCIM";

    private boolean isChecked = false;
    private AlertDialog alertDialog;
    private String delFileName;

    private List<GalleryHelper.ImageFile> images = new ArrayList<>();

    ImageHelper imageHelper;

    DataHelper dataHelper;

    GalleryHelper galleryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XXPermissions.setScopedStorage(true);

        progress_circular = findViewById(R.id.progress_circular);
        mLvItems = findViewById(R.id.lv_items);

        checkPermission();


        Log.d(TAG, "onCreate: " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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
        mLvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                showListDialog(position);
                return true;
            }
        });

    }

    /**
     * 长按事件控件
     *
     * @param position
     */
    private void showListDialog(int position) {
        final String listItems[] = new String[]{"删除"};

        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle("test");
        listDialog.setIcon(R.mipmap.ic_launcher_round);

    /*
        设置item 不能用setMessage()
        用setItems
        items : listItems[] -> 列表项数组
        listener -> 回调接口
    */
        listDialog.setItems(listItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, listItems[which], Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessage(DELETE_FILE);
                delFileName = fileUrl + "/" + folderNames.get(position);
            }
        });

        //设置按钮
        listDialog.setPositiveButton("确定"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        listDialog.create().show();
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
//                            imageHelper = new ImageHelper(fileUrl);
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
                Log.d(TAG, "run: " + images);
                Log.d(TAG, "run: " + counts);
                Log.d(TAG, "run: " + images.get(3).getFileName());
                Log.d(TAG, "run: " + images.get(3).getUri());
                handler.sendEmptyMessage(PIC_FOR_READY);
//                folderNames = imageHelper.getFolderNames();
//                folderNames = imageHelper.getScanFolder();
//                imageUrls = imageHelper.getThumbNail();
//                counts = imageHelper.getCount();
//                String md5 = Utils.md5(counts.toString());
//                if (folderNames.size() <= 0) {
//                    handler.sendEmptyMessage(NONE_PIC);
//                } else {
//                    if (!md5.equals(mMd5)) {
//                        handler.sendEmptyMessage(PIC_FOR_READY);
//                    }
//                }
//                Log.d("yeor", "run: "+ (counts.retainAll(dataHelper.getCounts())));
//                dataHelper = new DataHelper(imageUrls, folderNames, counts, MainActivity.this);
//                dataHelper.WriteSharedPreferences();
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
                case DELETE_FILE:
                    Log.d("Handler", "handleMessage: delete");
                    Log.d("Handler", delFileName);
//                    imageHelper.delFile(delFileName);
//                    imageThread();

                    break;
            }
        }
    };


    private void initAdapter() {
//        ImageAdapter imageAdapter = new ImageAdapter(this, imageUrls, folderNames, counts);
        ImageAdapter imageAdapter = new ImageAdapter(this, images, counts);
        mLvItems.setAdapter(imageAdapter);
        progress_circular.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_FILE) {
            Log.d("onActivityResult", "onActivityResult: 1000");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
