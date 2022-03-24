package com.ezteam.applocker.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.PhotoAdapter
import com.ezteam.applocker.databinding.ActivityIntrusionAlertBinding
import com.ezteam.applocker.databinding.LayoutDialogDeleteImageBinding
import com.ezteam.applocker.dialog.DialogDeleteImage
import com.ezteam.applocker.item.PhotoImage
import com.ezteam.applocker.key.IntrusionAlert
import com.ezteam.applocker.key.KeyLock
import com.ezteam.applocker.service.LockService
import com.ezteam.applocker.viewmodel.TakePhotoViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

class IntrusionAlertActivity : BaseActivity<ActivityIntrusionAlertBinding>() {
    private val listPhoto = mutableListOf<PhotoImage>()
    private lateinit var adapterPhoto: PhotoAdapter
    private val viewModel by inject<TakePhotoViewModel>()
    private var dialogQuestion: DialogDeleteImage? = null
    override fun initView() {
        // ads
        EzAdControl.getInstance(this).showAds()
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.turnOn.isChecked =
                PreferencesUtils.getBoolean(
                    IntrusionAlert.ON_INTRUSION,
                    false
                )&&checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        } else {
            binding.turnOn.isChecked =
                PreferencesUtils.getBoolean(IntrusionAlert.ON_INTRUSION, false)
        }
//
        checkTurnOn(binding.turnOn.isChecked)
        viewModel.takePhotoFromActivity(this)
        //padding
        setStatusBarHomeTransparent(this)
        binding.intrusionAlert.setPadding(0, getHeightStatusBar(), 0, 0)
        // adapter rcl
        adapterPhoto = PhotoAdapter(this, listPhoto)
        binding.rclPhoto.adapter = adapterPhoto
        //
        binding.txtTime.text = PreferencesUtils.getInteger(KeyLock.TIME_TAKE_PHOTO, 3).toString()
    }

    override fun initData() {
        listPhoto.clear()
        viewModel.photoLiveData.observe(this) {
            if (it.bitmap!=null) {
                listPhoto.add(0, it)
                adapterPhoto.notifyItemInserted(0)
            }
        }
    }

    @KoinApiExtension
    override fun initListener() {
        binding.turnOn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                LockService.isCreate = true // not lock when request any permission
                requestPermission(complete = {
                    if (it) {
                        PreferencesUtils.putBoolean(IntrusionAlert.ON_INTRUSION, isChecked)
                        checkTurnOn(isChecked)
                    } else {
                        PreferencesUtils.putBoolean(IntrusionAlert.ON_INTRUSION, false)
                        binding.turnOn.isChecked = false
                        checkTurnOn(binding.turnOn.isChecked)
                    }
                }, Manifest.permission.CAMERA)
            } else {
                PreferencesUtils.putBoolean(IntrusionAlert.ON_INTRUSION, false)
                checkTurnOn(false)
            }
        }
        //
        binding.icBackLockIntrusionAlert.setOnClickListener {
            onBackPressed()
        }
        // subtraction
        binding.subtraction.setOnClickListener {
            if (PreferencesUtils.getInteger(KeyLock.TIME_TAKE_PHOTO, 3) > 1) {
                val time = PreferencesUtils.getInteger(KeyLock.TIME_TAKE_PHOTO, 3) - 1
                PreferencesUtils.putInteger(KeyLock.TIME_TAKE_PHOTO, time)
                binding.txtTime.text = time.toString()
            }
        }
        // add
        binding.add.setOnClickListener {
            val time = PreferencesUtils.getInteger(KeyLock.TIME_TAKE_PHOTO, 3) + 1
            PreferencesUtils.putInteger(KeyLock.TIME_TAKE_PHOTO, time)
            binding.txtTime.text =
                PreferencesUtils.getInteger(KeyLock.TIME_TAKE_PHOTO, 3).toString()
            binding.txtTime.text = time.toString()
        }
        // clear photo
        binding.icClearPhoto.setOnClickListener {
            if (listPhoto.isNotEmpty()) {
                showDialog(resources.getString(R.string.do_you_want_to_delete_all_the_photo))
            }
        }
        adapterPhoto.onClickListener = {

        }
    }

    private fun checkTurnOn(isChecked: Boolean) {
        if (isChecked) {
            binding.off.setTextColor(resources.getColor(R.color.color_66FFFFFF))
            binding.on.setTextColor(resources.getColor(R.color.white))
        } else {
            binding.off.setTextColor(resources.getColor(R.color.white))
            binding.on.setTextColor(resources.getColor(R.color.color_66FFFFFF))
        }
    }

    private fun showDialog(content: String? = null) {
        val bindDialog =
            LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.let {
            it.setCancelable(true)
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            content?.let { c -> it.binding.txtContent.text = c }
        }

        dialogQuestion?.listenerYes = {
            listPhoto.clear()
            viewModel.delete(this)
            adapterPhoto.notifyDataSetChanged()
            dialogQuestion?.dismiss()
            EzAdControl.getInstance(this).showAds()
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
        }
        dialogQuestion?.setOnDismissListener {

        }
        dialogQuestion?.show()
    }

    override fun viewBinding(): ActivityIntrusionAlertBinding {
        return ActivityIntrusionAlertBinding.inflate(LayoutInflater.from(this))
    }
}