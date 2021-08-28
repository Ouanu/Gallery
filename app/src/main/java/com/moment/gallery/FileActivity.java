package com.moment.gallery;

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

        imagesInFileAdapter = new ImagesInFileAdapter(this, images, folderUri);
        mGvImages.setAdapter(imagesInFileAdapter);

        mGvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


    }
}
