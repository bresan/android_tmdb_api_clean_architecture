package com.rodrigobresan.sampleboilerplateandroid.movies.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.rodrigobresan.domain.model.MovieCategory
import com.rodrigobresan.presentation.movies.contract.MoviesContract
import com.rodrigobresan.presentation.movies.model.MovieView
import com.rodrigobresan.sampleboilerplateandroid.R
import com.rodrigobresan.sampleboilerplateandroid.movies.mapper.MovieMapper
import com.rodrigobresan.sampleboilerplateandroid.movies.ui.adapter.MoviesAdapter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_movies.*
import javax.inject.Inject
import android.app.ActivityOptions
import com.rodrigobresan.sampleboilerplateandroid.movie_detail.ui.activity.MovieDetailActivity
import com.rodrigobresan.sampleboilerplateandroid.movies.ui.view.MovieSectionView

class MoviesActivity : AppCompatActivity(), MoviesContract.View, MoviesAdapter.MovieClickListener {

    @Inject lateinit var moviePresenter: MoviesContract.Presenter
    @Inject lateinit var popularMoviesAdapter: MoviesAdapter
    @Inject lateinit var topRatedMoviesAdapter: MoviesAdapter
    @Inject lateinit var upcomingMoviesAdapter: MoviesAdapter
    @Inject lateinit var nowPlayingMoviesAdapter: MoviesAdapter

    @Inject lateinit var movieMapper: MovieMapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_movies)
        AndroidInjection.inject(this)
    }

    override fun onMovieSelected(id: Long, imageView: ImageView) {
        val options: ActivityOptions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(this, imageView, "transition")
            val intent = MovieDetailActivity.makeIntent(this, id)
            startActivity(intent, options.toBundle())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.movies, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_top_rated_refresh -> {
                moviePresenter.loadMovies()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        moviePresenter.loadMovies()
    }

    override fun onStop() {
        super.onStop()
        moviePresenter.stop()
    }

    override fun setPresenter(presenter: MoviesContract.Presenter) {
        this.moviePresenter = presenter
    }

    override fun hideProgress(category: MovieCategory) {
        getMovieSectionView(category).hideProgress()
    }

    override fun showProgress(category: MovieCategory) {
        getMovieSectionView(category).showProgress()
    }

    override fun showErrorState(category: MovieCategory) {
        getMovieSectionView(category).showErrorState()
    }

    override fun hideErrorState(category: MovieCategory) {
        getMovieSectionView(category).hideErrorState()
    }

    override fun showEmptyState(category: MovieCategory) {
        getMovieSectionView(category).showEmptyState()
    }

    override fun hideEmptyState(category: MovieCategory) {
        getMovieSectionView(category).hideEmptyState()
    }

    override fun showMovies(category: MovieCategory, movies: List<MovieView>) {
        getMovieSectionView(category).showMovies(getMovieAdapter(category),
                movies.map { movieMapper.mapToViewModel(it) })
    }

    override fun hideMovies(category: MovieCategory) {
        getMovieSectionView(category).hideMovies()
    }

    fun getMovieSectionView(movieCategory: MovieCategory): MovieSectionView {
        when (movieCategory) {
            MovieCategory.TOP_RATED -> return movie_section_top_rated
            MovieCategory.NOW_PLAYING -> return movie_section_now_playing
            MovieCategory.UPCOMING -> return movie_section_upcoming
            MovieCategory.POPULAR -> return movie_section_popular
        }
    }

    fun getMovieAdapter(movieCategory: MovieCategory): MoviesAdapter {
        when (movieCategory) {
            MovieCategory.TOP_RATED -> return topRatedMoviesAdapter
            MovieCategory.NOW_PLAYING -> return nowPlayingMoviesAdapter
            MovieCategory.UPCOMING -> return upcomingMoviesAdapter
            MovieCategory.POPULAR -> return popularMoviesAdapter
        }
    }

}