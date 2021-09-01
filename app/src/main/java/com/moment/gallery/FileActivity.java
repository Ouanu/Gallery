package com.moment.gallery;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.moment.gallery.base.ImagesInFileAdapter;
import com.moment.gallery.common.GalleryHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.moment.gallery.Utils.Utils.getRealPathFromURI;

public class FileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 1111;
    private TextView mFileName;
    private GridView mGvImages;

    private String folderName;


    ImagesInFileAdapter imagesInFileAdapter;
    private List<GalleryHelper.Image> imageList = new ArrayList<>();
    GalleryHelper galleryHelper = GalleryHelper.getInstance(this);

    private int countDeleteImages = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mFileName = findViewById(R.id.file_name);
        mGvImages = findViewById(R.id.gv_images);

        folderName = getIntent().getStringExtra("folderName");
        mFileName.setText(folderName);


        imageList = galleryHelper.getImageInFolder(folderName);


        imagesInFileAdapter = new ImagesInFileAdapter(this, imageList);

        mGvImages.setAdapter(imagesInFileAdapter);


        mGvImages.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(FileActivity.this, SingleImageActivity.class);
            intent.putExtra("folderName", folderName);
            intent.putExtra("position", position);

            startActivityForResult(intent, REQUEST_IMAGE);

        });

        mGvImages.setOnItemLongClickListener((parent, view, position, id) -> {
            showListDialog(position);
            return true;
        });


    }

    /**
     * 长按事件控件
     *
     * @param position 获得图片位置
     */
    private void showListDialog(int position) {
        final String[] listItems = new String[]{"删除"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle("test");
        listDialog.setIcon(R.drawable.baseline_warning_black_24dp);

    /*
        设置item 不能用setMessage()
        用setItems
        items : listItems[] -> 列表项数组
        listener -> 回调接口
    */
        listDialog.setItems(listItems, (dialog, which) -> {
            Toast.makeText(FileActivity.this, listItems[which], Toast.LENGTH_SHORT).show();
            //删除图片的索引（其他应用还可以搜到）
            boolean checkDelete = galleryHelper.deleteImage(imageList.get(position).getImageNameId());
//            this.getContentResolver().delete(imageList.get(position).getImageNameId(), null, null);
            Toast.makeText(FileActivity.this, "result: " + checkDelete, Toast.LENGTH_SHORT).show();
            imageList.remove(position);
            countDeleteImages++;
            imagesInFileAdapter.notifyDataSetChanged();
            //删除图片“文件”
            String path = getRealPathFromURI(this, imageList.get(position).getImageNameId());
            File file = new File(path);
            if (file.exists()) {
                Log.d("TAG", "showListDialog: yfhjdksahfjdhsajkfhjkdshajfhjdakshfjkdshajkfh");
                file.delete();
            } else {
                Log.d("TAG", "showListDialog: NONEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            }
            notifyLocalMedia(imageList.get(position).getImageNameId());
            updateGallery(imageList.get(position).getImageName());
            mGvImages.setAdapter(imagesInFileAdapter);

//            Log.d("imagesInFileAdapter", "showListDialog: " + imagesInFileAdapter.toString());
        });

        //设置按钮
        listDialog.setPositiveButton("确定"
                , (dialog, which) -> dialog.dismiss());
        listDialog.create().show();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("countDeleteImages", countDeleteImages);
        FileActivity.this.setResult(RESULT_OK, intent);
        Log.d("-----------", "onBackPressed: ______" + countDeleteImages);
        finish();
    }

    //通知MediaStore刷新删除的文件
    private void notifyLocalMedia(Uri imgPath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imgPath);
        this.sendBroadcast(intent);
    }
    // 通知MediaStore刷新新的文件
    private void updateGallery(String filename)//filename是我们的文件全名，包括后缀哦
    {
        MediaScannerConnection.scanFile(this,
                new String[] { filename }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }



}
