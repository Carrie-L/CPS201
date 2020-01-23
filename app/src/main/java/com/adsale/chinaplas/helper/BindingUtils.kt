package com.adsale.chinaplas.helper

import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.ExhibitorListAdapter
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.data.dao.MainIcon
import com.adsale.chinaplas.data.dao.RegOptionData
import com.adsale.chinaplas.data.entity.CountryJson
import com.adsale.chinaplas.hasNetwork
import com.adsale.chinaplas.network.BASE_URL
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.LogUtil.i
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File

/**
 * Created by Carrie on 2019/10/18.
 */

//fun ImageView.setMainIcon(item:MainIcon){
//    Glide.with(context).load(BASE_URL+item.Icon).into(imageV)
//}

@BindingAdapter("mainIcon")
fun setMainIcon(imgView: ImageView, item: MainIcon) {
    item.let {
        if (hasNetwork) {
            val imgUri = (BASE_URL + item.Icon).toUri().buildUpon().scheme("https").build()
            Glide.with(imgView.context).load(imgUri).apply(RequestOptions()).into(imgView)
        } else {
            Glide.with(imgView.context).load("file:///android_asset/" + item.Icon)
                .apply(RequestOptions()).into(imgView)
        }

    }
}

//@BindingAdapter("textHintSize")
//fun EditText.textSize(hint: String) {
//    textSize = 16f
//    val ss = SpannableString(hint)
//    val ass = AbsoluteSizeSpan(12, true)
//    ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//    setHint(SpannableString(ss))
//    setHintTextColor(resources.getColor(R.color.grey_400))
//}

//@BindingAdapter("textHintSize")
//fun EditText.textSize(hint: Int) {
//    textSize = 16f
//    val ss = SpannableString(resources.getString(hint))
//    val ass = AbsoluteSizeSpan(12, true)
//    ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//    setHint(SpannableString(ss))
//    setHintTextColor(resources.getColor(R.color.grey_400))
//}

@BindingAdapter("textHintSize")
fun textSize(editText: EditText, hint: String) {
    editText.textSize = 16f
    val ss = SpannableString(hint)
    val ass = AbsoluteSizeSpan(12, true)
    ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    editText.hint = SpannableString(ss)
    editText.setHintTextColor(editText.resources.getColor(R.color.reg_text_color_2))
}

@BindingAdapter("textHintSize")
fun TextView.textSize(hint: String) {
    textSize = 16f
    val ss = SpannableString(hint)
    val ass = AbsoluteSizeSpan(12, true)
    ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    setHint(SpannableString(ss))
    setHintTextColor(resources.getColor(R.color.reg_text_color_2))
}

@BindingAdapter("imagePath", "requestOptions")
fun setImagePath(imageView: ImageView,
                 absolutePath: String,
                 options: RequestOptions?) {
    i( "absolutePath=$absolutePath")
    if (TextUtils.isEmpty(absolutePath)) {
        return
    }
    Glide.with(imageView.context)
        .load(File(absolutePath))
        .apply(options!!)
        .into(imageView)
}

@BindingAdapter("itemTextColor")
fun setItemColor(textView: TextView, item: CountryJson) {
    LogUtil.i("setItemColor pressed: ${item.isChecked.get()}, ${item.getName()}")
    if (item.isChecked.get()!!) {
        textView.setTextColor(textView.resources.getColor(R.color.colorAccent))
    } else {
        textView.setTextColor(textView.resources.getColor(R.color.grey_700))
    }
}

@BindingAdapter("regProductParent")
fun setRegProductParent(textView: TextView, item: RegOptionData) {
    if (item.isProductParent()) {
        textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    } else {
        textView.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    }
}

@BindingAdapter("iconID")
fun ImageView.setIconID(id: String) {
    val sdPath = "file://${rootDir}icons/${id}.png}"
    if (File(sdPath).exists()) {
        Glide.with(this).load(sdPath).into(this)
    } else {
        Glide.with(this).load("file:///android_asset/icons/${id}.png").into(this)
    }
}

@BindingAdapter("iconRes")
fun ImageView.setIconRes(resId: Int) {
    setBackgroundResource(resId)
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Exhibitor>?) {
    LogUtil.i("bindRecyclerView: data=${data?.size}")
    if( recyclerView.adapter!=null){
//        val adapter = recyclerView.adapter as ExhibitorListAdapter
//        adapter.submitList(data)
    }
}

@BindingAdapter("imgUrl")
fun setImgUrl(imageView: ImageView, url: String) {
    if (TextUtils.isEmpty(url)) {
        return
    }
    if (url.toLowerCase().startsWith("http")) {
        Glide.with(imageView.context).load(Uri.parse(url)).into(imageView)
    } else { // 侧边栏按钮：当无网时显示asset下的Icon
        Glide.with(imageView.context).load("file:///android_asset/MainIcon/$url").into(imageView)
        i("menu: asset: $url")
    }
}



