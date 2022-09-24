package com.sumayyah.moviebrowser.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.sumayyah.moviebrowser.R

class MainFragment: Fragment() {

    private lateinit var errorView: TextView
    private lateinit var progressView: ProgressBar
    private lateinit var contentView: RecyclerView
    private lateinit var swipeView: SwipeRefreshLayout

    private lateinit var adapter: GridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Setup views
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        errorView = view.findViewById(R.id.errorView)
        contentView = view.findViewById(R.id.content)
        progressView = view.findViewById(R.id.loading_indicator)
        swipeView = view.findViewById(R.id.swipeContainer)

        //Setup adapter
        contentView = view.findViewById(R.id.content)

        adapter = GridAdapter(
            requireContext(),
            listOf(1,2,3,4,5,6,7,8,9,10),
            ::itemClicked
        )

        contentView.adapter = adapter
        contentView.layoutManager = GridLayoutManager(activity, 3)

        //Setup swipe layout
        //TODO
        swipeView.setOnRefreshListener { }

        showSuccessState()

        return view
    }

    private fun showLoadingState() {
        errorView.visibility = View.GONE
        contentView.visibility = View.GONE
        progressView.visibility = View.VISIBLE

    }

    private fun showSuccessState() {
        errorView.visibility = View.GONE
        contentView.visibility = View.VISIBLE
        progressView.visibility = View.GONE
        swipeView.isRefreshing = false
    }

    private fun showErrorState() {
        errorView.visibility = View.VISIBLE
        contentView.visibility = View.GONE
        progressView.visibility = View.GONE
        swipeView.isRefreshing = false
    }

    private fun itemClicked(id: String?) {
        // notify the ViewModel and navigate if necessary
    }

    class GridAdapter(
        val context: Context,
        var list: List<Int>,
        val clickListener : (String?) -> Unit
    ) : RecyclerView.Adapter<GridAdapter.GifViewHolder>() {

        class GifViewHolder(private val view: View, val context: Context): RecyclerView.ViewHolder(view) {
            fun bind(clicklistener: (String?) -> Unit) {
               //TODO
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
            return GifViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_grid_item, parent, false), context)
        }

        override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
            holder.bind(clickListener)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun swapData(newList: List<Int>) {
            list = newList
        }
    }
}