package com.patrick.movieapp.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.patrick.movieapp.R
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.repository.FavoriteRepository
import com.patrick.movieapp.databinding.FragmentFavoritesBinding
import com.patrick.movieapp.utils.Resource

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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
        val repository = FavoriteRepository(tokenManager)
        val factory = FavoriteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter(
            onMovieClick = { favorite ->
                findNavController().navigate(
                    R.id.action_favorites_to_details,
                    Bundle().apply { putInt("movieId", favorite.movieId) }
                )
            },
            onRemoveClick = { favorite ->
                viewModel.removeFavorite(favorite.movieId)
            }
        )

        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = favoritesAdapter
        }
    }

    private fun setupObservers() {
        // Favoritos
        viewModel.favorites.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyState.visibility = View.GONE
                    binding.rvFavorites.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE

                    resource.data?.let { page ->
                        if (page.content.isEmpty()) {
                            binding.emptyState.visibility = View.VISIBLE
                            binding.rvFavorites.visibility = View.GONE
                        } else {
                            binding.emptyState.visibility = View.GONE
                            binding.rvFavorites.visibility = View.VISIBLE
                            favoritesAdapter.submitList(page.content)
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

        // Stats de favoritos
        viewModel.favoriteStats.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { stats ->
                    updateStatsCard(stats.totalFavorites, stats.maxFavorites, stats.isPremium)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFavorites()
            viewModel.loadFavoriteStats()
            binding.swipeRefresh.isRefreshing = false
        }

        // Botón de upgrade a Premium (si es FREE)
        binding.btnUpgradePremium.setOnClickListener {
            Toast.makeText(
                context,
                "🎉 Funcionalidad Premium próximamente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateStatsCard(total: Int, max: Int, isPremium: Boolean) {
        binding.apply {
            tvFavoriteCount.text = "$total"

            if (isPremium) {
                tvFavoriteLimit.visibility = View.GONE
                cardPremiumBanner.visibility = View.GONE
            } else {
                tvFavoriteLimit.visibility = View.VISIBLE
                tvFavoriteLimit.text = "Límite: $total/$max"

                // Mostrar banner si está cerca del límite
                if (total >= max - 1) {
                    cardPremiumBanner.visibility = View.VISIBLE
                } else {
                    cardPremiumBanner.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}