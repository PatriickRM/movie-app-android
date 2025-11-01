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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.R
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovieDetails
import com.patrick.movieapp.data.repository.FavoriteRepository
import com.patrick.movieapp.data.repository.MovieDetailsRepository
import com.patrick.movieapp.data.repository.RatingRepository
import com.patrick.movieapp.databinding.FragmentMovieDetailsBinding
import com.patrick.movieapp.presentation.favorites.FavoriteViewModel
import com.patrick.movieapp.presentation.favorites.FavoriteViewModelFactory
import com.patrick.movieapp.presentation.home.MovieAdapter
import com.patrick.movieapp.presentation.ratings.RatingViewModel
import com.patrick.movieapp.presentation.ratings.RatingViewModelFactory
import com.patrick.movieapp.utils.Resource

<<<<<<< HEAD
import com.patrick.movieapp.presentation.customlists.CustomListViewModel
import com.patrick.movieapp.presentation.customlists.CustomListViewModelFactory
import com.patrick.movieapp.data.repository.CustomListRepository

import com.google.android.material.snackbar.Snackbar

=======
>>>>>>> 3a1acd080f1d287b3d972cd5856e8e333784d48d
class MovieDetailsFragment : Fragment() {
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var ratingViewModel: RatingViewModel
    private lateinit var castAdapter: CastAdapter
    private lateinit var similarAdapter: MovieAdapter

    private var movieId: Int = 0
    private var currentMovie: TMDbMovieDetails? = null
    private var isFavorite: Boolean = false
    private var currentRating: Double? = null
<<<<<<< HEAD
    private lateinit var customListViewModel: CustomListViewModel
    private var lastSelectedListName: String? = null


=======
>>>>>>> 3a1acd080f1d287b3d972cd5856e8e333784d48d
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
        val tokenManager = TokenManager(requireContext())
        // Movie Details ViewModel
        val detailsRepository = MovieDetailsRepository()
        val detailsFactory = MovieDetailsViewModelFactory(detailsRepository)
        viewModel = ViewModelProvider(this, detailsFactory)[MovieDetailsViewModel::class.java]

        // Favorite ViewModel
        val favoriteRepository = FavoriteRepository(tokenManager)
        val favoriteFactory = FavoriteViewModelFactory(favoriteRepository)
        favoriteViewModel = ViewModelProvider(this, favoriteFactory)[FavoriteViewModel::class.java]

        // Rating ViewModel
        val ratingRepository = RatingRepository(tokenManager)
        val ratingFactory = RatingViewModelFactory(ratingRepository)
        ratingViewModel = ViewModelProvider(this, ratingFactory)[RatingViewModel::class.java]
<<<<<<< HEAD

        // Custom List ViewModel
        val listRepository = CustomListRepository(tokenManager)
        val listFactory = CustomListViewModelFactory(listRepository)
        customListViewModel = ViewModelProvider(this, listFactory)[CustomListViewModel::class.java]
        customListViewModel.loadUserLists()

=======
>>>>>>> 3a1acd080f1d287b3d972cd5856e8e333784d48d
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

        // Videos
        viewModel.movieVideos.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { videos ->
                        val trailer = videos.find { it.type == "Trailer" && it.site == "YouTube" }
                        trailer?.let { video ->
                            val thumbnailUrl = "https://img.youtube.com/vi/${video.key}/hqdefault.jpg"
                            binding.trailerContainer.visibility = View.VISIBLE
                            Glide.with(this).load(thumbnailUrl).into(binding.ivTrailerThumbnail)
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

        // Cast & Similar
        viewModel.movieCast.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { castAdapter.submitList(it) }
            }
        }

