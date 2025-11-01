package com.patrick.movieapp.presentation.ratings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.R
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.repository.RatingRepository
import com.patrick.movieapp.databinding.FragmentRatingsBinding
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.launch

class RatingsFragment : Fragment() {

    private var _binding: FragmentRatingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RatingViewModel
    private lateinit var ratingsAdapter: RatingsAdapter

    private val movieTitles = mutableMapOf<Int, String>()
    private val moviePosters = mutableMapOf<Int, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(requireContext())
        val repository = RatingRepository(tokenManager)
        val factory = RatingViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RatingViewModel::class.java]
    }

    private fun setupRecyclerView() {
        ratingsAdapter = RatingsAdapter(
            onMovieClick = { rating ->
                findNavController().navigate(
                    R.id.action_ratings_to_details,
                    Bundle().apply { putInt("movieId", rating.movieId) }
                )
            },
            onDeleteClick = { rating ->
                viewModel.deleteRating(rating.movieId)
            },
            movieTitles = movieTitles,
            moviePosters = moviePosters
        )

        binding.rvRatings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ratingsAdapter
        }
    }

    private fun setupObservers() {
        // Ratings del usuario
        viewModel.userRatings.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyState.visibility = View.GONE
                    binding.rvRatings.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE

                    resource.data?.let { page ->
                        if (page.content.isEmpty()) {
                            binding.emptyState.visibility = View.VISIBLE
                            binding.rvRatings.visibility = View.GONE
                        } else {
                            binding.emptyState.visibility = View.GONE
                            binding.rvRatings.visibility = View.VISIBLE

                            // Cargar info de películas
                            loadMovieInfo(page.content.map { it.movieId })

                            ratingsAdapter.submitList(page.content)

                            // Calcular estadísticas
                            val total = page.totalElements
                            val average = page.content.map { it.rating }.average()

                            binding.tvRatingCount.text = "$total"
                            binding.tvAverageRating.text = String.format("⭐ %.1f", average)
                        }
                    }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyState.visibility = View.GONE
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Resultado de eliminar
        viewModel.deleteRatingResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "🗑️ Calificación eliminada", Toast.LENGTH_SHORT).show()
                    viewModel.resetDeleteRatingResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetDeleteRatingResult()
                }
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadUserRatings()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun loadMovieInfo(movieIds: List<Int>) {
        lifecycleScope.launch {
            movieIds.forEach { movieId ->
                try {
                    val response = RetrofitInstance.tmdbApi.getMovieDetails(
                        movieId,
                        BuildConfig.TMDB_API_KEY,
                        "es-ES"
                    )
                    if (response.isSuccessful && response.body() != null) {
                        val movie = response.body()!!
                        movieTitles[movieId] = movie.title
                        moviePosters[movieId] = movie.posterPath ?: ""
                    }
                } catch (e: Exception) {
                    // Ignorar errores y continuar
                }
            }
            // Actualizar adapter con la nueva info
            ratingsAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}