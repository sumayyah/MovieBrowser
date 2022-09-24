package com.sumayyah.moviebrowser.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.sumayyah.moviebrowser.MainApplication
import com.sumayyah.moviebrowser.R
import com.sumayyah.moviebrowser.model.Movie
import javax.inject.Inject

class MainFragment: Fragment() {

    private lateinit var errorView: TextView
    private lateinit var progressView: ProgressBar
    private lateinit var contentView: RecyclerView
    private lateinit var swipeView: SwipeRefreshLayout

    private lateinit var adapter: GridAdapter

    @Inject
    lateinit var factory: MainViewModelFactory

    private val viewModel: MainViewModel by viewModels { factory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as MainApplication).component.inject(this)
    }

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

        setObserver()

        //Setup adapter
        contentView = view.findViewById(R.id.content)

        adapter = GridAdapter(
            requireContext(),
            listOf(),
            ::itemClicked
        )

        contentView.adapter = adapter
        contentView.layoutManager = GridLayoutManager(activity, 3)

        //Setup swipe layout
        //TODO
        swipeView.setOnRefreshListener { }

        return view
    }

    private fun setObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MainViewModel.UIState.SUCCESS -> showSuccessState(state.list)
                is MainViewModel.UIState.ERROR -> showErrorState()
                is MainViewModel.UIState.LOADING -> showLoadingState()
            }
        }
    }

    private fun showLoadingState() {
        errorView.visibility = View.GONE
        contentView.visibility = View.GONE
        progressView.visibility = View.VISIBLE

    }

    private fun showSuccessState(newList: List<Movie>) {
        errorView.visibility = View.GONE
        contentView.visibility = View.VISIBLE
        progressView.visibility = View.GONE
        swipeView.isRefreshing = false

        adapter.swapData(newList)
    }

    private fun showErrorState() {
        errorView.visibility = View.VISIBLE
        contentView.visibility = View.GONE
        progressView.visibility = View.GONE
        swipeView.isRefreshing = false
    }

    private fun itemClicked(id: Int?) {
        // notify the ViewModel and navigate if necessary
    }

    class GridAdapter(
        private val context: Context,
        private var list: List<Movie>,
        private val clickListener : (Int?) -> Unit
    ) : RecyclerView.Adapter<GridAdapter.MovieViewHolder>() {

        class MovieViewHolder(private val view: View, val context: Context): RecyclerView.ViewHolder(view) {
            private val posterView = view.findViewById<ImageView>(R.id.poster)
            fun bind(clicklistener: (Int?) -> Unit, movie: Movie) {

                movie.posterPath?.let {
                    //TODO use config api to get baseurl and width
                    val url = "http://image.tmdb.org/t/p/w92"+it.toUri()
                    Glide.with(context)
                        .load(url)
                        .into(posterView)
                }

                posterView.setOnClickListener { clicklistener(movie.id) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            return MovieViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_grid_item, parent, false), context)
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            holder.bind(clickListener, list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun swapData(newList: List<Movie>) {
            list = newList
        }
    }
}