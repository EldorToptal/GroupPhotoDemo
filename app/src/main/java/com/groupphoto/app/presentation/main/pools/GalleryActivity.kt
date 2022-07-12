package com.groupphoto.app.presentation.main.pools

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.groupphoto.app.R
import com.groupphoto.app.data.remote.model.GalleryAssetItem
import com.groupphoto.app.presentation.viewmodels.GalleryEvent
import com.groupphoto.app.presentation.viewmodels.GalleryViewModel
import com.groupphoto.app.util.extensions.loadImage
import com.pawegio.kandroid.e
import com.pawegio.kandroid.startActivity
import com.shehabic.droppy.DroppyMenuCustomItem
import com.shehabic.droppy.DroppyMenuPopup
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.include_bar_gallery.*

import org.koin.androidx.viewmodel.ext.viewModel

class GalleryActivity : AppCompatActivity() {

    var baseUrl = ""
    private val viewModel: GalleryViewModel by viewModel()
    private var isDialogShown = false
    private var currentPosition: Int = 0
    private lateinit var viewer: StfalconImageViewer<GalleryAssetItem>
    private var page = 0
    var maxPage = 0
    var pages : MutableList <MutableList<GalleryAssetItem>>  = mutableListOf()
    var pageItems : MutableList<GalleryAssetItem> = mutableListOf()
    var droppyMenu: DroppyMenuPopup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        baseUrl = getString(R.string.FirebaseBaseUrl) //"https://group-photo-prod.firebaseapp.com/"
        img_one.setOnClickListener {
//           openViewer(0)
        }
        gallery_settings_button.setOnClickListener {
            showGallerySettings(placeholder_dropdown)
        }

        imageView19.setOnClickListener {
            onBackPressed()
        }
        viewModel.getImageList()
//        showGallerySettings(gallery_settings_button)
//        webView.settings.useWideViewPort = true
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is GalleryEvent.OnGetAssetsList -> loadImages(event.assets)
            }
        })
