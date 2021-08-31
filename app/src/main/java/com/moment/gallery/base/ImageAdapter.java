package com.moment.gallery.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.moment.gallery.R;
import com.moment.gallery.Utils.Utils;
import com.moment.gallery.common.GalleryHelper;

import java.io.IOException;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<GalleryHelper.ImageFile> imagefolders;
    private final List<Integer> counts;

    public ImageAdapter(Context mContext, List<GalleryHelper.ImageFile> imagefolders, List<Integer> counts) {
        this.mContext = mContext;
        this.imagefolders = imagefolders;
        this.counts = counts;
    }

    @Override
    public int getCount() {
        return imagefolders.size();
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
            convertView = View.inflate(mContext, R.layout.file_item, null);
            viewHolder.textView = convertView.findViewById(R.id.file_name);
            viewHolder.imageView = convertView.findViewById(R.id.iv_photo);
            //            viewHolder.imageView.setImageURI(Uri.parse(imageUris.get(position)));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(imagefolders.get(position).getFileName() + "(" + counts.get(position) + ")");
        Glide.with(mContext).load(imagefolders.get(position).getUri()).apply(Utils.getCoverRequestOptions()).into(viewHolder.imageView);

        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

//    private String getName(String name){
//        if (name.equals("")) {
//            return "DCIM";
//        }
//        return name;
//    }

}
