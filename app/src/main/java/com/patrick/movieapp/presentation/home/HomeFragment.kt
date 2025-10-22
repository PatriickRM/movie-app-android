package com.patrick.movieapp.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.patrick.movieapp.data.repository.MovieRepository
import com.patrick.movieapp.databinding.FragmentHomeBinding
import com.patrick.movieapp.utils.Resource

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var trendingAdapter: MovieAdapter
    private lateinit var popularAdapter: MovieAdapter
    private lateinit var upcomingAdapter: MovieAdapter
    private lateinit var topRatedAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerViews()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val repository = MovieRepository()
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun setupRecyclerViews() {
        // Trending
        trendingAdapter = MovieAdapter { movie ->
            Toast.makeText(context, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()

        }
        binding.rvTrending.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = trendingAdapter
        }

        // Popular
        popularAdapter = MovieAdapter { movie ->
            Toast.makeText(context, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvPopular.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }

        // Upcoming
        upcomingAdapter = MovieAdapter { movie ->
            Toast.makeText(context, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvUpcoming.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        // Top Rated
        topRatedAdapter = MovieAdapter { movie ->
            Toast.makeText(context, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvTopRated.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = topRatedAdapter
        }
    }

    private fun setupObservers() {
        // Trending
        viewModel.trendingMovies.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressTrending.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressTrending.visibility = View.GONE
                    trendingAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressTrending.visibility = View.GONE
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Popular
        viewModel.popularMovies.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressPopular.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressPopular.visibility = View.GONE
                    popularAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressPopular.visibility = View.GONE
                }
            }
        }

        // Upcoming
        viewModel.upcomingMovies.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressUpcoming.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressUpcoming.visibility = View.GONE
                    upcomingAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressUpcoming.visibility = View.GONE
                }
            }
        }

        // Top Rated
        viewModel.topRatedMovies.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressTopRated.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressTopRated.visibility = View.GONE
                    topRatedAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressTopRated.visibility = View.GONE
                }
            }
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadMovies()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}