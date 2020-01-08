package com.adsale.chinaplas.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.adsale.chinaplas.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test)


//        LogUtil.i("initData:::::to registerPreFragment")
//        val navController: NavController = findNavController(R.id.nav_host_fragment)
//        navController.navigate(R.id.registerWebsiteFragment)
    }

//    override fun initView() {
//
//    }
//
//    override fun initData() {
//
//    }

//    override fun initView() {
//        LogUtil.i("~~~~~ RegisterActivity ~~~~")
//    }
//
//    override fun initData() {
//        LogUtil.i("initData:::::to registerPreFragment")
//        val navController: NavController = findNavController(R.id.nav_host_fragment)
//        navController.navigate(R.id.registerWebsiteFragment)
//
//        if (isPaySuccess()) {
//            if (File(confirmPdfPath).exists()) {
//                /* open pdf */
////                val pdfPath = "file:///android_asset/pdfjs/web/viewer.html?file=$confirmPdfPath"
//                val pdfPath = localConfirmPdfPath + confirmPdfPath
////                navController.navigate(R.id.registerWebsiteFragment)
////                navController
////                    .navigate(RegisterActivityDirections.actionRegisterActivityToRegisterWebsiteFragment(pdfPath))
//            } else {
//                /* down pdf */
////                navController
////                    .navigate(RegisterActivityDirections.actionRegisterActivityToRegisterWebsiteFragment(getConfirmPdfUrl()))
////                navController.navigate(R.id.registerWebsiteFragment)
//            }
//        } else {
//            /* 预登记前置页，检测账户是否存在 */
////            navController
////                .navigate(RegisterActivityDirections.actionRegisterActivityToRegisterPreFragment())
////            navController.navigate(R.id.registerPreFragment)
//        }
//    }


    /**
     * 公司业务  “其他”输入框集合
     */
    private var otherCollections: MutableList<String> = mutableListOf()


    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }
}
