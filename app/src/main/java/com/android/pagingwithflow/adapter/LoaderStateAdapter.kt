package com.android.pagingwithflow.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.pagingwithflow.databinding.ErrorStateBinding
import com.android.pagingwithflow.integration.dynatrace.DynatraceOpenKitManager

class LoaderStateAdapter constructor(private val retry: () -> Unit) :
    LoadStateAdapter<LoaderStateAdapter.LoaderStateViewHolder>() {

    override fun onBindViewHolder(holder: LoaderStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoaderStateViewHolder {
        return LoaderStateViewHolder(
            ErrorStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retry
        )
    }

    class LoaderStateViewHolder(private val binding: ErrorStateBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentLoadState: LoadState? = null

        init {
            binding.retryBtn.setOnClickListener {

                DynatraceOpenKitManager.enterAction("Tentar Novamente (Paging)")

                val errorState = currentLoadState as? LoadState.Error
                if (errorState != null) {
                    DynatraceOpenKitManager.reportError(
                        "Falha no carregamento da página",
                        errorState.error
                    )
                }

                retry()

                DynatraceOpenKitManager.leaveAction()
            }
        }

        fun bind(loadState: LoadState) {
            this.currentLoadState = loadState

            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                retryBtn.isVisible = loadState is LoadState.Error
                errorTxt.isVisible = loadState is LoadState.Error
            }
        }
    }
}