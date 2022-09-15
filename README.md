### 使用说明
#### 添加资源地址
```
maven { url = uri("https://gitee.com/liu-huiliang/jarlibs/raw/master") }
```
#### 添加引用
```
implementation 'com.lhl.screen:screen:1.0.0'
```
#### 初始化

```
ScreenManager manager = new ScreenManager(Context)
```


####  说明

        暂时只支持activity和fragment继承
##### 继承BackColor  
        如果需要添加背景颜色实现BackColor接口，在backColor中返回你需要设置的颜色


##### 继承FullScreen  
        如果需要全屏继承FullScreen接口

##### 继承InvisibleStatusBar  
        如果需要隐藏状态栏继承InvisibleStatusBar
#####  继承NotFullScreen  
        如果不需要全屏继承NotFullScreen，一般在fragment中使用，配合viewpage使用，多个fragment有全屏和非全屏的
##### 继承NotScreenShot   
        如果不允许截屏继承NotScreenShot
##### 继承StatusBarColor  
        如果需要修改状态栏颜色继承StatusBarColor，在statusBarColor中返回需要设置的颜色

##### 继承StatusBarTextColorBlack  
        如果需要把状态栏字体颜色设置成黑色继承StatusBarTextColorBlack

#### ScreenManager静态方法说明
| 方法名 | 说明 | 参数说明 |
| --- | --- | --- |
 | statusBarTextColorWhite | 设置状态栏字体颜色为白色 | 当前窗口的window |
 | statusBarTextColorBlack | 设置状态栏字体颜色为黑色 | 当前窗口的window |
 | invisibleStatusBar | 隐藏状态栏  | 当前窗口的window |
 | getStatusBarHeight | 获取状态栏高度 | 无 |
 | getWindowWidth | 获取屏幕宽度 | 无 |
 | getWindowHeight  | 获取屏幕高度 | 无 |
 | setStatusBarColor | 设置状态栏颜色 | 第一个参数为当前窗口的widow，第二个参数为需要设置的颜色 |
 | dip2px | dip转px |  需要转换的dip，返回转换后px |
 | px2dip | px转dip | 需要转换的px，返回转换后的dip |
 | px2sp  | px转sp | 需要转换的px，返回转换后的sp |
 | sp2px | sp转px | 需要转换的sp，返回转换后的px |
