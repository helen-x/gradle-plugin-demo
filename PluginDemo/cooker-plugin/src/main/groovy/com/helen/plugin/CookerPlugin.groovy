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
