package com.moment.gallery.common;

import android.net.Uri;
import android.util.Log;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.*;

public class ImageHelper {
    private final String fileUri;
    private static final List<Integer> count = new ArrayList<>();
    private final List<String> folderNames = new ArrayList<>();


    public ImageHelper(String fileUri) {
        this.fileUri = fileUri;
    }

    /***
     * 获取文件夹里图片的数量
     * @return 返回图片数量
     */
    public List<Integer> getCount() {
        List<Integer> resultCount = new ArrayList<>();
        for (Integer integer : count) {
            if (integer > 0) {
                resultCount.add(integer);
            }
        }
        return resultCount;
    }

    /**
     * 缩略图
     *
     * @return 返回缩略图路径列表
     */
    public List<String> getThumbNail() {
        List<String> thumbNails = new ArrayList<>();
        for (String folderName : folderNames) {
            if (folderName.equals("")) {
                thumbNails.add(fileUri + "/" + getImageNames(fileUri).get(0));
            } else {
                thumbNails.add(fileUri + "/" + folderName + "/" + getImageNames(fileUri + "/" + folderName).get(0));
            }
        }
        Log.d("thumbNails", thumbNails.toString());
        return thumbNails;
    }

    /**
     * 扫描DCIM目录下所有文件及第一层文件夹
     * @return 返回有图片的文件夹名称列表
     */
    public List<String> getScanFolder() {
        List<String> allFolderNames = new ArrayList<>();
        int Count = 0;
        Count = getImageNames(fileUri).size();
        if (Count > 0) {
            allFolderNames.add("");
        }
        getFolderNames();
        folderNames.addAll(allFolderNames);
//        allFolderNames.addAll(getFolderNames());
        count.add(Count);

        return folderNames;
    }

    /**
     * 获取文件夹名称
     *
     * @return 返回文件夹名称列表
     */
    public List<String> getFolderNames() {
        File[] files = new File(fileUri).listFiles();
        int cnt;
        int dcimCount = 0;
        for (File file : files) {
            String path = fileUri + "/" + file.getName();
            if (file.getName().startsWith(".") || isImage(file)) {
                continue;
            } else {
                if ((cnt = getImageNames(path).size()) > 0) {
                    folderNames.add(file.getName());
                    count.add(cnt);
                }
            }
        }
        return folderNames;
    }


    /**
     * 获取图片名称
     *
     * @param folderUri
     * @return 返回图片名称列表
     */
    public List<String> getImageNames(String folderUri) {
        List<String> imageNames = new ArrayList<>();
        File[] files = new File(folderUri).listFiles();
        for (File file : files) {
            if (isImage(file)) {
                imageNames.add(file.getName());
            }
        }
        Log.d("imageNames", imageNames.toString());
        return imageNames;
    }

    /**
     * 判断是否为图片
     *
     * @param file
     * @return 返回判断
     */
    private boolean isImage(File file) {
        if (file.getName().endsWith(".jpg")) {
            return true;
        } else if (file.getName().endsWith(".jpeg")) {
            return true;
        } else if (file.getName().endsWith(".png")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 利用Glide设置圆角图片
     *
     * @return 返回设置
     */
    public static RequestOptions requestOptions() {
        RoundedCorners roundedCorners = new RoundedCorners(20);//数字为圆角度数
        RequestOptions coverRequestOptions = new RequestOptions()
                .transforms(new CenterCrop(), roundedCorners)
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                .skipMemoryCache(true);//不做内存缓存
        //Glide 加载图片简单用法
//        Glide.with(context).load(path).apply(coverRequestOptions).into(imageView);
        return coverRequestOptions;
    }

    /**
     * 删除文件
     * @param fileUri
     */
    public void delFile(String fileUri) {
        File file = new File(fileUri);
        file.delete();
    }

}
