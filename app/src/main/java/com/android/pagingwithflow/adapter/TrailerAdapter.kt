package com.android.pagingwithflow.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.pagingwithflow.R
import com.android.pagingwithflow.databinding.ItemTrailerBinding
import com.android.pagingwithflow.integration.dynatrace.DynatraceOpenKitManager
import com.android.pagingwithflow.model.TrailerResultList
import com.android.pagingwithflow.network.NetworkingConstants
import com.android.pagingwithflow.network.NetworkingConstants.YOUTUBE_THUMBNAIL_URL_ENDPOINT

class TrailerAdapter(
    var trailerList: List<TrailerResultList>,
    val trailerOnClick: (TrailerResultList) -> Unit
) : RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {

    class TrailerViewHolder(val binding: ItemTrailerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trailer: TrailerResultList, trailerOnClick: (TrailerResultList) -> Unit) {
            itemView.setOnClickListener {

                DynatraceOpenKitManager.enterAction("Selecionar Trailer")

                DynatraceOpenKitManager.reportEvent("Trailer Key: ${trailer.key}")

                trailerOnClick(trailer)

                DynatraceOpenKitManager.leaveAction()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemTrailerBinding>(
            inflater,
            R.layout.item_trailer, parent, false
        )
        return TrailerViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return trailerList.size
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        val trailer = trailerList[position]
        holder.binding.trailer = trailer
        holder.bind(trailer, trailerOnClick)
        holder.binding.itemVideoThumbnail.load(NetworkingConstants.YOUTUBE_THUMBNAIL_URL + trailer.key + YOUTUBE_THUMBNAIL_URL_ENDPOINT)
    }
}
