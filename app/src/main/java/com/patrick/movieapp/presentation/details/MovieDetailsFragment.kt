package com.patrick.movieapp.presentation.details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.R
import com.patrick.movieapp.data.repository.MovieDetailsRepository
import com.patrick.movieapp.databinding.FragmentMovieDetailsBinding
import com.patrick.movieapp.presentation.home.MovieAdapter
import com.patrick.movieapp.utils.Resource
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import androidx.navigation.fragment.findNavController

class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var castAdapter: CastAdapter
    private lateinit var similarAdapter: MovieAdapter

    private var movieId: Int = 0
    private var youTubePlayer: YouTubePlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener movieId del argumento
        movieId = arguments?.getInt("movieId") ?: 0

        if (movieId == 0) {
            Toast.makeText(context, "Error: ID de película inválido", Toast.LENGTH_SHORT).show()
            return
        }

        setupViewModel()
        setupRecyclerViews()
        setupObservers()
        loadData()
    }

    private fun setupViewModel() {
        val repository = MovieDetailsRepository()
        val factory = MovieDetailsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MovieDetailsViewModel::class.java]
    }

    private fun setupRecyclerViews() {
        // Cast
        castAdapter = CastAdapter()
        binding.rvCast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = castAdapter
        }

        // Similar movies
        similarAdapter = MovieAdapter { movie ->
            findNavController().navigate(
                R.id.action_movieDetailsFragment_self,
                Bundle().apply { putInt("movieId", movie.id) }
            )
        }

        binding.rvSimilar.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = similarAdapter
        }
    }

    private fun setupObservers() {
        // Movie Details
        viewModel.movieDetails.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
                    resource.data?.let { displayMovieDetails(it) }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Videos (Trailer)
        viewModel.movieVideos.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { videos ->
                        val trailer = videos.find { it.type == "Trailer" && it.site == "YouTube" }
                        trailer?.let { video ->
                            val thumbnailUrl = "https://img.youtube.com/vi/${video.key}/hqdefault.jpg"

                            binding.trailerContainer.visibility = View.VISIBLE
                            Glide.with(this)
                                .load(thumbnailUrl)
                                .placeholder(R.drawable.ic_movie_placeholder)
                                .into(binding.ivTrailerThumbnail)

                            binding.trailerContainer.setOnClickListener {
                                openYouTubeVideo(video.key)
                            }
                        } ?: run {
                            binding.trailerContainer.visibility = View.GONE
                        }
                    }
                }
                else -> binding.trailerContainer.visibility = View.GONE
            }
        }

        // Cast
        viewModel.movieCast.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { castAdapter.submitList(it) }
            }
        }

        // Similar Movies
        viewModel.similarMovies.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { similarAdapter.submitList(it) }
            }
        }
    }

    private fun openYouTubeVideo(videoKey: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoKey"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoKey"))
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }

    private fun loadData() {
        viewModel.loadMovieDetails(movieId)
        viewModel.loadMovieVideos(movieId)
        viewModel.loadMovieCast(movieId)
        viewModel.loadSimilarMovies(movieId)
    }

    private fun displayMovieDetails(movie: com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovieDetails) {
        binding.apply {
            tvMovieTitle.text = movie.title
            tvMovieOverview.text = movie.overview
            tvMovieRating.text = String.format("⭐ %.1f/10", movie.voteAverage)
            tvMovieReleaseDate.text = movie.releaseDate
            tvMovieRuntime.text = "${movie.runtime ?: 0} min"

            val genres = movie.genres.joinToString(", ") { it.name }
            tvMovieGenres.text = genres

            val backdropUrl = movie.backdropPath?.let {
                "${BuildConfig.TMDB_IMAGE_URL}w780$it"
            }

            Glide.with(ivBackdrop)
                .load(backdropUrl)
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(ivBackdrop)

            val posterUrl = movie.posterPath?.let {
                "${BuildConfig.TMDB_IMAGE_URL}w500$it"
            }

            Glide.with(ivPoster)
                .load(posterUrl)
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(ivPoster)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
