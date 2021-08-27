package com.moment.gallery;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.moment.gallery.Utils.FileUtil;
import com.moment.gallery.base.ImageAdapter;
import com.moment.gallery.common.ImageHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {


    private static final int PIC_FOR_READY = 1;
    private TextView progress_circular;
    private ListView mLvItems;
    private List<String> imageUrls;
    private List<String> folderNames;
    private List<Integer> counts;


    private final String fileUrl = getExternalStorageDirectory().getAbsolutePath() + "/DCIM";
    private static boolean isRunning = false;

    ImageHelper imageHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress_circular = findViewById(R.id.progress_circular);
        mLvItems = findViewById(R.id.lv_items);

        imageHelper = new ImageHelper(fileUrl);

        new Thread(new Runnable() {
            @Override
            public void run() {
                folderNames = imageHelper.getFolderNames();
                imageUrls = imageHelper.getThumbNail();
                counts = imageHelper.getCount();
                Log.d("imageUrls", imageHelper.getThumbNail().toString());
                Log.d("folderNames", folderNames.toString());
                Log.d("counts", counts.toString());
                isRunning = true;
                handler.sendEmptyMessage(PIC_FOR_READY);
            }
        }).start();


    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PIC_FOR_READY:
                    initAdapter();
            }
        }
    };

    private void initAdapter() {
        ImageAdapter imageAdapter = new ImageAdapter(this, imageUrls, folderNames, counts);
        mLvItems.setAdapter(imageAdapter);
        progress_circular.setVisibility(View.GONE);
    }
}