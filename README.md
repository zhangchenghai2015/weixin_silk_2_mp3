jni用jdk 1.6，ndk版本是19或者17
4
​
5
1.使用lame和silk开源代码在silk转换成mp3时候，微信中在silk文件头添加0x02
6
​
7
2.使用mad和silk开源代码在mp3转换成silk时候，这里要
8
2.1.使用https://github.com/zhangchenghai2015/silk-v3-decoder/tree/master/silk/test中的Encoder.c中
9
转换成pcm，
10
2.2.然后再用mad库使用https://blog.csdn.net/gepanqiang3020/article/details/73695483实例代码，libmad库代码
11
链接：http://www.linuxfromscratch.org/blfs/view/svn/multimedia/libmad.html
12
2.3.要把android.mk中的silk 的encode对应的文件都去掉注释
13
2.4.把libmad中的调用的mad的api放到include中