# Gallery（相册）
模块：

common:

* DataHelper 保存前一次扫描文件的信息，以便下次快速打开
* ImageHelper 扫描DCIM文件夹、获取文件夹及照片的路径和名称 

（已知BUG，MIUI没法用，测试机型有小米8SE、一加7T）

utils：

* 生成MD5的工具

base：（适配器）

* ImageAdapter 首页item的格式

* SingleImageAdapter 次要页面及展示页面的格式

Activity:

* MainActivity首页

* FileActivity 首页item所指向的页面

* SingleImageActivity 次页item指向的单张图片展示


