package me.hacket.library.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

// 未用ActivityResultContracts.TakeVideo是因为录像成功，result返回的bitmap也可能为null，https://stackoverflow.com/questions/65704408/activityresultcontracts-takevideo-is-returning-null-after-recording-the-video
class TakeVideo2Contract : ActivityResultContract<Uri, Uri>() {
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