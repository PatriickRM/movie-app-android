package com.patrick.movieapp.presentation.customlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patrick.movieapp.R
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.repository.CustomListRepository
import com.patrick.movieapp.databinding.FragmentCustomListsBinding
import com.patrick.movieapp.utils.Resource

class CustomListsFragment : Fragment() {
    private var _binding: FragmentCustomListsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CustomListViewModel
    private lateinit var listsAdapter: CustomListsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomListsBinding.inflate(inflater, container, false)
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
        val repository = CustomListRepository(tokenManager)
        val factory = CustomListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CustomListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        listsAdapter = CustomListsAdapter(
            onListClick = { list ->
                findNavController().navigate(
                    R.id.action_customLists_to_listDetails,
                    Bundle().apply { putLong("listId", list.id) }
                )
            },
            onDeleteClick = { list ->
                showDeleteConfirmation(list.id, list.name)
            }
        )

        binding.rvLists.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listsAdapter
        }
    }

    private fun setupObservers() {
        // Listas del usuario
        viewModel.userLists.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyState.visibility = View.GONE
                    binding.rvLists.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE

                    resource.data?.let { page ->
                        if (page.content.isEmpty()) {
                            binding.emptyState.visibility = View.VISIBLE
                            binding.rvLists.visibility = View.GONE
                        } else {
                            binding.emptyState.visibility = View.GONE
                            binding.rvLists.visibility = View.VISIBLE
                            listsAdapter.submitList(page.content)

                            binding.tvListCount.text = "${page.totalElements}"
                        }
                    }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyState.visibility = View.GONE

                    // Si es error de Premium, mostrar banner
                    if (resource.message?.contains("Premium") == true) {
                        binding.cardPremiumBanner.visibility = View.VISIBLE
                    }

                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Resultado de crear lista
        viewModel.createListResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "✅ Lista creada", Toast.LENGTH_SHORT).show()
                    viewModel.resetCreateListResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                    viewModel.resetCreateListResult()
                }
                else -> {}
            }
        }

        // Resultado de eliminar lista
        viewModel.deleteListResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "🗑️ Lista eliminada", Toast.LENGTH_SHORT).show()
                    viewModel.resetDeleteListResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetDeleteListResult()
                }
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        // Botón crear lista
        binding.fabCreateList.setOnClickListener {
            showCreateListDialog()
        }

        // Pull to refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadUserLists()
            binding.swipeRefresh.isRefreshing = false
        }

        // Botón de upgrade a Premium
        binding.btnUpgradePremium.setOnClickListener {
            Toast.makeText(
                context,
                "🎉 Funcionalidad Premium próximamente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showCreateListDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_list, null)
        val etName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etListName)
        val etDescription = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etListDescription)
        val switchPublic = dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switchPublic)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("📝 Nueva Lista")
            .setView(dialogView)
            .setPositiveButton("Crear") { dialog, _ ->
                val name = etName.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val isPublic = switchPublic.isChecked

                if (name.isNotEmpty()) {
                    viewModel.createList(
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

    private fun showDeleteConfirmation(listId: Long, listName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("⚠️ Eliminar lista")
            .setMessage("¿Estás seguro de eliminar \"$listName\"?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                viewModel.deleteList(listId)
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