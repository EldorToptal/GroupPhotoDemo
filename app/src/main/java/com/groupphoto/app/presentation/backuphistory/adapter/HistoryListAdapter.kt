package com.groupphoto.app.presentation.backuphistory.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.util.extensions.decimalPlaces

//TODO Change the adapter to the RecyclerView
class HistoryListAdapter(private val context: Context) : BaseAdapter() {

    var backupEntity : List<BackupEntity>? = ArrayList()
    var typeIdx = 0

    fun HistoryListAdapter(history : List<BackupEntity>, typeIndex: Int){
        backupEntity = history
        typeIdx = typeIndex
//        notifyDataSetChanged()
    }
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return backupEntity!!.size
    }

    override fun getItem(position: Int): Any {
        return backupEntity!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.item_history_list, parent, false)

        val imvPhoto = rowView.findViewById(R.id.ivPhoto) as ImageView
        val txvTitle = rowView.findViewById(R.id.tvPhotoTitle) as TextView
        val txvSize = rowView.findViewById(R.id.tvFileSize) as TextView
        val txvStatusNum = rowView.findViewById(R.id.tvProgressStatus) as TextView
        val txvStatusTx = rowView.findViewById(R.id.tvStatus) as TextView
        val progressBar = rowView.findViewById(R.id.pbUploadingImage) as ProgressBar

        val entity = getItem(position) as BackupEntity

        when(typeIdx){

            //0-Active, 1-Completed, 2-Failed
            0 -> {
                Glide.with(context).load(entity.path).into(imvPhoto)
                txvTitle.text = entity.fileName
                txvSize.text = "${entity.fileSize.decimalPlaces(2)} MB"
                txvStatusNum.text = "${entity.uploadPercentage} %"
                progressBar.progress = entity.uploadPercentage
            }

            1 -> {
                progressBar.visibility = View.GONE
                txvStatusNum.visibility = View.GONE
                txvStatusTx.visibility = View.GONE

                Glide.with(context).load(entity.path).into(imvPhoto)
                txvTitle.text = entity.fileName
                txvSize.text = "${entity.fileSize.decimalPlaces(2)} MB"
            }

            2 -> {

                progressBar.progress = 0
                txvSize.text = "${progressBar.progress} MB"
                progressBar.progressDrawable.setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
                txvStatusNum.text = "Failed"
                txvStatusNum.setTextColor(Color.parseColor("#FF0000"))
                txvStatusTx.visibility = View.GONE

                Glide.with(context).load(entity.path).into(imvPhoto)
                txvTitle.text = entity.fileName
            }
        }

        return rowView
    }
}