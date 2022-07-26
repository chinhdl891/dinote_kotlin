package com.bzk.dinoteslite.view.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.ActivityMainBinding
import com.bzk.dinoteslite.reciver.TimeRemindReceiver
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.view.dialog.ExitDialog
import com.bzk.dinoteslite.view.fragment.MainFragment
import java.util.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    var timeRemindDefault: Long = 0L
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        onsetUpTheme()
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(mBinding.root)
        checkPermissions()

    }

    private fun onsetUpTheme() {
        val theme: Int = MySharedPreferences(this).getTheme()
        val nightMode =
            if (theme == 1) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstant.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, R.string.write_debied, Toast.LENGTH_SHORT)
                    .show()
            }
            checkPermissions()
        }

    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE),
                AppConstant.PERMISSION_WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onBackPressed() {
        if (isMainFragment()) {
            MainFragment.closeDrawer()
            ExitDialog(this@MainActivity, onFinish = {
                finish()
            }).show()
        } else {
            findNavController(R.id.nav_host_fragment).popBackStack()
        }
    }

    private fun isMainFragment(): Boolean {
        val controller = findNavController(R.id.nav_host_fragment)
        return controller.currentDestination?.id == R.id.mainFragment
    }
}
