package com.patrick.movieapp.presentation.customlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patrick.movieapp.R
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.repository.CustomListRepository
import com.patrick.movieapp.databinding.FragmentListDetailsBinding
import com.patrick.movieapp.utils.Resource

class ListDetailsFragment : Fragment() {
    private var _binding: FragmentListDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CustomListViewModel
    private lateinit var moviesAdapter: ListMoviesAdapter
    private var listId: Long = 0L
    private var listName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listId = arguments?.getLong("listId") ?: 0L

        if (listId == 0L) {
            Toast.makeText(context, "Error: ID de lista inválido", Toast.LENGTH_SHORT).show()
            return
        }

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        loadData()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(requireContext())
        val repository = CustomListRepository(tokenManager)
        val factory = CustomListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CustomListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        moviesAdapter = ListMoviesAdapter(
            onMovieClick = { movie ->
                findNavController().navigate(
                    R.id.action_listDetails_to_movieDetails,
                    Bundle().apply { putInt("movieId", movie.movieId) }
                )
            },
            onRemoveClick = { movie ->
                showRemoveConfirmation(movie.movieId, movie.movieTitle ?: "esta película")
            }
        )

        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = moviesAdapter
        }
    }

    private fun setupObservers() {
        // Detalles de la lista
        viewModel.listDetails.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    resource.data?.let { details ->
                        listName = details.name
                        binding.tvListName.text = details.name
                        binding.tvListDescription.text = details.description ?: "Sin descripción"
                        binding.tvMovieCount.text = "${details.movies.size} películas"

                        if (details.isPublic) {
                            binding.tvVisibility.text = "🌐 Pública"
                        } else {
                            binding.tvVisibility.text = "🔒 Privada"
                        }

                        if (details.movies.isEmpty()) {
                            binding.emptyState.visibility = View.VISIBLE
                            binding.rvMovies.visibility = View.GONE
                        } else {
                            binding.emptyState.visibility = View.GONE
                            binding.rvMovies.visibility = View.VISIBLE
                            moviesAdapter.submitList(details.movies)
                        }
                    }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Resultado de eliminar película
        viewModel.removeMovieResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "🗑️ Película eliminada", Toast.LENGTH_SHORT).show()
                    viewModel.resetRemoveMovieResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetRemoveMovieResult()
                }
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        // Botón de editar
        binding.btnEdit.setOnClickListener {
            showEditListDialog()
        }

        // Pull to refresh
        binding.swipeRefresh.setOnRefreshListener {
            loadData()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun loadData() {
        viewModel.loadListDetails(listId)
    }

    private fun showEditListDialog() {
        val currentDetails = viewModel.listDetails.value?.data ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_create_list, null)
        val etName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etListName)
        val etDescription = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etListDescription)
        val switchPublic = dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switchPublic)

        // Pre-rellenar con valores actuales
        etName.setText(currentDetails.name)
        etDescription.setText(currentDetails.description)
        switchPublic.isChecked = currentDetails.isPublic

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("✏️ Editar Lista")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val name = etName.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val isPublic = switchPublic.isChecked

                if (name.isNotEmpty()) {
                    viewModel.updateList(
                        listId,
                        name,
                        description.ifEmpty { null },
                        isPublic
                    )
                } else {
                    Toast.makeText(context, "El nombre es requerido", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showRemoveConfirmation(movieId: Int, movieTitle: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("⚠️ Eliminar película")
            .setMessage("¿Eliminar \"$movieTitle\" de esta lista?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                viewModel.removeMovieFromList(listId, movieId)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}