        viewModel.similarMovies.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { similarAdapter.submitList(it) }
            }
        }

        // Favorito
        favoriteViewModel.isFavorite.observe(viewLifecycleOwner) { favorite ->
            isFavorite = favorite
            updateFavoriteButton()
        }

        favoriteViewModel.addFavoriteResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "❤️ Agregado a favoritos", Toast.LENGTH_SHORT).show()
                    favoriteViewModel.resetAddFavoriteResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                    favoriteViewModel.resetAddFavoriteResult()
                }
                else -> {}
            }
        }

        favoriteViewModel.removeFavoriteResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "💔 Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    favoriteViewModel.resetRemoveFavoriteResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    favoriteViewModel.resetRemoveFavoriteResult()
                }
                else -> {}
            }
        }

        // Rating
        ratingViewModel.movieRating.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { rating ->
                        currentRating = rating.rating
                        updateRatingButton(rating.rating)
                    }
                }
                is Resource.Error -> {
                    currentRating = null
                    updateRatingButton(null)
                }
                else -> {}
            }
        }

        ratingViewModel.addRatingResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "⭐ Calificación guardada", Toast.LENGTH_SHORT).show()
                    ratingViewModel.resetAddRatingResult()
                    ratingViewModel.loadMovieRating(movieId)
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    ratingViewModel.resetAddRatingResult()
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
                        movieId, movie.title, movie.posterPath,
                        movie.overview, movie.releaseDate, movie.voteAverage
                    )
                }
            }
        }

        // Botón de calificar
        binding.btnRate.setOnClickListener {
            showRatingDialog()
        }
<<<<<<< HEAD

        // Botón Agregar a Lista
        binding.btnAddToList.setOnClickListener {
            val movie = currentMovie ?: return@setOnClickListener
            val movieTitle = movie.title
            val moviePoster = movie.posterPath

            val listsResource = customListViewModel.userLists.value
            if (listsResource is Resource.Success) {
                val lists = listsResource.data?.content ?: emptyList()

                if (lists.isEmpty()) {
                    Toast.makeText(context, "No tienes listas aún", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val listNames = lists.map { it.name }.toTypedArray()

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("📋 Agregar a lista")
                    .setItems(listNames) { dialog, which ->
                        val selectedList = lists[which]
                        lastSelectedListName = selectedList.name // Guardamos el nombre para usarlo después

                        customListViewModel.addMovieToList(
                            selectedList.id,
                            movie.id,
                            movieTitle,
                            moviePoster
                        )

                        dialog.dismiss()
                    }

                    .setNegativeButton("Cancelar", null)
                    .show()
            } else {
                Toast.makeText(context, "Cargando tus listas...", Toast.LENGTH_SHORT).show()
                customListViewModel.loadUserLists()
            }
        }


        customListViewModel.addMovieResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val addedListName = lastSelectedListName ?: "lista seleccionada"
                    Snackbar.make(
                        binding.root,
                        "✅ Película agregada a $addedListName",
                        Snackbar.LENGTH_LONG
                    ).setAction("Ver lista") {
                        findNavController().navigate(R.id.customListsFragment)
                    }.show()
                    customListViewModel.resetAddMovieResult()
                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root,
                        "❌ ${resource.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                    customListViewModel.resetAddMovieResult()
                }
                else -> {}
            }
        }

=======
>>>>>>> 3a1acd080f1d287b3d972cd5856e8e333784d48d
    }

    private fun showRatingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_rate_movie, null)
        val ratingBar = dialogView.findViewById<android.widget.RatingBar>(R.id.ratingBar)
        val etReview = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etReview)

        // Pre-rellenar si ya calificó
        currentRating?.let { ratingBar.rating = it.toFloat() }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("⭐ Calificar Película")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val rating = ratingBar.rating.toDouble()
                val review = etReview.text.toString().trim()

                if (rating > 0) {
                    ratingViewModel.addOrUpdateRating(
                        movieId,
                        rating,
                        review.ifEmpty { null }
                    )
                } else {
                    Toast.makeText(context, "Selecciona una calificación", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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

    private fun updateRatingButton(rating: Double?) {
        if (rating != null) {
            val stars = "⭐".repeat(rating.toInt())
            binding.btnRate.text = "Tu calificación: $stars"
        } else {
            binding.btnRate.text = "⭐ Calificar"
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

        favoriteViewModel.checkIsFavorite(movieId)
        ratingViewModel.loadMovieRating(movieId)
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

            Glide.with(ivBackdrop).load("${BuildConfig.TMDB_IMAGE_URL}w780${movie.backdropPath}").into(ivBackdrop)
            Glide.with(ivPoster).load("${BuildConfig.TMDB_IMAGE_URL}w500${movie.posterPath}").into(ivPoster)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}