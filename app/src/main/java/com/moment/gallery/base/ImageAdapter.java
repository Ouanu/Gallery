package com.moment.gallery.base;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.moment.gallery.R;
import com.moment.gallery.common.ImageHelper;

import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<String> imageUris;
    private final List<String> fileNames;
    private final List<Integer> counts;

    public ImageAdapter(Context mContext, List<String> imageUris, List<String> fileNames, List<Integer> counts) {
        this.mContext = mContext;
        this.imageUris = imageUris;
        this.fileNames = fileNames;
        this.counts = counts;
    }

    @Override
    public int getCount() {
        return fileNames.size();
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
            viewHolder.textView.setText(fileNames.get(position) + "(" + counts.get(position) + ")");
            Glide.with(mContext).load(imageUris.get(position)).apply(ImageHelper.requestOptions()).into(viewHolder.imageView);
//            viewHolder.imageView.setImageURI(Uri.parse(imageUris.get(position)));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
