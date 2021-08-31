package com.moment.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.moment.gallery.base.ImagesInFileAdapter;
import com.moment.gallery.common.GalleryHelper;

import java.util.ArrayList;
import java.util.List;

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

        mGvImages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showListDialog(position);
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
        listDialog.setIcon(R.drawable.baseline_warning_black_24dp);

    /*
        设置item 不能用setMessage()
        用setItems
        items : listItems[] -> 列表项数组
        listener -> 回调接口
    */
        listDialog.setItems(listItems, (dialog, which) -> {
            Toast.makeText(FileActivity.this, listItems[which], Toast.LENGTH_SHORT).show();
//            handler.sendEmptyMessage(DELETE_FILE);
//            delFileName = images.get(position).getFileName();
            boolean checkDelete = galleryHelper.deleteImage(imageList.get(position).getImageNameId());
            Toast.makeText(FileActivity.this, "result: " + String.valueOf(checkDelete), Toast.LENGTH_SHORT).show();
            imageList.remove(position);
            countDeleteImages++;
            imagesInFileAdapter.notifyDataSetChanged();
        });

        //设置按钮
        listDialog.setPositiveButton("确定"
                , (dialog, which) -> dialog.dismiss());

        listDialog.create().show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("countDeleteImages", 1);
        setResult(RESULT_OK);
        Log.d("-----------", "onBackPressed: ______" + countDeleteImages);
//        setResult(Activity.RESULT_OK);
        super.onBackPressed();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private String reName(String name) {
        if (name.equals("")) {
            return "DCIM";
        }
        return name;
    }
}
