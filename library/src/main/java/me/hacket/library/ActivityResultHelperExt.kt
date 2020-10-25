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

// <editor-fold defaultstate="collapsed" desc="launchActForResult">

@JvmOverloads
fun ActivityResultHelper.launchActForResult(
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
    this.launchActForResult(provider, intent, callback)
}

@JvmOverloads
fun ActivityResultHelper.launchActForResult(
    provider: IActivityResultProvider,
    input: Intent,
    listener: ((Intent) -> Unit)? = null
) {
    val callback = if (listener != null) object : ActivityCallback<Intent> {
        override fun onSuccess(result: Intent) {
            listener.invoke(result)
        }
    } else null
    this.launchActForResult(provider, input, callback)
}

@JvmOverloads
fun Activity.launchActForResult(
    clazz: Class<out Activity>,
    extra: Bundle? = null,
    listener: ((Intent) -> Unit)
) {
    val intent = Intent(applicationContext, clazz)
    extra?.let { intent.putExtras(it) }
    launchActForResult(intent, listener)
}

@JvmOverloads
fun Activity.launchActForResult(
    input: Intent,
    listener: ((Intent) -> Unit)? = null
) {
    if (this is IActivityResultProvider) {
        ActivityResultHelper.getInstance().launchActForResult(this, input, listener)
    } else {
        checkActivityResultProvider(this)
//        val callback = if (listener != null) object : ActivityCallback<Intent> {
//            override fun onSuccess(result: Intent) {
//                listener.invoke(result)
//            }
//        } else null
//        ActivityResultHelper.getInstance().launchActForResult(this, input, callback)
    }
}

@JvmOverloads
fun Fragment.launchActForResult(
    clazz: Class<out Activity>,
    extra: Bundle? = null,
    listener: ((Intent) -> Unit)? = null
) {
    val intent = Intent(context, clazz)
    extra?.let { intent.putExtras(it) }
    launchActForResult(intent, listener)
}

@JvmOverloads
fun Fragment.launchActForResult(
    input: Intent,
    listener: ((Intent) -> Unit)? = null
) {
    if (this is IActivityResultProvider) {
        ActivityResultHelper.getInstance().launchActForResult(this, input, listener)
    } else {
        checkActivityResultProvider(this)
//        val callback = if (listener != null) object : ActivityCallback<Intent> {
//            override fun onSuccess(result: Intent) {
//                listener.invoke(result)
//            }
//        } else null
//        ActivityResultHelper.getInstance().launchActForResult(this, input, callback)
    }
}

// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="launchPermissions">
/**
 * 请求单个权限
 */
@JvmOverloads
fun ActivityResultHelper.launchPermission(
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
    launchPermissions(provider, arrayOf(permission), callback)
}

suspend fun ActivityResultHelper.launchPermissionSuspend(
    provider: IActivityResultProvider,
    permission: String
): Boolean {
    return suspendCancellableCoroutine { c ->
        launchPermission(provider, permission) {
            c.resume(it.second)
        }
    }
}

/**
 * 请求多个权限
 */
@JvmOverloads
fun ActivityResultHelper.launchPermissions(
    provider: IActivityResultProvider,
    permission: Array<String>,
    listener: ((Map<String, Boolean>) -> Unit)? = null
) {
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            listener.invoke(result)
        }
    } else null
    launchPermissions(provider, permission, callback)
}

suspend fun ActivityResultHelper.launchPermissionsSuspend(
    provider: IActivityResultProvider,
    permission: Array<String>
): Map<String, Boolean> {
    return suspendCancellableCoroutine { c ->
        launchPermissions(provider, permission) {
            c.resume(it)
        }
    }
}

@JvmOverloads
fun Activity.launchPermission(
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
    ActivityResultHelper.getInstance().launchPermissions(this, arrayOf(permission), callback)
}

suspend fun Activity.launchPermissionSuspend(permission: String): Boolean {
    return suspendCancellableCoroutine { continuation ->
        launchPermission(permission) {
            continuation.resume(it.second)
        }
    }
}

@JvmOverloads
fun Activity.launchPermissions(
    permissions: Array<String>,
    listener: ((Map<String, Boolean>) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            listener.invoke(result)
        }
    } else null
    ActivityResultHelper.getInstance().launchPermissions(this, permissions, callback)
}

suspend fun Activity.launchPermissionsSuspend(permissions: Array<String>): Map<String, Boolean> {
    return suspendCancellableCoroutine { continuation ->
        launchPermissions(permissions) {
            continuation.resume(it)
        }
    }
}

@JvmOverloads
fun Fragment.launchPermission(
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
    ActivityResultHelper.getInstance().launchPermissions(this, arrayOf(permission), callback)
}

suspend fun Fragment.launchPermissionSuspend(permission: String): Boolean {
    return suspendCancellableCoroutine { continuation ->
        launchPermission(permission) {
            continuation.resume(it.second)
        }
    }
}

@JvmOverloads
fun Fragment.launchPermissions(
    permissions: Array<String>,
    listener: ((Map<String, Boolean>) -> Unit)? = null
) {
    checkActivityResultProvider(this)
    val callback = if (listener != null) object : ActivityCallback<Map<String, Boolean>> {
        override fun onSuccess(result: Map<String, Boolean>) {
            listener.invoke(result)
        }
    } else null
    ActivityResultHelper.getInstance().launchPermissions(this, permissions, callback)
}

suspend fun Fragment.launchPermissionsSuspend(permissions: Array<String>): Map<String, Boolean> {
    return suspendCancellableCoroutine { continuation ->
        launchPermissions(permissions) {
            continuation.resume(it)
        }
    }
}

@JvmOverloads
fun Activity.cameraPermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    launchPermission(Manifest.permission.CAMERA, listener)
}

@JvmOverloads
fun Fragment.cameraPermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    launchPermission(Manifest.permission.CAMERA, listener)
}

@JvmOverloads
fun Activity.writeStoragePermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    launchPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, listener)
}

@JvmOverloads
fun Fragment.writeStoragePermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    launchPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, listener)
}

@JvmOverloads
fun Activity.recordAudioPermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    launchPermission(Manifest.permission.RECORD_AUDIO, listener)
}

@JvmOverloads
fun Fragment.recordAudioPermission(listener: ((Pair<String, Boolean>) -> Unit)? = null) {
    launchPermission(Manifest.permission.RECORD_AUDIO, listener)
}

@JvmOverloads
fun Activity.locationPermissions(listener: ((Map<String, Boolean>) -> Unit)? = null) {
    launchPermissions(
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ), listener
    )
}

@JvmOverloads
fun Fragment.locationPermissions(listener: ((Map<String, Boolean>) -> Unit)? = null) {
    launchPermissions(
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
        throw IllegalArgumentException("${provider.javaClass.simpleName} 未实现IActivityResultProvider")
    }
}
// </editor-fold>