package com.android.pagingwithflow.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.pagingwithflow.activities.MovieDetails
import com.android.pagingwithflow.databinding.ItemMovieThmBinding
import com.android.pagingwithflow.integration.dynatrace.DynatraceOpenKitManager // Importe o manager
import com.android.pagingwithflow.model.UpcomingResultList
import com.android.pagingwithflow.network.NetworkingConstants

class PopularMovieAdapter(val ctx: Context, val movies: List<UpcomingResultList>) :
    RecyclerView.Adapter<PopularMovieAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder = MyViewHolder(
        ItemMovieThmBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        val movie: UpcomingResultList = movies[position]
        viewHolder.binds(movie)

        viewHolder.itemView.setOnClickListener {
            val movieId: String = movie.id.toString()

            DynatraceOpenKitManager.enterAction("Selecionar Filme (Popular)")

            DynatraceOpenKitManager.reportEvent("ID do Filme: $movieId")

            val intent = Intent(ctx, MovieDetails::class.java)
            intent.putExtra("MovieIdPass", movieId)
            ctx.startActivity(intent)

            DynatraceOpenKitManager.leaveAction()
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    class MyViewHolder(private val binding: ItemMovieThmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binds(moiveResultList: UpcomingResultList) {
            binding.apply {
                movieImage.load(NetworkingConstants.BASE_POSTER_PATH + moiveResultList.posterPath)
                movieTitle.text = moiveResultList.title
                year.text = moiveResultList.releaseDate
            }
        }
    }
}
