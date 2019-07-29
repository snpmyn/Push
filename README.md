### 介绍
极光推送示例

### 依赖
#### 自身
##### app
implementation 'com.github.snpmyn:*Util*:master-SNAPSHOT'
#### com.github.snpmyn:Util(implementation)
##### AndroidLibrary - UtilOne
* api 'com.github.bumptech.glide:glide:4.9.0'（避重）
* api 'com.google.android.material:material:1.1.0-alpha08'（避重）
* api 'com.jakewharton.timber:timber:4.7.1'（避重）
* implementation 'com.qw:soulpermission:1.1.8'
* implementation 'org.apache.commons:commons-lang3:3.9'

### 权限
#### com.github.snpmyn:Util
##### app
* <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />（避重）
* <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />（避重）
