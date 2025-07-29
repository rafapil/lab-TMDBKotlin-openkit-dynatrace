package com.android.pagingwithflow.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.android.pagingwithflow.Utlis.ProgressBarHandler
import com.android.pagingwithflow.adapter.TrailerAdapter
import com.android.pagingwithflow.databinding.ActivityMovieDetailsBinding
import com.android.pagingwithflow.integration.dynatrace.DynatraceOpenKitManager
import com.android.pagingwithflow.network.NetworkingConstants
import com.android.pagingwithflow.repo.MovieViewModel
import com.dynatrace.openkit.util.json.objects.JSONNumberValue
import com.dynatrace.openkit.util.json.objects.JSONStringValue
import com.dynatrace.openkit.util.json.objects.JSONValue
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieDetails : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailsBinding
    private val mainViewModel: MovieViewModel by viewModels()
    var movieId: String? = null
    var progressBar: ProgressBarHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DynatraceOpenKitManager.enterAction("Ver Detalhes do Filme")

        initProgress()
        initApiCalls()

        binding.testCrashBtn.setOnClickListener {

            try {
                DynatraceOpenKitManager.enterAction("Teste de Crash")
                DynatraceOpenKitManager.reportEvent("Botão de crash (Detalhes) clicado")

                throw RuntimeException("Teste de Crash Manual - Debug")

            } catch (e: Exception) {
                val errorName = e.javaClass.simpleName
                val reason =
                    e.message ?: "Causa desconhecida"
                val stacktrace =
                    android.util.Log.getStackTraceString(e)

                DynatraceOpenKitManager.reportError(errorName, e)
                DynatraceOpenKitManager.reportCrash(errorName, reason, stacktrace)

                throw e
            }

        }

        binding.sendEventBtn.setOnClickListener {
            android.widget.Toast.makeText(this, "Evento de teste enviado!", android.widget.Toast.LENGTH_SHORT).show()
            val attributes: MutableMap<String?, JSONValue?> = HashMap<String?, JSONValue?>()
            attributes.put("event.name", JSONStringValue.fromString("Send Event BE"))
            attributes.put("screen", JSONStringValue.fromString("detail movie"))
            attributes.put("product", JSONStringValue.fromString("movie"))
            attributes.put("amount", JSONNumberValue.fromDouble(358.35))
            attributes.put("currency", JSONStringValue.fromString("USD"))
            attributes.put("reviewScore", JSONNumberValue.fromDouble(4.8))

            DynatraceOpenKitManager.sendBusEvent("com.movie-app.funnel.detail-movie", attributes)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        DynatraceOpenKitManager.leaveAction()
    }

    private fun initApiCalls() {
        movieId = intent.getStringExtra("MovieIdPass").toString()

        DynatraceOpenKitManager.reportEvent("Movie ID: $movieId")

        mainViewModel.getMovieDetails(movieId!!)
        mainViewModel.getTrailerResponse(movieId!!)
        binding.backButton.setOnClickListener {
            DynatraceOpenKitManager.reportEvent("Botão Voltar Clicado")
            finish()
        }

        mainViewModel.movieDetailLivedata.observe(this@MovieDetails, Observer { response ->
            progressBar?.hide()
            binding.apply {

                movieBackdropImage.load(NetworkingConstants.BASE_BACKDROP_PATH + response.backdropPath)
                moviePosterImage.load(NetworkingConstants.BASE_POSTER_PATH + response.posterPath)
                movieTitleText.text = response.title
                movieOverviewText.text = response.overview
                movieInfoText.text = response.releaseDate
            }
        })
        mainViewModel.getTrailerLiveData.observe(this, Observer {
            it?.let {
                binding.apply {
                    recyclerviewTrailer.apply {
                        recyclerviewTrailer.adapter =
                            TrailerAdapter(
                                it
                            ) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data =
                                    Uri.parse(NetworkingConstants.YOUTUBE_VIDEO_URL + it.key)
                                startActivity(intent)
                            }
                        layoutManager = LinearLayoutManager(
                            this@MovieDetails,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )

                    }

                }
            }
        })
    }

    private fun initProgress() {
        progressBar = ProgressBarHandler(this)
        progressBar!!.show()
    }


}

