package com.moment.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.moment.gallery.base.ImagesInFileAdapter;

import java.util.ArrayList;

public class FileActivity extends AppCompatActivity {

    private TextView mFileName;
    private GridView mGvImages;

    private ArrayList<String> images = new ArrayList<>();
    private String folderUri;
    private String fileUri;
    ImagesInFileAdapter imagesInFileAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mFileName = findViewById(R.id.file_name);
        mGvImages = findViewById(R.id.gv_images);

        mFileName.setText(getIntent().getStringExtra("fileName"));
        images = getIntent().getStringArrayListExtra("images");
        folderUri = getIntent().getStringExtra("folderUri");

        Log.d("FileActivity", images.toString());
        Log.d("FileActivity", folderUri.toString());

        imagesInFileAdapter = new ImagesInFileAdapter(this, images, folderUri);
        mGvImages.setAdapter(imagesInFileAdapter);

        mGvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FileActivity.this, SingleImageActivity.class);
                intent.putStringArrayListExtra("images", images);
                intent.putExtra("singleImage", images.get(position));
                intent.putExtra("folderUri", folderUri);
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
}
