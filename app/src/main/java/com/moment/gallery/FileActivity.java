package com.moment.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.moment.gallery.base.ImagesInFileAdapter;
import com.moment.gallery.common.GalleryHelper;

import java.util.ArrayList;
import java.util.List;

public class FileActivity extends AppCompatActivity {

    private TextView mFileName;
    private GridView mGvImages;

    private String folderName;

    private List<Bitmap> thumbnailList;

    ImagesInFileAdapter imagesInFileAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mFileName = findViewById(R.id.file_name);
        mGvImages = findViewById(R.id.gv_images);

        folderName = getIntent().getStringExtra("folderName");
        mFileName.setText(folderName);


        imagesInFileAdapter = new ImagesInFileAdapter(this, folderName);
        mGvImages.setAdapter(imagesInFileAdapter);

        mGvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FileActivity.this, SingleImageActivity.class);
                intent.putExtra("folderName", folderName);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private String reName(String name) {
        if (name.equals("")){
            return "DCIM";
        }
        return name;
    }
}
