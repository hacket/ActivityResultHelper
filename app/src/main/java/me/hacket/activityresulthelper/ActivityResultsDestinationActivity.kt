package me.hacket.activityresulthelper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import me.hacket.activityresulthelper.databinding.ActivityResultsDestinationBinding

class ActivityResultsDestinationActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "hacket"
    }

    private val binding by lazy { ActivityResultsDestinationBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d(TAG, "${javaClass.simpleName} onCreate")

        val input = intent.getIntExtra("input", -1)
        Log.d(TAG, "input=$input")

        val from = intent.getStringExtra("from")

        binding.tvInfo.text = "from=$from \n input=$input"

        binding.btnSelectContact.setOnClickListener {
            val data = Intent()
            data.putExtra(
                "data",
                "from=$from input=${input}"
            )
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "${javaClass.simpleName} onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "${javaClass.simpleName} onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "${javaClass.simpleName} onResume")
    }

    override fun onPause() {
        Log.i(TAG, "${javaClass.simpleName} onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.i(TAG, "${javaClass.simpleName} onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.i(TAG, "${javaClass.simpleName} onDestroy")
        super.onDestroy()
    }

}