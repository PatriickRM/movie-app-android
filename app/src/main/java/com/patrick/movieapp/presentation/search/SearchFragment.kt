package com.patrick.movieapp.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patrick.movieapp.R
import com.patrick.movieapp.data.remote.dto.tmdb.Genre
import com.patrick.movieapp.data.repository.SearchRepository
import com.patrick.movieapp.databinding.FragmentSearchBinding
import com.patrick.movieapp.presentation.home.MovieAdapter
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.*

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchAdapter: MovieAdapter
    private var searchJob: Job? = null
    private var genres: List<Genre> = emptyList()

    private var selectedGenre: Int? = null
    private var selectedYear: Int? = null
    private var selectedMinRating: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
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
        val repository = SearchRepository()
        val factory = SearchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }

    private fun setupRecyclerView() {
        searchAdapter = MovieAdapter { movie ->
            findNavController().navigate(
                R.id.action_search_to_details,
                Bundle().apply { putInt("movieId", movie.id) }
            )
        }

        binding.rvSearchResults.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = searchAdapter
        }
    }

    private fun setupObservers() {
        // Resultados de búsqueda
        viewModel.searchResults.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyState.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE

                    resource.data?.let { movies ->
                        if (movies.isEmpty()) {
                            binding.emptyState.visibility = View.VISIBLE
                            binding.tvEmptyMessage.text = "No se encontraron películas"
                            binding.rvSearchResults.visibility = View.GONE
                        } else {
                            binding.emptyState.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.VISIBLE
                            searchAdapter.submitList(movies)
                        }
                    }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }

                null -> {
                    // Estado inicial
                    binding.progressBar.visibility = View.GONE
                    binding.emptyState.visibility = View.VISIBLE
                    binding.tvEmptyMessage.text = "Busca películas por título o usa filtros"
                    binding.rvSearchResults.visibility = View.GONE
                }
            }
        }

        // Géneros
        viewModel.genres.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let {
                    genres = it
                }
            }
        }
    }

    private fun setupListeners() {
        // Búsqueda con debounce
        binding.etSearch.addTextChangedListener { text ->
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(500) // Esperar 500ms después de que el usuario deje de escribir
                val query = text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchMovies(query)
                } else {
                    viewModel.clearResults()
                }
            }
        }

        // Limpiar búsqueda
        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.clearResults()
        }

        // Filtros
        binding.chipGenre.setOnClickListener {
            showGenreDialog()
        }

        binding.chipYear.setOnClickListener {
            showYearDialog()
        }

        binding.chipRating.setOnClickListener {
            showRatingDialog()
        }

        // Aplicar filtros
        binding.btnApplyFilters.setOnClickListener {
            applyFilters()
        }

        // Limpiar filtros
        binding.btnClearFilters.setOnClickListener {
            clearFilters()
        }
    }

    private fun showGenreDialog() {
        if (genres.isEmpty()) {
            Toast.makeText(context, "Cargando géneros...", Toast.LENGTH_SHORT).show()
            return
        }

        val genreNames = genres.map { it.name }.toTypedArray()
        var selectedIndex = genres.indexOfFirst { it.id == selectedGenre }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona Género")
            .setSingleChoiceItems(genreNames, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Seleccionar") { dialog, _ ->
                selectedGenre = genres[selectedIndex].id
                binding.chipGenre.text = genres[selectedIndex].name
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showYearDialog() {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val years = (currentYear downTo 1900).map { it.toString() }.toTypedArray()
        var selectedIndex = selectedYear?.let { years.indexOf(it.toString()) } ?: -1

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecciona Año")
            .setSingleChoiceItems(years, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Seleccionar") { dialog, _ ->
                selectedYear = years[selectedIndex].toInt()
                binding.chipYear.text = years[selectedIndex]
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showRatingDialog() {
        val ratings = arrayOf("9+", "8+", "7+", "6+", "5+")
        val ratingValues = arrayOf(9.0, 8.0, 7.0, 6.0, 5.0)
        var selectedIndex = ratingValues.indexOfFirst { it == selectedMinRating }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Calificación Mínima")
            .setSingleChoiceItems(ratings, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Seleccionar") { dialog, _ ->
                selectedMinRating = ratingValues[selectedIndex]
                binding.chipRating.text = "⭐ ${ratings[selectedIndex]}"
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun applyFilters() {
        if (selectedGenre == null && selectedYear == null && selectedMinRating == null) {
            Toast.makeText(context, "Selecciona al menos un filtro", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.discoverMovies(selectedGenre, selectedYear, selectedMinRating)
    }

    private fun clearFilters() {
        selectedGenre = null
        selectedYear = null
        selectedMinRating = null

        binding.chipGenre.text = "Género"
        binding.chipYear.text = "Año"
        binding.chipRating.text = "Rating"

        viewModel.clearResults()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        _binding = null
    }
}