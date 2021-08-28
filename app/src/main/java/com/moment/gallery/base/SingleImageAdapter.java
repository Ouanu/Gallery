package com.moment.gallery.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class SingleImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> images = new ArrayList<>();
    private String folderUri;
    private String singleImage;

    public SingleImageAdapter(Context mContext, ArrayList<String> images, String folderUri, String singleImage) {
        this.mContext = mContext;
        this.images = images;
        this.folderUri = folderUri;
        this.singleImage = singleImage;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
