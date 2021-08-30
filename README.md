# Gallery（相册）

（集成了 Glide 、 XXPermissions 框架）

模块：

common:

* GalleryHelper 获得所有共享存储空间的照片（大于80k）


utils：

* 生成MD5的工具

base：（适配器）

* ImageAdapter 首页item的格式

* SingleImageAdapter 次要页面及展示页面的格式

Activity:

* MainActivity首页

* FileActivity 首页item所指向的页面

* SingleImageActivity 次页item指向的单张图片展示


# Usage

GalleryHelper 使用方法：

`````/
GalleryHelper galleryHelper = GalleryHelper.getInstance(MainActivity.this);

new Thread(new Runnable() {
            @Override
            public void run() {
                galleryHelper.getImageList();   
                galleryHelper.getImageInFileList();
                images = galleryHelper.getImageFileList();
                counts = galleryHelper.getCountList();
                handler.sendEmptyMessage(PIC_FOR_READY);
            }
        }).start();

private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PIC_FOR_READY:
                    initAdapter();
                    handler.removeMessages(PIC_FOR_READY);
                    break;
                case NONE_PIC:
                    progress_circular.setText("没有找到图片，也可能没有获取文件读写权限");
                    handler.removeMessages(NONE_PIC);
                    break;
                case PIC_FOR_UPDATE:
                    initAdapter();
                    break;
                case DELETE_FILE:
                    Log.d("Handler", "handleMessage: delete");
                    Log.d("Handler", delFileName);
                    break;
            }
        }
    };
    
private void initAdapter() {
        ImageAdapter imageAdapter = new ImageAdapter(this, images, counts);
        mLvItems.setAdapter(imageAdapter);
        progress_circular.setVisibility(View.GONE);
    }
    
    
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

            Log.d(TAG, "------------- " + countBucketNames.keySet());
            Log.d(TAG, "------------- " + countBucketNames.values());
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
    
    public List<ImageFile> getImageFileList() {
        return imageFileList;
    }
    

    /**
     * 首页Item指向的文件夹里的所有图片(用于下一页）
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
