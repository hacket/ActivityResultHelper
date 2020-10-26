@file:JvmName("ActivityResultHelperExt")

package me.hacket.library

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// <editor-fold defaultstate="collapsed" desc="startActivityForResult">

@JvmOverloads
fun ActivityResultHelper.startActivityForResult(
    provider: IActivityResultProvider,
    clazz: Class<out Activity>,
    extra: Bundle? = null,
    listener: ((Intent) -> Unit)? = null
) {
    val callback = if (listener != null) object : ActivityCallback<Intent> {
        override fun onSuccess(result: Intent) {
            listener.invoke(result)
        }
    } else null
    val intent = Intent(getContext(), clazz)
    extra?.let { intent.putExtras(it) }
    this.startActivityForResult(provider, intent, callback)
}

@JvmOverloads
fun ActivityResultHelper.startActivityForResult(
    provider: IActivityResultProvider,
    input: Intent,
    listener: ((Intent) -> Unit)? = null
) {
    val callback = if (listener != null) object : ActivityCallback<Intent> {
        override fun onSuccess(result: Intent) {
            listener.invoke(result)
        }
    } else null
    this.startActivityForResult(provider, input, callback)
}

@JvmOverloads
fun Activity.startActivityForResultExt(
    clazz: Class<out Activity>,
    extra: Bundle? = null,
    listener: ((Intent) -> Unit)
) {
    val intent = Intent(applicationContext, clazz)
    extra?.let { intent.putExtras(it) }
    startActivityForResultExt(intent, listener)
}

@JvmOverloads
fun Activity.startActivityForResultExt(
    input: Intent,
    listener: ((Intent) -> Unit)? = null
) {
    if (this is IActivityResultProvider) {
        ActivityResultHelper.getInstance().startActivityForResult(this, input, listener)
    } else {
        checkActivityResultProvider(this)
//        val callback = if (listener != null) object : ActivityCallback<Intent> {
//            override fun onSuccess(result: Intent) {
//                listener.invoke(result)
//            }
//        } else null
//        ActivityResultHelper.getInstance().startActivityForResult(this, input, callback)
    }
}

@JvmOverloads
fun Fragment.startActivityForResultExt(
    clazz: Class<out Activity>,
    extra: Bundle? = null,
    listener: ((Intent) -> Unit)? = null
) {
    val intent = Intent(context, clazz)
    extra?.let { intent.putExtras(it) }
    startActivityForResultExt(intent, listener)
}

@JvmOverloads
fun Fragment.startActivityForResultExt(
    input: Intent,
    listener: ((Intent) -> Unit)? = null
) {
    if (this is IActivityResultProvider) {
        ActivityResultHelper.getInstance().startActivityForResult(this, input, listener)
    } else {
        checkActivityResultProvider(this)
//        val callback = if (listener != null) object : ActivityCallback<Intent> {
//            override fun onSuccess(result: Intent) {
//                listener.invoke(result)
//            }
//        } else null
//        ActivityResultHelper.getInstance().startActivityForResult(this, input, callback)
    }
}

// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="requestPermission">
/**
 * 请求单个权限
 */
@JvmOverloads
fun ActivityResultHelper.requestPermission(
    provider: IActivityResultProvider,
    permission: String,
    listener: ((Pair<String, Boolean>) -> Unit)? = null
) {
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            val value = result[permission] ?: false
            listener.invoke(Pair(permission, value))
        }
    } else null
    requestPermissions(provider, arrayOf(permission), callback)
}

suspend fun ActivityResultHelper.requestPermissionSuspend(
    provider: IActivityResultProvider,
    permission: String
): Boolean {
    return suspendCancellableCoroutine { c ->
        requestPermission(provider, permission) {
            c.resume(it.second)
        }
    }
}

/**
 * 请求多个权限
 */
@JvmOverloads
fun ActivityResultHelper.requestPermissions(
    provider: IActivityResultProvider,
    permission: Array<String>,
    listener: ((Map<String, Boolean>) -> Unit)? = null
) {
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            listener.invoke(result)
        }
    } else null
    requestPermissions(provider, permission, callback)
}

suspend fun ActivityResultHelper.requestPermissionsSuspend(
    provider: IActivityResultProvider,
    permission: Array<String>
): Map<String, Boolean> {
    return suspendCancellableCoroutine { c ->
        requestPermissions(provider, permission) {
            c.resume(it)
        }
    }
}

@JvmOverloads
fun Activity.requestPermission(
    permission: String,
    listener: ((Pair<String, Boolean>) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            val value = result[permission] ?: false
            listener.invoke(Pair(permission, value))
        }
    } else null
    ActivityResultHelper.getInstance().requestPermissions(this, arrayOf(permission), callback)
}

suspend fun Activity.requestPermissionSuspend(permission: String): Boolean {
    return suspendCancellableCoroutine { continuation ->
        requestPermission(permission) {
            continuation.resume(it.second)
        }
    }
}

