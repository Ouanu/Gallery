package com.moment.gallery.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.moment.gallery.Utils.Utils;


import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DataHelper {
    private List<String> imageUrls;
    private List<String> folderNames;
    private List<Integer> counts;
    private Context mContext;
    private String md5;
    public int cnt;

    public String getMd5() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", MODE_PRIVATE);
        md5 = sharedPreferences.getString("md5", "0");
        return md5;
    }

    public DataHelper(List<String> imageUrls, List<String> folderNames, List<Integer> counts, Context mContext) {
        this.imageUrls = imageUrls;
        this.folderNames = folderNames;
        this.counts = counts;
        this.mContext = mContext;
    }

    public void WriteSharedPreferences() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("data", MODE_PRIVATE).edit();
        int i = 0;
        for (String folderName : folderNames) {
            editor.putString("imageUrls" + i, imageUrls.get(i));
            editor.putString("folderNames" + i, folderNames.get(i));
            editor.putInt("counts" + i, counts.get(i));
            i++;
        }
        editor.putString("md5", Utils.md5(counts.toString()));
        editor.putInt("Nums", i);
        editor.commit();
    }

    public List<String> getImageUrls() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", MODE_PRIVATE);
        List<String> mImageUrls = new ArrayList<>();
        cnt = sharedPreferences.getInt("Nums", 0);
        for (int i = 0; i < cnt; i++) {
            mImageUrls.add(sharedPreferences.getString("imageUrls" + i, ""));
        }
        return mImageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getFolderNames() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", MODE_PRIVATE);

        List<String> mFolderNames = new ArrayList<>();
        cnt = sharedPreferences.getInt("Nums", 0);
        for (int i = 0; i < cnt; i++) {
            mFolderNames.add(sharedPreferences.getString("folderNames" + i, ""));
        }
        return mFolderNames;
    }

    public void setFolderNames(List<String> folderNames) {
        this.folderNames = folderNames;
    }

    public List<Integer> getCounts() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", MODE_PRIVATE);
        List<Integer> mCounts = new ArrayList<>();
        cnt = sharedPreferences.getInt("Nums", 0);
        for (int i = 0; i < cnt; i++) {
            mCounts.add(sharedPreferences.getInt("counts" + i, 0));
        }
        return mCounts;
    }

    public void setCounts(List<Integer> counts) {
        this.counts = counts;
    }
}
