package com.moment.gallery.common;

import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GalleryHelper {

    private static final String TAG = "GalleryHelper";

    private Context context;

    private List<Image> imageList = new ArrayList<>();
    private HashMap<String, Integer> countBucketNames = new HashMap<>();
    private List<ImageFile> imageFileList = new ArrayList<>();
    private List<Integer> countList = new ArrayList<>();

    public static GalleryHelper instance;

    public static GalleryHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GalleryHelper(context);
        }
        return instance;
    }

    public GalleryHelper(Context context) {
        this.context = context;
    }

    public List<ImageFile> getImageFileList() {
        return imageFileList;
    }

    public List<Integer> getCountList() {
        return countList;
    }

    String[] projection = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED
    };

    String selection = MediaStore.Images.Media.SIZE + " >= 81920";

    String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " ASC";


    /**
     * 获得共享存储的所有图片
     */
    public void getImageList() {

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
        )) {
            int name_id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int bucket_id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            int name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int bucket_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int size = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int date = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);

            while (cursor.moveToNext()) {
                long imageId = cursor.getLong(name_id);
                long bucketId = cursor.getLong(bucket_id);
                String imageName = cursor.getString(name);
                String bucketName = cursor.getString(bucket_name);
                int imageSize = cursor.getInt(size);
                String dateModified = cursor.getString(date);

                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
                imageList.add(new Image(imageName, bucketName, contentUri, bucketId, imageSize, dateModified));
            }

            for (Image image : imageList) {
                if (!"".equals(image.bucketName) && image.bucketName != null) {
                    Integer cnt = countBucketNames.get(image.getBucketName());
                    countBucketNames.put(image.bucketName, (cnt == null) ? 1 : cnt + 1);
                }
            }
        }
    }

    /**
     * ImageFileList --> 文件夹名称 + 图片Uri(首页）
     */
    public void getImageInFileList() {
        List<Uri> imageId = new ArrayList<>();
        List<String> imageName = new ArrayList<>();
        int i = 0;
        int cnt = 0;
        for (String s : countBucketNames.keySet()) {
            if (s != null && s.length() > 0) {
                for (Image image : imageList) {
                    if (s.equals(image.getBucketName())) {
                        imageName.add(image.getImageName());
                        imageId.add(image.getImageNameId());
                        i++;
                        cnt++;
                    }
                }
            }

            countList.add(cnt);
            cnt = 0;
            imageFileList.add(new ImageFile(s, imageId.get(i - 1)));

        }
    }

    /**
     * 首页Item指向的文件夹里的所有图片
     */
    public List<Image> getImageInFolder(String folderName) {
        List<Image> images = new ArrayList<>();
        for (Image image : imageList) {
            if (image != null) {
                if (folderName.equals(image.bucketName)) {
                    images.add(image);
                }
            }
        }
        return images;
    }


    /**
     * 移除指定图片
     * @param uri
     * @return
     */
    public boolean deleteImage(Uri uri) {
        // URI of the image to remove.
        // WHERE clause.
        String selection = null;
        String[] selectionArgs = null;
//        File file = new File(String.valueOf(uri));
//        if (file.exists()) {
//            file.delete();
//        }
        // Perform the actual removal.
        int numImagesRemoved = context.getContentResolver().delete(
                uri,
                selection,
                selectionArgs);
        if (numImagesRemoved > 0) {
            return true;
        } else {
            return false;
        }


    }



    public class Image {


        private final String imageName;
        private final String bucketName;
        private final Uri imageNameId;
        private final long bucketNameId;
        private final int imageSize;
        private final String dateModified;

        public Image(String imageName, String bucketName, Uri imageNameId, long bucketNameId, int imageSize, String dateModified) {
            this.imageName = imageName;
            this.bucketName = bucketName;
            this.imageNameId = imageNameId;
            this.bucketNameId = bucketNameId;
            this.imageSize = imageSize;
            this.dateModified = dateModified;
        }

        public String getImageName() {
            return imageName;
        }

        public String getBucketName() {
            return bucketName;
        }

        public Uri getImageNameId() {
            return imageNameId;
        }

        public long getBucketNameId() {
            return bucketNameId;
        }

        public int getImageSize() {
            return imageSize;
        }

        public String getDateModified() {
            return dateModified;
        }


    }

    public class ImageFile {
        String fileName;
        Uri uri;

        public ImageFile(String fileName, Uri uri) {
            this.fileName = fileName;
            this.uri = uri;
        }

        public String getFileName() {
            return fileName;
        }

        public Uri getUri() {
            return uri;
        }
    }

    public List<Bitmap> thumbnail(String folderName) {
        List<Image> imageList = getImageInFolder(folderName);
        List<Bitmap> thumbnailList = new ArrayList<>();
        int i = 0;
        for (GalleryHelper.Image image : imageList) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                Bitmap thumbnail = null;
                try {
                    thumbnail = context.getContentResolver().loadThumbnail(imageList.get(i).getImageNameId(),
                            new Size(640, 840), null);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                thumbnailList.add(thumbnail);
            }
            i++;
        }
        return thumbnailList;
    }


}
