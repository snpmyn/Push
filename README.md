### 介绍
极光推送示例

### 依赖
#### 自身
##### AndroidLibrary - Jpush
* api 'com.github.snpmyn:*Util*:master-SNAPSHOT'（避重）
* api 'cn.jiguang.sdk:jpush:3.3.4'（避重）
* api 'cn.jiguang.sdk:jcore:2.1.0'（避重）

#### com.github.snpmyn:Util(api)
##### AndroidLibrary - UtilOne
* api 'com.github.bumptech.glide:glide:4.9.0'（避重）
* api 'com.google.android.material:material:1.1.0-alpha09'（避重）
* api 'com.jakewharton.timber:timber:4.7.1'（避重）
* implementation 'com.qw:soulpermission:1.2.1_x'
* implementation 'org.apache.commons:commons-lang3:3.9'

##### AndroidLibrary - UtilTwo
* implementation 'androidx.core:core-ktx:1.2.0-alpha03'
* implementation "org.jetbrains.kotlin:*kotlin-stdlib-jdk7*:$kotlin_version"

### 权限
#### AndroidLibrary - Jpush
待补充
#### com.github.snpmyn:Util
##### app
* android:name="android.permission.WRITE_EXTERNAL_STORAGE"（避重）
* android:name="android.permission.READ_EXTERNAL_STORAGE"（避重）