//        loadImages()
        hideRings()
        hideRatings()
        hideLabels()
        hideLabelBg()

        next_btn.setOnClickListener {
            if (pages.size-1 > page){
                page ++
                showImages(pages[page])
            }
        }

        prev_btn.setOnClickListener {
            if (page > 0){
                page--
                showImages(pages[page])
            }
        }

    }

    private fun openViewer(startPosition: Int) {
        viewer = StfalconImageViewer.Builder<GalleryAssetItem>(this, pages[page], ::loadPosterImage)
            .withTransitionFrom(getTransitionTarget(startPosition))
            .withStartPosition(startPosition)
            .withImageChangeListener {
                currentPosition = it
                viewer.updateTransitionImage(getTransitionTarget(it))
            }
            .withDismissListener { isDialogShown = false }
            .show(!isDialogShown)

        currentPosition = startPosition
        isDialogShown = true
    }
    private fun loadPosterImage(imageView: ImageView, poster: GalleryAssetItem?) {
        imageView.apply {
//            background = getDrawableCompat(R.drawable.shape_placeholder)
            loadImage(baseUrl + poster?.renderings!![1].path)
        }
    }
    private fun getTransitionTarget(position: Int) =
        if (position == 0) img_five else null


    fun loadImages(assets: MutableList<GalleryAssetItem>?) {

        if (assets != null){
            e("assets count = ${assets!!.size}")
            var ctr = 0
            assets!!.forEach { item ->
                pageItems.add(item)
                ctr++
                if (ctr == 7){
                    pages.add(pageItems)
                    pageItems = mutableListOf()
                    ctr = 0
                }
            }

            maxPage = pages.size

            e("maxpage $maxPage")
//        showImages(pages[page])



            if(pages.size > 0){
                showImages(pages[page])

            }
        }


    }

    fun showImages(assets: MutableList<GalleryAssetItem>?) {

        for ((counter, item) in assets!!.withIndex()){
            when(counter % 8) {
                0 -> Glide.with(this).load(baseUrl + item.renderings!![0].path).into(img_one)
                1 -> Glide.with(this).load(baseUrl + item.renderings!![0].path).into(img_two)
                2 -> Glide.with(this).load(baseUrl + item.renderings!![0].path).into(img_three)
                3 -> Glide.with(this).load(baseUrl + item.renderings!![0].path).into(img_four)
                4 -> Glide.with(this).load(baseUrl + item.renderings!![0].path).into(img_five)
                5 -> Glide.with(this).load(baseUrl + item.renderings!![0].path).into(img_six)
                6 -> Glide.with(this).load(baseUrl + item.renderings!![0].path).into(img_seven)
                7 -> Glide.with(this).load(baseUrl + item.renderings!![0].path).into(img_eight)
            }
        }

    }

    private fun showGallerySettings(v: View) {

        val droppyBuilder = DroppyMenuPopup.Builder(this, v)

        val settingsItems = DroppyMenuCustomItem(R.layout.alert_gallery_settings)
        droppyBuilder
            .addMenuItem(settingsItems)

        droppyMenu = droppyBuilder.build()

        droppyMenu!!.show()


        val rate =
            droppyMenu!!.menuView.findViewById<View>(R.id.option_rate) as ConstraintLayout
        val poolChat =
            droppyMenu!!.menuView.findViewById<View>(R.id.option_pool_chat) as ConstraintLayout
        val shareRings =
            droppyMenu!!.menuView.findViewById<View>(R.id.option_share_rings) as ConstraintLayout
        val copyShare =
            droppyMenu!!.menuView.findViewById<View>(R.id.option_copy_share) as ConstraintLayout
        val addMedia =
            droppyMenu!!.menuView.findViewById<View>(R.id.option_add_media) as ConstraintLayout
        val select =
            droppyMenu!!.menuView.findViewById<View>(R.id.option_select) as ConstraintLayout
        val poolSettings =
            droppyMenu!!.menuView.findViewById<View>(R.id.option_pool_settings) as ConstraintLayout



        rate.setOnClickListener {
            hideLabels()
            showRatings()
            hideRings()
            hideCheckbox()
            droppyMenu!!.dismiss(true)
        }

        poolChat.setOnClickListener {
            droppyMenu!!.dismiss(true)
        }
        shareRings.setOnClickListener {
            hideLabels()
            showRings()
            hideRatings()
            hideCheckbox()
            droppyMenu!!.dismiss(true)
        }
        copyShare.setOnClickListener {
            droppyMenu!!.dismiss(true)
        }
        addMedia.setOnClickListener {
            droppyMenu!!.dismiss(true)
        }
        select.setOnClickListener {
            showCheckbox()
            showLabels()
            hideRatings()
            hideRings()
            droppyMenu!!.dismiss(true)
        }
        poolSettings.setOnClickListener {
            startActivity<PoolSettingsActivity>()
            droppyMenu!!.dismiss(true)
        }
    }
    fun hideLabels(){
        img1_tv.visibility = View.GONE
        img2_tv.visibility = View.GONE
        img3_tv.visibility = View.GONE
        img4_tv.visibility = View.GONE
        img5_tv.visibility = View.GONE
        img6_tv.visibility = View.GONE
        img7_tv.visibility = View.GONE
        img8_tv.visibility = View.GONE
    }
    fun hideLabelBg(){
        img1_label.visibility = View.GONE
        img2_label.visibility = View.GONE
        img3_label.visibility = View.GONE
        img4_label.visibility = View.GONE
        img5_label.visibility = View.GONE
        img6_label.visibility = View.GONE
        img7_label.visibility = View.GONE
        img8_label.visibility = View.GONE
    }
    fun showLabels(){
        img1_tv.visibility = View.VISIBLE
        img2_tv.visibility = View.VISIBLE
        img3_tv.visibility = View.VISIBLE
        img4_tv.visibility = View.VISIBLE
        img5_tv.visibility = View.VISIBLE
        img6_tv.visibility = View.VISIBLE
        img7_tv.visibility = View.VISIBLE
        img8_tv.visibility = View.VISIBLE
    }
    fun showRatings(){

        img1_stars.visibility = View.VISIBLE
        img2_stars.visibility = View.VISIBLE
        img3_stars.visibility = View.VISIBLE
        img4_stars.visibility = View.VISIBLE
        img5_stars.visibility = View.VISIBLE
        img6_stars.visibility = View.VISIBLE
        img7_stars.visibility = View.VISIBLE
        img8_stars.visibility = View.VISIBLE
    }
    fun hideRatings(){

        img1_stars.visibility = View.GONE
        img2_stars.visibility = View.GONE
        img3_stars.visibility = View.GONE
        img4_stars.visibility = View.GONE
        img5_stars.visibility = View.GONE
        img6_stars.visibility = View.GONE
        img7_stars.visibility = View.GONE
        img8_stars.visibility = View.GONE
    }
    fun showRings(){
        img1_ring.visibility = View.VISIBLE
        img2_ring.visibility = View.VISIBLE
        img3_ring.visibility = View.VISIBLE
        img4_ring.visibility = View.VISIBLE
        img5_ring.visibility = View.VISIBLE
        img6_ring.visibility = View.VISIBLE
        img7_ring.visibility = View.VISIBLE
        img8_ring.visibility = View.VISIBLE
    }
    fun hideRings(){
        img1_ring.visibility = View.GONE
        img2_ring.visibility = View.GONE
        img3_ring.visibility = View.GONE
        img4_ring.visibility = View.GONE
        img5_ring.visibility = View.GONE
        img6_ring.visibility = View.GONE
        img7_ring.visibility = View.GONE
        img8_ring.visibility = View.GONE
    }
    fun showCheckbox(){
        img1_checkbox.visibility = View.VISIBLE
        img2_checkbox.visibility = View.VISIBLE
        img3_checkbox.visibility = View.VISIBLE
        img4_checkbox.visibility = View.VISIBLE
        img5_checkbox.visibility = View.VISIBLE
        img6_checkbox.visibility = View.VISIBLE
        img7_checkbox.visibility = View.VISIBLE
        img8_checkbox.visibility = View.VISIBLE
    }
    fun hideCheckbox(){
        img1_checkbox.visibility = View.GONE
        img2_checkbox.visibility = View.GONE
        img3_checkbox.visibility = View.GONE
        img4_checkbox.visibility = View.GONE
        img5_checkbox.visibility = View.GONE
        img6_checkbox.visibility = View.GONE
        img7_checkbox.visibility = View.GONE
        img8_checkbox.visibility = View.GONE
    }



    companion object {
        private const val TAG = "Gallery Activity "
    }
}