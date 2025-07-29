package com.android.pagingwithflow.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.pagingwithflow.activities.MovieDetails
import com.android.pagingwithflow.databinding.ItenGenreBinding
import com.android.pagingwithflow.integration.dynatrace.DynatraceOpenKitManager
import com.android.pagingwithflow.model.GenreResultList

class GenreAdapter(val ctx: Context, val movies: List<GenreResultList>) :
    RecyclerView.Adapter<GenreAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder = MyViewHolder(
        ItenGenreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        val movie: GenreResultList = movies[position]
        viewHolder.binds(movie)

        viewHolder.itemView.setOnClickListener {
            val genreId: String = movie.id.toString()

            DynatraceOpenKitManager.enterAction("Selecionar Gênero")

            DynatraceOpenKitManager.reportEvent("ID do Gênero: $genreId")

            val intent = Intent(ctx, MovieDetails::class.java)
            intent.putExtra("MovieIdPass", genreId)
            ctx.startActivity(intent)

            DynatraceOpenKitManager.leaveAction()
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    class MyViewHolder(private val binding: ItenGenreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binds(genreResultList: GenreResultList) {
            binding.apply {
                title.text = genreResultList.name
            }
        }
    }
}