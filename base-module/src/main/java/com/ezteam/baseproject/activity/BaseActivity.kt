package com.ezteam.baseproject.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.ezteam.baseproject.dialog.DialogLoading
import com.ezteam.baseproject.extensions.getHeightNavigationBar
import com.ezteam.baseproject.utils.permisson.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    protected lateinit var binding: B
    private var permissionComplete: ((Boolean) -> Unit)? = null
    private var lastClickTime: Long = 0
    private var progressDialog: DialogLoading? = null
    protected val activityLauncher: BetterActivityResult<Intent, ActivityResult> =
        BetterActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        binding = viewBinding()
        setContentView(binding.root)
        initView()
        initData()
        initListener()
    }

    protected val viewException: Array<View>?
        protected get() = null

    protected abstract fun initView()
    protected abstract fun initData()
    protected abstract fun initListener()
    protected abstract fun viewBinding(): B

    private fun showLoading() {
        progressDialog ?: let {
            progressDialog = DialogLoading.ExtendBuilder(this)
                .setCancelable(false)
                .setCanOnTouchOutside(false)
                .build() as DialogLoading
            progressDialog?.show()
        }
    }

    private fun hideLoading() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
            progressDialog = null
        }
    }

    fun showHideLoading(isShow: Boolean) {
        if (isShow) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    fun aVoidDoubleClick(): Boolean {
        if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
            return true
        }
        lastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    fun toast(content: String?) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (!TextUtils.isEmpty(content)) Toast.makeText(
                this@BaseActivity,
                content,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    open fun requestPermission(
        complete: (Boolean) -> Unit,
        vararg permissions: String?
    ) {
        this.permissionComplete = complete
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PermissionUtils.checkPermissonAccept(
                this,
                *permissions
            )
        ) {
            PermissionUtils.requestRuntimePermission(this, *permissions)
        } else {
            complete.invoke(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtils.MY_PERMISSIONS_REQUEST -> if (PermissionUtils.checkPermissonAccept(
                    this,
                    *permissions
                )
            ) {
                permissionComplete?.invoke(true)
            } else {
                permissionComplete?.invoke(false)
            }
        }
    }

    protected open fun isAcceptManagerStorage(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            PermissionUtils.checkPermissonAccept(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    open fun setStatusBarHomeTransparent(activity: FragmentActivity) {
        val window: Window = activity.window
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(
                activity,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                true
            )
        }

        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(
                activity,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                false
            )
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    open fun setAppActivityFullScreenOver(activity: FragmentActivity) {
        val window: Window = activity.window
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(
                activity,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                true
            )
        }

        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(
                activity,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                false
            )
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    open fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}