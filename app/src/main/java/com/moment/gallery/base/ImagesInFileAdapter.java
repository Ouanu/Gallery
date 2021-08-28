package com.moment.gallery.base;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.moment.gallery.R;
import com.moment.gallery.common.ImageHelper;

import java.util.ArrayList;

public class ImagesInFileAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> images = new ArrayList<>();
    private String path;

    public ImagesInFileAdapter(Context mContext, ArrayList<String> images, String path) {
        this.mContext = mContext;
        this.images = images;
        this.path = path;
    }

    @Override
    public int getCount() {
        return images.size();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.images_in_file_item, null);
            viewHolder.imageView = convertView.findViewById(R.id.iv_image);
            viewHolder.linearLayout = convertView.findViewById(R.id.ll_item);
            Glide.with(convertView).load(path + "/" + images.get(position)).into(viewHolder.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        LinearLayout linearLayout;
    }
}
