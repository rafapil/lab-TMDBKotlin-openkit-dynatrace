package com.android.pagingwithflow.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.pagingwithflow.activities.MovieDetails
import com.android.pagingwithflow.databinding.ItemCardMoiveBinding
import com.android.pagingwithflow.integration.dynatrace.DynatraceOpenKitManager
import com.android.pagingwithflow.model.MoiveResultList
import com.android.pagingwithflow.network.NetworkingConstants
import javax.inject.Inject

class DiscoverMovieCardAdapter @Inject constructor() :
    PagingDataAdapter<MoiveResultList, DiscoverMovieCardAdapter.MovieViewHolder>(Diff()) {

    override fun onBindViewHolder(viewHolder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            viewHolder.binds(movie)
        }

        viewHolder.itemView.setOnClickListener {
            val movieId: String? = movie?.id?.toString()
            if (movieId == null) return@setOnClickListener

            DynatraceOpenKitManager.enterAction("Selecionar Filme")

            DynatraceOpenKitManager.reportEvent("ID do Filme: $movieId")

            val intent = Intent(viewHolder.itemView.context, MovieDetails::class.java)
            intent.putExtra("MovieIdPass", movieId)
            viewHolder.itemView.context?.startActivity(intent)

            DynatraceOpenKitManager.leaveAction()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
        MovieViewHolder(
            ItemCardMoiveBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    class MovieViewHolder(private val binding: ItemCardMoiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binds(moiveResultList: MoiveResultList) {
            binding.apply {
                image.load(NetworkingConstants.BASE_BACKDROP_PATH + moiveResultList.backdrop_path)
                name.text = moiveResultList.title
                details.text = moiveResultList.overview
                datePickerActions.text = moiveResultList.release_date
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<MoiveResultList>() {
        override fun areItemsTheSame(oldItem: MoiveResultList, newItem: MoiveResultList): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: MoiveResultList,
            newItem: MoiveResultList
        ): Boolean =
            oldItem == newItem
    }
}