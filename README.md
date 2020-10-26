# ActivityResultHelper

一行代码实现权限请求、startActivityForResult、调用相机拍照及调用相机录像等，消除onActivityResult()、onRequestPermissionsResult()回调导致的代码分散的问题；支持协程

## request permissions 请求权限
1. 请求单个权限
```kotlin
// AppCompatActivity or Fragment
requestPermission(Manifest.permission.CAMERA) {
    // ...
}
```

2. 请求多个权限
```kotlin
// AppCompatActivity or Fragment
requestPermissions(
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )) {
    // ...    
}
```

3. 常见的权限，可以自定义
```kotlin
// AppCompatActivity or Fragment
requestCameraPermission {
    // ...
}
```

4. 对协程的支持
```kotlin
lifecycleScope.launch {
    if (!requestPermissionSuspend(Manifest.permission.CAMERA)) {
        return@launch
    }
    // ...
}
```

## startActivityForResult
```kotlin
val intent = Intent(this, ActivityResultsDestinationActivity::class.java)
intent.putExtra("from", "MainActivity")
intent.putExtra("input", 12306)
startActivityForResultExt(intent) { data->
    // ...
}
```

## takePicture 调用系统相机拍照
```kotlin
val imageFile = File(getExternalFilesDir("image"), "temp.jpg")
val uri = FileProviderUtils.getUriForFile(this, imageFile)
takePicture(uri) {
    Log.d(TAG, "takePicture it=$it, imageUri=$imageUri, uri=$uri")
}
```

## takeVideo 调用系统相机拍照
```kotlin
val videoFile = File(getExternalFilesDir("image"), "temp.mp4")
val uri = FileProviderUtils.getUriForFile(applicationContext, videoFile)
takeVideo(uri) {
    // ... 
}
```

## 其他
还有`TakePicturePreview`、`OpenDocument`、`PickContact`等如有需要可以添加，具体见`ActivityResultContracts`

## License

    Copyright 2020 hacket

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.