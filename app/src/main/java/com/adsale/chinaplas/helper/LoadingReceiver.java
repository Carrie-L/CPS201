package com.adsale.chinaplas.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.adsale.chinaplas.APP;
import com.adsale.chinaplas.utils.AppUtilKt;
import com.adsale.chinaplas.utils.LogUtil;

import kotlin.reflect.jvm.internal.impl.load.java.Constant;

import static com.adsale.chinaplas.utils.AppUtilKt.getCountDownFinish;
import static com.adsale.chinaplas.utils.AppUtilKt.getIntentImmediately;
import static com.adsale.chinaplas.utils.ConstantKt.LOADING_D1_FINISH;
import static com.adsale.chinaplas.utils.ConstantKt.LOADING_SIZE;
import static com.adsale.chinaplas.utils.ConstantKt.LOADING_TXT_FINISH;
import static com.adsale.chinaplas.utils.ConstantKt.SP_CONFIG;

/**
 * Created by Carrie on 2017/9/15.
 */

public class LoadingReceiver extends BroadcastReceiver {
    private String[] items = {
            "ad count down",
            "ad.txt",
            "pdf.txt",
//            todo "mainBanner",
            "regOptions",
            "country",
            "webContent",
            "fileControl",
            "mainIcon"
    };
    private static final String TAG = "LoadingReceiver";
    public static final String LOADING_ACTION = "com.adsale.chinaplas.LoadingReceiver";
    private SharedPreferences sp;
    private int loadingSize = 0;
    private boolean countDownFinish = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE);
        boolean isM1ShowFinish = sp.getBoolean(LOADING_D1_FINISH, false);
        boolean isMainIconFinish = sp.getBoolean("mainIconFinish", false);  // 主界面数据库加载完成
        boolean isTxtDownFinish = sp.getBoolean(LOADING_TXT_FINISH, false);
        boolean isWebServicesDownFinish = sp.getBoolean("webServicesDownFinish", false);
        boolean isApkDownFinish = sp.getBoolean("apkDialogFinish", false); /* 1. 没有更新时，true； 2. 有更新，点击了（不管是yes or no），true; 3. 有更新，没有点击，false   */

       String type =  intent.getStringExtra("type");


//        LogUtil.i("m1 = " + isM1ShowFinish + ", txt = " + isTxtDownFinish + ", isMainIconFinish =" + isMainIconFinish + ",isApkDownFinish=" + isApkDownFinish);
//
//        if (!hasNetwork) {
//            isApkDownFinish = true;
//        }

//        if (isM1ShowFinish && isTxtDownFinish && isWebServicesDownFinish && isApkDialogClicked) {
//            String companyId = sp.getString("M1ClickId", "");
//            LogUtil.i(TAG, "companyId=" + companyId);
//            mListener.intent(companyId); //todo zszs
//        }

//
//        if (isM1ShowFinish && isTxtDownFinish) { // && isApkDownFinish  && isMainIconFinish
//            String companyId = sp.getString("M1ClickId", "");
////            LogUtil.i("companyId=" + companyId);
//            mListener.intent(companyId);
//        }


//        if (!getCountDownFinish()) {
//            loadingSize++;
//        }

        loadingSize++;


//        && getCountDownFinish()

//        if (getIntentImmediately()) {  // 无网络立刻跳转
//            mListener.intent("");
//        } else

// LOADING_SIZE
        if (loadingSize == items.length && isM1ShowFinish) {
            mListener.intent("");
        }

//        Log.i("cps:LoadingReceiver", "loadingSize=" + loadingSize + ", getCountDownFinish=" + getCountDownFinish());
        Log.i("cps:LoadingReceiver", "loadingSize=" + loadingSize + ", type=" + type);


    }

    public interface OnLoadFinishListener {
        void intent(String companyId);
    }

    private OnLoadFinishListener mListener;

    public void setOnLoadFinishListener(OnLoadFinishListener listener) {
        mListener = listener;
    }

}
