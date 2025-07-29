package com.android.pagingwithflow.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.pagingwithflow.R
import com.android.pagingwithflow.activities.MovieDetails
import com.android.pagingwithflow.integration.dynatrace.DynatraceOpenKitManager
import com.android.pagingwithflow.model.UpcomingResultList
import com.android.pagingwithflow.network.NetworkingConstants
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter

class MovieSliderAdapter(val ctx: Context, val movies: List<UpcomingResultList>) :
    SliderViewAdapter<MovieSliderAdapter.MyViewHolder>() {

    override fun getCount(): Int {
        return minOf(movies.size, 5)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
        val view: View = LayoutInflater.from(ctx)
            .inflate(R.layout.item_slider, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        val movie: UpcomingResultList = movies[position]

        Glide
            .with(ctx)
            .load(NetworkingConstants.BASE_POSTER_PATH + (movie.posterPath))
            .into(viewHolder.poster)

        if (movie.adult) {
            viewHolder.adultCheck.text = "18+"
        } else {
            viewHolder.adultCheck.text = "13+"
        }

        viewHolder.movieTitle.text = movie.title
        viewHolder.releaseDate.text = movie.releaseDate

        viewHolder.itemView.setOnClickListener {
            val movieId: String = movie.id.toString()

            DynatraceOpenKitManager.enterAction("Selecionar Filme (Slider)")

            DynatraceOpenKitManager.reportEvent("ID do Filme: $movieId")

            val intent = Intent(ctx, MovieDetails::class.java)
            intent.putExtra("MovieIdPass", movieId)
            ctx.startActivity(intent)

            DynatraceOpenKitManager.leaveAction()
        }
    }

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.imageView_single_movie_slider)
        val movieTitle: TextView = itemView.findViewById(R.id.title_single_movie_slider)
        val titleBig: TextView = itemView.findViewById(R.id.titleBig_single_movie_slider)
        val releaseDate: TextView = itemView.findViewById(R.id.date_single_movie_slider)
        val genre1: TextView = itemView.findViewById(R.id.genre1_movie_slider)
        val genre2: TextView = itemView.findViewById(R.id.genre2_movie_slider)
        val genre2Layout: LinearLayout = itemView.findViewById(R.id.genre2Layout_movie_slider)
        val adultCheck: TextView = itemView.findViewById(R.id.adultCheck_movie_slider)
    }
}
