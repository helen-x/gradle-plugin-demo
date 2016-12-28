# AndroidStudio中自定义 Gradle plugin

>作者:卫晨  
>微信公众号:互联网技术内参  
>QQ群: 452780193


本文内容包括:     

- 利用AndroidStudio,编写自定义Gradle plugin  
- MavenDeployer 发布plugin  
- 使用Gradle plugin  


## 简介   
之前写了一个Android中的AOP框架Cooker.   
这里总结一下里面用到的两块小知识:     

1)自定义 Gradle plugin      
2)发布自己的jar到 maven仓库    

项目中引入自定义Gradle plugin一般有三种方法:   
>1. 直接写在 build.gradle中. 
>2. plugin源码放到rootProjectDir/buildSrc/src/main/groovy目录下 
>3. plugin打包成jar, 发布到maven仓库, 然后项目通过Build Script依赖jar的形式引入    


下面介绍的是第3种方式.  


##  一. 用AndroidStudio写Gradle plugin       

### 1.新建一个Android工程 
### 2.在这个工程里面,新建一个Android Library 
   先起名叫cooker-plugin吧, 我们将会用这个library写Gradle plugin 


![](http://upload-images.jianshu.io/upload_images/4048192-c46c28de5172cdd9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 3.建立plugin的目录结构  
把这个cooker-plugin中默认产生的文件都删除, 然后按照下面结构新建文件   


![](http://upload-images.jianshu.io/upload_images/4048192-debed4cf882cc124.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 解释   
1.因为我们用Groovy写的插件, 插件代码放在 src/main/groovy下       

2.在src/main/resources/META-INF/gradle-plugins 里声明plugin信息   
 比如:新建`cooker-plugin.properties`文件,内容如下     

>```java   
implementation-class=com.helen.plugin.CookerPlugin
```  
> 这里:  
> "cooker-plugin" 是插件名称;    
> "com.helen.plugin.CookerPlugin" 是对应的插件实现类  

3.build.gradle 声明用groovy开发      

```java   
apply plugin: 'groovy'

dependencies {
    compile gradleApi()
    compile localGroovy()
}

repositories {
    mavenCentral()
}
```    

### 4.实现插件   
实现plugin,其实就是需要继承实现Plugin<Project> 的接口    

```java   
package com.helen.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public class CookerPlugin implements Plugin<Project> {

    void apply(Project project) {
        //这里实现plugin的逻辑
        //巴拉巴拉巴拉
        println "hello, this is cooker plugin!"

        //cooker-plugin
        //比如这里加一个简单的task
        project.task('cooker-test-task') << {
            println "hello, this is cooker test task!"
        }
    }
}


```   

### 5.一个简单的plugin就写好了   
在cooker-plugin项目中, build一下.   
就能在build/libs下生成对应的plugin插件了

![](http://upload-images.jianshu.io/upload_images/4048192-177dc91cce18091e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240) 


这个插件就能使用了. 可以发布在本地仓库或者Maven仓库.   
       



## 二. mavenDeployer发布插件      
下面介绍一下利用mavenDeployer发布在本地仓库.   

### 1. 引入 mavenDeplayer插件      
修改cooker-plugin的build.gradle, 修改后如下:  

```java   
apply plugin: 'groovy'
//添加maven plugin, 用于发布我们的jar
apply plugin: 'maven'

dependencies {
    compile gradleApi()
    compile localGroovy()
}

repositories {
    mavenCentral()
}

//设置maven deployer
uploadArchives {
    repositories {
        mavenDeployer {
            //设置插件的GAV参数
            pom.groupId = 'com.helen.plugin'
            pom.artifactId = 'cooker-plugin'
            pom.version = 1.0 
            //文件发布到下面目录
            repository(url: uri('../release'))
        }
    }
}

```   
### 2.用uploadArchices发布 
运行uploadArchives. 就能在设置的仓库路径中生成 cooker-plugin了           

![](http://upload-images.jianshu.io/upload_images/4048192-200e613468863e3a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)





## 三. 使用gradle plugin     

### 1.在build.gradle引入 cooker-plugin     

```java  
apply plugin: 'com.android.application'
//使用cooker-plugin
apply plugin: 'cooker-plugin'

buildscript {
    repositories {
        maven {
            //cooker-plugin 所在的仓库
            //这里是发布在本地文件夹了
            url uri('../release')
        }
    }
    dependencies {
        //引入cooker-plugin
        classpath 'com.helen.plugin:cooker-plugin:1.0'
    }
}
```



### 2. 我们编译App的时候,cooker-plugin就会介入了  
每次clean/build时, 在Gradle Console可以看到我们的log   
`hello, this is cooker plugin!`      

```   
Configuration on demand is an incubating feature.
hello, this is cooker plugin!
Incremental java compilation is an incubating feature.
:app:preBuild UP-TO-DATE
```      

### 3.使用cooker-plugin中定义的task     
前面demo中, 我们新建了一个task: `cooker-test-task`, 他简单输出一句log.下面测试运行一下这个task.     
在控制台输入 `gradle cooker-test-task` 运行结果如下      


![](http://upload-images.jianshu.io/upload_images/4048192-0e73dc69b9dad25f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  



## 四. 总结   

[Demo地址-Github](https://github.com/helen-x/gradle-plugin-demo)   
到此为止, 自定义Gradle plugin就介绍完了.   
结合AndroidStudio, 自定义Gradle plugin可以完成很多功能.   
    
比如cooker的plugin完成了:  
1)添加编译依赖      
2)进行Aspecj编译  
3)自动生成混淆配置

后面会另开一个文章介绍一下gradle plugin的进阶应用 




## 更多文章请关注公众号   

![](http://upload-images.jianshu.io/upload_images/4048192-6d55383acfda73f5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)