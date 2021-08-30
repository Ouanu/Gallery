package com.moment.gallery.base;

import android.content.ContentUris;
import android.content.Context;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.moment.gallery.R;
import com.moment.gallery.Utils.Utils;
import com.moment.gallery.common.GalleryHelper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagesInFileAdapter extends BaseAdapter {
    private Context mContext;

    private String folderName;

    private GalleryHelper galleryHelper;

    private List<GalleryHelper.Image> imageNameList;


    public ImagesInFileAdapter(Context mContext, String folderName) {
        this.mContext = mContext;
        this.folderName = folderName;
        galleryHelper = GalleryHelper.getInstance(mContext);
        imageNameList = galleryHelper.getImageInFolder(folderName);
    }

    @Override
    public int getCount() {
        return imageNameList.size();
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
//            viewHolder.imageView.setImageURI(Uri.parse(path + "/" + images.get(position)));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        Glide.with(convertView).load(path + "/" + images.get(position)).into(viewHolder.imageView);
        Glide.with(convertView).load(imageNameList.get(position).getImageNameId()).into(viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        LinearLayout linearLayout;
    }
}
