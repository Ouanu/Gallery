package com.moment.gallery.common;

import android.util.Log;

import java.io.File;
import java.util.*;

public class ImageHelper {
    private String fileUri;
    private static List<Integer> count = new ArrayList<>();
    private List<String> folderNames = new ArrayList<>();


    public ImageHelper(String fileUri) {
        this.fileUri = fileUri;
    }

    //获取文件夹里图片的数量
    public List<Integer> getCount() {
        List<Integer> resultCount = new ArrayList<>();
        for (Integer integer : count) {
            if (integer > 0) {
                resultCount.add(integer);
            }
        }
        return resultCount;
    }

    //缩略图
    public List<String> getThumbNail() {
        List<String> thumbNails = new ArrayList<>();
        for (String folderName : folderNames) {
            thumbNails.add(fileUri + "/" + folderName + "/" + getImageNames(fileUri + "/" + folderName).get(0));
        }
        Log.d("thumbNails", thumbNails.toString());
        return thumbNails;
    }

    //获取文件夹名称
    public List<String> getFolderNames() {
        File[] files = new File(fileUri).listFiles();
        int cnt;
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

    //获取图片名称
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

    //判断是否为图片
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
     * 按照文件名排序
     *
     * @param filePath
     * @return
     */
    public static ArrayList<String> orderByName(String filePath) {
        ArrayList<String> FileNameList = new ArrayList<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        List fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (File file1 : files) {
            if (file1.isDirectory()) {
                FileNameList.add(file1.getName());
            }
        }
        return FileNameList;
    }

}
