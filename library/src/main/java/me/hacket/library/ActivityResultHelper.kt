package me.hacket.library

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

const val TYPE_START_ACT_FOR_RESULT = 1
const val TYPE_PERMISSION = 2
const val TYPE_TAKE_PICTURE = 3
const val TYPE_TAKE_VIDEO = 4

class ActivityResultHelper private constructor() {

    private val mLauncherMap by lazy(LazyThreadSafetyMode.NONE) {
        mutableMapOf<String, ActivityResultLauncher<*>>()
    }
    private val mResultCallbacks by lazy(LazyThreadSafetyMode.NONE) {
        mutableMapOf<String, ActivityCallback<*>>()
    }

    private lateinit var mApplication: Application

    companion object {
        private const val TAG = "ActivityResultHelper"

        private const val TYPE_LAUNCHER_ACT = "activity#"
        private const val TYPE_LAUNCHER_FRAG = "fragment#"
        private const val TYPE_LAUNCHER_NONE = "none#"

        private val INSTANCE by lazy { ActivityResultHelper() }

        @JvmStatic
        fun getInstance(): ActivityResultHelper {
            return INSTANCE
        }
    }

    private val mFragmentLifecycleCallbacks = object : EmptyFragmentLifecycleCallbacks() {
        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            registerLaunchers(f)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            clearLauncherAndCallbacks(f)
        }
    }

    fun init(application: Application) {
        this.mApplication = application
        application.registerActivityLifecycleCallbacks(object : EmptyActivityLifecycleCallbacks() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is FragmentActivity) {
                    activity.supportFragmentManager
                        .registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, true)
                }
                registerLaunchers(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activity is FragmentActivity) {
                    activity.supportFragmentManager
                        .unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks)
                }
                if (activity !is ComponentActivity) return
                clearLauncherAndCallbacks(activity)
            }
        })
    }

    private fun registerLaunchers(provider: Any) {
        if (provider !is IActivityResultProvider) {
            // 未实现IActivityResult接口的，不注册
            return
        }
        var register = false
        if (provider is IActivityResultProviderForStart) {
            registerResultLauncher(provider)
            register = true
        }
        if (provider is IActivityResultProviderForPermissions) {
            registerPermissionLauncher(provider)
            register = true
        }
        if (provider is IActivityResultProviderForTakePicture) {
            registerTakePictureLauncher(provider)
            register = true
        }
        if (provider is IActivityResultProviderForTakeVideo) {
            registerTakeVideoLauncher(provider)
            register = true
        }
        if (!register) {
            registerResultLauncher(provider)
            registerPermissionLauncher(provider)
            registerTakePictureLauncher(provider)
            registerTakeVideoLauncher(provider)
        }
    }

    private fun clearLauncherAndCallbacks(provider: Any) {
        if (provider !is IActivityResultProvider) {
            // 未实现IActivityResult接口的，忽略
            return
        }
        val launcherType = when (provider) {
            is ComponentActivity -> TYPE_LAUNCHER_ACT + provider.resultKey
            is Fragment -> TYPE_LAUNCHER_FRAG + provider.resultKey
            else -> TYPE_LAUNCHER_NONE + provider.resultKey
        }
        val removeLauncherKeyList = mutableListOf<String>()
        mLauncherMap.forEach {
            val key = it.key
            if (key.startsWith(launcherType)) {
                removeLauncherKeyList.add(key)
            }
        }
        removeLauncherKeyList.forEach { key ->
            mLauncherMap.remove(key)
        }
        val removeCallbackKeyList = mutableListOf<String>()
        mResultCallbacks.forEach {
            val key = it.key
            if (key.startsWith(launcherType)) {
                removeCallbackKeyList.add(key)
            }
        }
        removeCallbackKeyList.forEach { key ->
            mResultCallbacks.remove(key)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerPermissionLauncher(provider: Any) {
        val key = getKey(provider, TYPE_PERMISSION)
        val resultLauncher = this.mLauncherMap[key]
        if (resultLauncher != null) return
        when (provider) {
            is ComponentActivity -> {
                val launcher = provider
                    .registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
                    {
                        (mResultCallbacks[key] as? ActivityCallback<Map<String, Boolean>>)
                            ?.onSuccess(it)
                    }
                this.mLauncherMap[key] = launcher
            }
            is Fragment -> {
                val launcher = provider
                    .registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
                    {
                        (mResultCallbacks[key] as? ActivityCallback<Map<String, Boolean>>)
                            ?.onSuccess(it)
                    }
                this.mLauncherMap[key] = launcher
            }
            else -> {
                Log.e(TAG, "registerPermissionLauncher provider is not support: $provider")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerResultLauncher(provider: Any) {
        val key = getKey(provider, TYPE_START_ACT_FOR_RESULT)
        val resultLauncher = this.mLauncherMap[key]
        if (resultLauncher != null) return
        when (provider) {
            is ComponentActivity -> {
                val launcher =
                    provider.registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                        object : ActivityResultCallback<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult?) {
                                if (result == null) return
                                if (result.resultCode != Activity.RESULT_OK) return
                                val data = result.data ?: return
                                (this@ActivityResultHelper.mResultCallbacks[key] as? ActivityCallback<Intent>)?.onSuccess(
                                    data
                                )
                            }
                        })
                this.mLauncherMap[key] = launcher
            }
            is Fragment -> {
                val launcher =
                    provider.registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                        object : ActivityResultCallback<ActivityResult> {
                            override fun onActivityResult(result: ActivityResult?) {
                                if (result == null) return
                                if (result.resultCode != Activity.RESULT_OK) return
                                val data = result.data ?: return
                                (this@ActivityResultHelper.mResultCallbacks[key] as? ActivityCallback<Intent>)?.onSuccess(
                                    data
                                )
                            }
                        })
                this.mLauncherMap[key] = launcher
            }
            else -> {
                Log.e(TAG, "registerResultLauncher provider is not support: $provider")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerTakePictureLauncher(provider: Any) {
        val key = getKey(provider, TYPE_TAKE_PICTURE)
        val resultLauncher = this.mLauncherMap[key]
        if (resultLauncher != null) return
        when (provider) {
            is ComponentActivity -> {
                val launcher =
                    provider.registerForActivityResult(ActivityResultContracts.TakePicture(),
                        object : ActivityResultCallback<Boolean> {
                            override fun onActivityResult(result: Boolean?) {
                                if (result == null) return
                                (this@ActivityResultHelper.mResultCallbacks[key] as? ActivityCallback<Boolean>)
                                    ?.onSuccess(result)
                            }
                        })
                this.mLauncherMap[key] = launcher
            }
            is Fragment -> {
                val launcher =
                    provider.registerForActivityResult(ActivityResultContracts.TakePicture(),
                        object : ActivityResultCallback<Boolean> {
                            override fun onActivityResult(result: Boolean?) {
                                if (result == null) return
                                (this@ActivityResultHelper.mResultCallbacks[key] as? ActivityCallback<Boolean>)
                                    ?.onSuccess(result)
                            }
                        })
                this.mLauncherMap[key] = launcher
            }
            else -> {
                Log.e(TAG, "registerTakePictureLauncher provider is not support: $provider")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerTakeVideoLauncher(provider: Any) {
        val key = getKey(provider, TYPE_TAKE_VIDEO)
        val resultLauncher = this.mLauncherMap[key]
        if (resultLauncher != null) return
        when (provider) {
            is ComponentActivity -> {
                val launcher = provider.registerForActivityResult(
                    TakeVideo2(),
                    ActivityResultCallback<Uri> { uri ->
                        (this@ActivityResultHelper.mResultCallbacks[key] as? ActivityCallback<Uri>)
                            ?.onSuccess(uri)
                    })
                this.mLauncherMap[key] = launcher
            }
            is Fragment -> {
                val launcher = provider.registerForActivityResult(
                    TakeVideo2(),
                    ActivityResultCallback<Uri> { uri ->
                        (this@ActivityResultHelper.mResultCallbacks[key] as? ActivityCallback<Uri>)
                            ?.onSuccess(uri)
                    })
                this.mLauncherMap[key] = launcher
            }
            else -> {
                Log.e(TAG, "registerTakeVideoLauncher provider is not support: $provider")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun launchActForResult(
        provider: Any,
        input: Intent,
        callback: ActivityCallback<Intent>? = null
    ) {
        checkApplication()
        val key = getKey(provider, TYPE_START_ACT_FOR_RESULT)
        callback?.let {
            mResultCallbacks[key] = it
        }
        (mLauncherMap[key] as? ActivityResultLauncher<Intent>)
            ?.launch(input)
    }

    /**
     * 请求多个权限
     */
    @Suppress("UNCHECKED_CAST")
    fun launchPermissions(
        provider: Any,
        input: Array<String>,
        callback: ActivityCallback<Map<String, Boolean>>? = null
    ) {
        checkApplication()
        val key = getKey(provider, TYPE_PERMISSION)
        callback?.let {
            this.mResultCallbacks.put(key, callback)
        }
        (mLauncherMap[key] as? ActivityResultLauncher<Array<String>>)
            ?.launch(input)
    }

    /**
     * 调用相机拍照
     */
    @Suppress("UNCHECKED_CAST")
    fun takePicture(
        provider: Any,
        input: Uri,
        callback: ActivityCallback<Boolean>? = null
    ) {
        checkApplication()
        val key = getKey(provider, TYPE_TAKE_PICTURE)
        callback?.let {
            this.mResultCallbacks.put(key, callback)
        }
        (mLauncherMap[key] as? ActivityResultLauncher<Uri>)
            ?.launch(input)
    }

    /**
     * 调用相机录像
     */
    @Suppress("UNCHECKED_CAST")
    fun takeVideo(
        provider: Any,
        input: Uri,
        callback: ActivityCallback<Uri>? = null
    ) {
        checkApplication()
        val key = getKey(provider, TYPE_TAKE_VIDEO)
        callback?.let {
            this.mResultCallbacks.put(key, callback)
        }
        (mLauncherMap[key] as? ActivityResultLauncher<Uri>)
            ?.launch(input)
    }

    private fun checkApplication() {
        if (!this::mApplication.isInitialized) {
            throw IllegalArgumentException("mApplication not init. call init() first.")
        }
    }


    fun getContext(): Context {
        checkApplication()
        return mApplication.applicationContext
    }

    private fun getKey(provider: Any, type: Int): String {
        val launcherType = when (provider) {
            is ComponentActivity -> TYPE_LAUNCHER_ACT
            is Fragment -> TYPE_LAUNCHER_FRAG
            else -> TYPE_LAUNCHER_NONE
        }
        return if (provider is IActivityResultProvider) {
            "$launcherType${provider.resultKey}#$type"
        } else {
            "$launcherType${provider.hashCode()}#$type"
        }
    }

}


abstract class EmptyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}

abstract class EmptyFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks()

interface ActivityCallback<T> {
    fun onSuccess(result: T)
}

interface IActivityResultProviderForStart : IActivityResultProvider
interface IActivityResultProviderForPermissions : IActivityResultProvider
interface IActivityResultProviderForTakePicture : IActivityResultProvider
interface IActivityResultProviderForTakeVideo : IActivityResultProvider

// 未用ActivityResultContracts.TakeVideo是因为录像成功，result返回的bitmap也可能为null，https://stackoverflow.com/questions/65704408/activityresultcontracts-takevideo-is-returning-null-after-recording-the-video
class TakeVideo2 : ActivityResultContract<Uri, Uri>() {
    @CallSuper
    override fun createIntent(context: Context, input: Uri): Intent {
        return Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, input)
    }

    override fun getSynchronousResult(context: Context, input: Uri): SynchronousResult<Uri>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri {
        if (intent == null || resultCode != Activity.RESULT_OK) return Uri.EMPTY

        if (resultCode == Activity.RESULT_OK) {
            intent.data?.let { uri ->
                // Do something with the video URI
                return uri
            }
        }
        // intent.getParcelableExtra("data")
        return Uri.EMPTY
    }
}