@JvmOverloads
fun Activity.requestPermissions(
    permissions: Array<String>,
    listener: ((Map<String, Boolean>) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            listener.invoke(result)
        }
    } else null
    ActivityResultHelper.getInstance().requestPermissions(this, permissions, callback)
}

suspend fun Activity.requestPermissionsSuspend(permissions: Array<String>): Map<String, Boolean> {
    return suspendCancellableCoroutine { continuation ->
        requestPermissions(permissions) {
            continuation.resume(it)
        }
    }
}

@JvmOverloads
fun Fragment.requestPermission(
    permission: String,
    listener: ((Pair<String, Boolean>) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            val value = result[permission] ?: false
            listener.invoke(Pair(permission, value))
        }
    } else null
    ActivityResultHelper.getInstance().requestPermissions(this, arrayOf(permission), callback)
}

suspend fun Fragment.requestPermissionSuspend(permission: String): Boolean {
    return suspendCancellableCoroutine { continuation ->
        requestPermission(permission) {
            continuation.resume(it.second)
        }
    }
}

@JvmOverloads
fun Fragment.requestPermissions(
    permissions: Array<String>,
    listener: ((Map<String, Boolean>) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            listener.invoke(result)
        }
    } else null
    ActivityResultHelper.getInstance().requestPermissions(this, permissions, callback)
}

suspend fun Fragment.requestPermissionsSuspend(permissions: Array<String>): Map<String, Boolean> {
    return suspendCancellableCoroutine { continuation ->
        requestPermissions(permissions) {
            continuation.resume(it)
        }
    }
}

@JvmOverloads
fun Activity.requestCameraPermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    requestPermission(Manifest.permission.CAMERA, listener)
}

@JvmOverloads
fun Fragment.requestCameraPermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    requestPermission(Manifest.permission.CAMERA, listener)
}

@JvmOverloads
fun Activity.requestWriteStoragePermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, listener)
}

@JvmOverloads
fun Fragment.requestWriteStoragePermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, listener)
}

@JvmOverloads
fun Activity.requestRecordAudioPermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    requestPermission(Manifest.permission.RECORD_AUDIO, listener)
}

@JvmOverloads
fun Fragment.requestRecordAudioPermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    requestPermission(Manifest.permission.RECORD_AUDIO, listener)
}

@JvmOverloads
fun Activity.requestLocationPermissions(listener: ((Map<String, Boolean>) -> Unit)? = null) {
    requestPermissions(
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ), listener
    )
}

@JvmOverloads
fun Fragment.requestLocationPermissions(listener: ((Map<String, Boolean>) -> Unit)? = null) {
    requestPermissions(
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ), listener
    )
}

// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="takePicture">
@JvmOverloads
fun ActivityResultHelper.takePicture(
    provider: IActivityResultProvider,
    input: Uri,
    listener: ((Boolean) -> Unit)? = null
) {
    val callback = if (listener != null) object : ActivityCallback<Boolean> {
        override fun onSuccess(result: Boolean) {
            listener.invoke(result)
        }
    } else null
    takePicture(provider, input, callback)
}

@JvmOverloads
fun Activity.takePicture(
    input: Uri,
    listener: ((Boolean) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Boolean> {
        override fun onSuccess(result: Boolean) {
            listener.invoke(result)
        }
    } else null
    ActivityResultHelper.getInstance().takePicture(this, input, callback)
}

@JvmOverloads
fun Fragment.takePicture(
    input: Uri,
    listener: ((Boolean) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Boolean> {
        override fun onSuccess(result: Boolean) {
            listener.invoke(result)
        }
    } else null
    ActivityResultHelper.getInstance().takePicture(this, input, callback)
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="takeVideo">
@JvmOverloads
fun ActivityResultHelper.takeVideo(
    provider: IActivityResultProvider,
    input: Uri,
    listener: ((Uri) -> Unit)? = null
) {
    val callback = if (listener != null) object : ActivityCallback<Uri> {
        override fun onSuccess(result: Uri) {
            listener.invoke(result)
        }
    } else null
    takeVideo(provider, input, callback)
}

@JvmOverloads
fun Activity.takeVideo(
    input: Uri,
    listener: ((Uri) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Uri> {
        override fun onSuccess(result: Uri) {
            listener.invoke(result)
        }
    } else null
    ActivityResultHelper.getInstance().takeVideo(this, input, callback)
}

@JvmOverloads
fun Fragment.takeVideo(
    input: Uri,
    listener: ((Uri) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Uri> {
        override fun onSuccess(result: Uri) {
            listener.invoke(result)
        }
    } else null
    ActivityResultHelper.getInstance().takeVideo(this, input, callback)
}

private fun checkActivityResultProvider(provider: Any) {
    if (provider !is IActivityResultProvider) {
        throw IllegalArgumentException("${provider.javaClass.simpleName} should implement IActivityResultProvider or sub interface.")
    }
}
// </editor-fold>