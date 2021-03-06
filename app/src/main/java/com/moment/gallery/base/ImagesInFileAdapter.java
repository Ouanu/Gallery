package com.moment.gallery.base;


import android.content.Context;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.moment.gallery.R;

import com.moment.gallery.common.GalleryHelper;


import java.util.List;



public class ImagesInFileAdapter extends BaseAdapter {
    private Context mContext;

    private List<GalleryHelper.Image> imageNameList;


    public ImagesInFileAdapter(Context mContext, List<GalleryHelper.Image> imageNameList) {
        this.mContext = mContext;
        this.imageNameList = imageNameList;
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Glide.with(convertView).load(imageNameList.get(position).getImageNameId()).into(viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        LinearLayout linearLayout;
    }


}
