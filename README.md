<div align=center><img src="https://github.com/snpmyn/Push/raw/master/image.png"/></div>

[![SNAPSHOT](https://jitpack.io/v/Jaouan/Revealator.svg)](https://jitpack.io/#snpmyn/Push)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/55524606732f4c4fb9f5e207c412a3a7)](https://www.codacy.com/manual/snpmyn/Push?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=snpmyn/Push&amp;utm_campaign=Badge_Grade)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)

### 介绍
极光推送。

### 架构

| 模块 | 说明 | 补充 |
|:-:|:-:|:-:|
| 示例app | 用法举例 | 无 |
| 一方库Jpush | 推送集成实现 | 无 |
| 一方库Janalytics | 统计集成实现 | 无 |

### 依赖、权限

| 模块 | 依赖 |
|:-:|:-:|
| 示例app | implementation project(path: ':jpush') |
| 示例app | implementation project(path: ':janalytics') |
| 一方库Jpush | api 'com.github.snpmyn:*Util*:master-SNAPSHOT'（避重）|
| 一方库Jpush | implementation 'cn.jiguang.sdk:jcore:2.1.4' |
| 一方库Jpush | api 'cn.jiguang.sdk:jpush:3.4.0'（避重）|
| 一方库Janalytics | implementation 'com.github.snpmyn:*Util*:master-SNAPSHOT' |
| 一方库Janalytics | implementation 'cn.jiguang.sdk:jcore:2.1.4' |
| 一方库Janalytics | api 'cn.jiguang.sdk:janalytics:2.1.0'（避重）|
| 二方库Util-示例app | implementation project(path: ':utilone') |
| 二方库Util-示例app | implementation project(path: ':utiltwo') |
| 二方库Util-UtilOne | api 'com.github.bumptech.glide:glide:4.10.0'（避重）|
| 二方库Util-UtilOne | api 'com.google.android.material:material:1.2.0-alpha01'（避重）|
| 二方库Util-UtilOne | api 'io.reactivex:rxandroid:1.2.1'（避重）|
| 二方库Util-UtilOne | api 'io.reactivex:rxjava:1.3.8'（避重）|
| 二方库Util-UtilOne | api 'com.jakewharton.timber:timber:4.7.1'（避重）|
| 二方库Util-UtilOne | api 'com.tencent:mmkv-static:1.0.23'（避重）|
| 二方库Util-UtilOne | implementation 'com.getkeepsafe.relinker:relinker:1.3.1' |
| 二方库Util-UtilOne | implementation 'com.qw:soulpermission:1.2.2_x' |
| 二方库Util-UtilOne | implementation 'org.apache.commons:commons-lang3:3.9' |
| 二方库Util-UtilTwo | implementation 'androidx.core:core-ktx:1.2.0-beta02' |
| 二方库Util-UtilTwo | implementation "org.jetbrains.kotlin:*kotlin-stdlib-jdk7*:$kotlin_version" |

| 模块 | 权限 |
|:-:|:-:|
| 示例app | 无 |
| 一方库Jpush | 略 |
| 一方库Janalytics | android:name="android.permission.INTERNET"（避重）|
| 一方库Janalytics | android:name="android.permission.WAKE_LOCK"（避重）|
| 一方库Janalytics | android:name="android.permission.READ_PHONE_STATE"（避重）|
| 一方库Janalytics | android:name="android.permission.WRITE_EXTERNAL_STORAGE"（避重）|
| 一方库Janalytics | android:name="android.permission.READ_EXTERNAL_STORAGE"（避重）|
| 一方库Janalytics | android:name="android.permission.ACCESS_NETWORK_STATE"（避重）|
| 一方库Janalytics | android:name="android.permission.ACCESS_WIFI_STATE"（避重）|
| 二方库Util-app | android:name="android.permission.WRITE_EXTERNAL_STORAGE"（避重）|
| 二方库Util-app | android:name="android.permission.READ_EXTERNAL_STORAGE"（避重）|
| 二方库Util-UtilOne | 无 |
| 二方库Util-UtilTwo | 无 |

### 使用
> 版本快速迭代中，暂时使用`master-SNAPSHOT`版。<br>
> 如获取不成功，请暂时查看源码。

build.gradle(module)
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.2'
        // 动态圈选插件（可选）
        classpath 'cn.jiguang.android:janalytics-gradle-plugin:3.0.0'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```
build.gradle(app)
```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 29
    defaultConfig {
        ...
        ndk {
            abiFilters 'armeabi-v7a'//,'armeabi','arm64-v8a','x86','x86_64','mips','mips64'
        }
        manifestPlaceholders = [
                JPUSH_PKGNAME: applicationId,
                // JPush注册包名对应AppKey
                JPUSH_APPKEY : "xxx",
                // 暂默值即可
                JPUSH_CHANNEL: "developer-default",
        ]
    }  
    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
    }
}

dependencies {
    implementation 'com.github.snpmyn.Push:jpush:master-SNAPSHOT'
    implementation 'com.github.snpmyn.Push:janalytics:master-SNAPSHOT'
}
```

### License
```
Copyright 2019 snpmyn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
