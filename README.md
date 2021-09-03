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


# Author
* Ouanu

# Usage

* GalleryHelper 使用方法：

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
            }
        }
    };
    
private void initAdapter() {
        imageAdapter.notifyDataSetChanged();
        mLvItems.setAdapter(imageAdapter);
        progress_circular.setVisibility(View.GONE);
    }

    

    /**
     * 首页Item指向的文件夹里的所有图片(用于下一页）
     */
     
     List<Image> imageFolder = galleryHelper.getImageInFolder(String folderName);
     

    /**
     * 将URI路径转化为path路径
     */
     
     String realPath = getRealPathFromURI(Context context, Uri contentURI);
     
     /**
     * 移除指定图片
     * @param uri
     * @return
     */
    boolean deleteResult = galleryHelper.deleteImage(Uri uri);
    
  
