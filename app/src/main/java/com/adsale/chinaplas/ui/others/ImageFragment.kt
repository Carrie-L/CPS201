package com.adsale.chinaplas.ui.others


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adsale.chinaplas.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView

/**
 * 查看图片，可放大缩小
 */
class ImageFragment : Fragment() {
    private var beforeScale = 1.0f   //之前的伸缩值
    private var nowScale = 0f  //当前的伸缩值
    //    private lateinit var imageView: ImageView
    private lateinit var imageView: PhotoView
    private var image: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        imageView = view.findViewById(R.id.iv_image_scale)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            image = ImageFragmentArgs.fromBundle(it).image
        }

        val options = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        imageView.isZoomable = true
        Glide.with(requireContext())
            .load(image)
            .apply(options).into(imageView);

    }


}
