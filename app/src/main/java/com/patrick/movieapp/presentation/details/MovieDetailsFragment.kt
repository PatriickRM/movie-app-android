package com.patrick.movieapp.presentation.details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.R
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovieDetails
import com.patrick.movieapp.data.repository.FavoriteRepository
import com.patrick.movieapp.data.repository.MovieDetailsRepository
import com.patrick.movieapp.databinding.FragmentMovieDetailsBinding
import com.patrick.movieapp.presentation.favorites.FavoriteViewModel
import com.patrick.movieapp.presentation.favorites.FavoriteViewModelFactory
import com.patrick.movieapp.presentation.home.MovieAdapter
import com.patrick.movieapp.utils.Resource

class MovieDetailsFragment : Fragment() {
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var castAdapter: CastAdapter
    private lateinit var similarAdapter: MovieAdapter

    private var movieId: Int = 0
    private var currentMovie: TMDbMovieDetails? = null
    private var isFavorite: Boolean = false

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

        movieId = arguments?.getInt("movieId") ?: 0

        if (movieId == 0) {
            Toast.makeText(context, "Error: ID de película inválido", Toast.LENGTH_SHORT).show()
            return
        }

        setupViewModels()
        setupRecyclerViews()
        setupObservers()
        setupListeners()
        loadData()
    }

    private fun setupViewModels() {
        // Movie Details ViewModel
        val detailsRepository = MovieDetailsRepository()
        val detailsFactory = MovieDetailsViewModelFactory(detailsRepository)
        viewModel = ViewModelProvider(this, detailsFactory)[MovieDetailsViewModel::class.java]

        // Favorite ViewModel
        val tokenManager = TokenManager(requireContext())
        val favoriteRepository = FavoriteRepository(tokenManager)
        val favoriteFactory = FavoriteViewModelFactory(favoriteRepository)
        favoriteViewModel = ViewModelProvider(this, favoriteFactory)[FavoriteViewModel::class.java]
    }

    private fun setupRecyclerViews() {
        castAdapter = CastAdapter()
        binding.rvCast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = castAdapter
        }

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
                    resource.data?.let {
                        currentMovie = it
                        displayMovieDetails(it)
                    }
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

        // Estado de favorito
        favoriteViewModel.isFavorite.observe(viewLifecycleOwner) { favorite ->
            isFavorite = favorite
            updateFavoriteButton()
        }

        // Resultado de agregar favorito
        favoriteViewModel.addFavoriteResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnFavorite.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnFavorite.isEnabled = true
                    Toast.makeText(context, "❤️ Agregado a favoritos", Toast.LENGTH_SHORT).show()
                    favoriteViewModel.resetAddFavoriteResult()
                }
                is Resource.Error -> {
                    binding.btnFavorite.isEnabled = true
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                    favoriteViewModel.resetAddFavoriteResult()
                }
                else -> {}
            }
        }

        // Resultado de eliminar favorito
        favoriteViewModel.removeFavoriteResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnFavorite.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnFavorite.isEnabled = true
                    Toast.makeText(context, "💔 Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    favoriteViewModel.resetRemoveFavoriteResult()
                }
                is Resource.Error -> {
                    binding.btnFavorite.isEnabled = true
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    favoriteViewModel.resetRemoveFavoriteResult()
                }
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        // Botón de favorito
        binding.btnFavorite.setOnClickListener {
            currentMovie?.let { movie ->
                if (isFavorite) {
                    favoriteViewModel.removeFavorite(movieId)
                } else {
                    favoriteViewModel.addFavorite(
                        movieId = movie.id,
                        movieTitle = movie.title,
                        moviePoster = movie.posterPath,
                        movieOverview = movie.overview,
                        releaseDate = movie.releaseDate,
                        voteAverage = movie.voteAverage
                    )
                }
            }
        }
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            binding.btnFavorite.setIconResource(R.drawable.ic_favorite)
            binding.btnFavorite.text = "En Favoritos"
            binding.btnFavorite.setIconTintResource(R.color.error)
        } else {
            binding.btnFavorite.setIconResource(R.drawable.ic_favorite_border)
            binding.btnFavorite.text = "Agregar a Favoritos"
            binding.btnFavorite.setIconTintResource(R.color.text_secondary)
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

        // Verificar si es favorito
        favoriteViewModel.checkIsFavorite(movieId)
    }

    private fun displayMovieDetails(movie: TMDbMovieDetails) {
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