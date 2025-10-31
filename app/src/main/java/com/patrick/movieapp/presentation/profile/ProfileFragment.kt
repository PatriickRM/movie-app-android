package com.patrick.movieapp.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.patrick.movieapp.R
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.repository.AuthRepository
import com.patrick.movieapp.data.repository.UserRepository
import com.patrick.movieapp.databinding.FragmentProfileBinding
import com.patrick.movieapp.utils.Resource

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(requireContext())
        val userRepo = UserRepository(tokenManager)
        val authRepo = AuthRepository(tokenManager)
        val factory = ProfileViewModelFactory(userRepo, authRepo)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun setupObservers() {
        // Usuario
        viewModel.user.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.let { user ->
                        binding.tvUserName.text = user.fullName ?: "Usuario"
                        binding.tvUserEmail.text = user.email

                        // Plan badge
                        when (user.plan) {
                            "PREMIUM" -> {
                                binding.tvUserPlan.text = "⭐ PREMIUM"
                                binding.tvUserPlan.setBackgroundResource(R.drawable.bg_icon_circle)
                                binding.cardPremiumBanner.visibility = View.GONE
                            }
                            else -> {
                                binding.tvUserPlan.text = "FREE"
                                binding.cardPremiumBanner.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Estadísticas
        viewModel.stats.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                resource.data?.let { stats ->
                    binding.tvFavoriteCount.text = stats.totalFavorites.toString()
                    binding.tvRatingCount.text = stats.totalRatings.toString()
                    binding.tvListCount.text = stats.totalLists.toString()
                    binding.tvAverageRating.text = String.format("%.1f", stats.averageRating)
                }
            }
        }

        // Resultado actualizar perfil
        viewModel.updateResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "✅ Perfil actualizado", Toast.LENGTH_SHORT).show()
                    viewModel.resetUpdateResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetUpdateResult()
                }
                else -> {}
            }
        }

        // Resultado cambiar contraseña
        viewModel.passwordResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(context, "✅ Contraseña cambiada", Toast.LENGTH_SHORT).show()
                    viewModel.resetPasswordResult()
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                    viewModel.resetPasswordResult()
                }
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.btnMyRatings.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_ratings)
        }

        binding.btnUpgradePremium.setOnClickListener {
            Toast.makeText(context, "🎉 Premium próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etFullName = dialogView.findViewById<TextInputEditText>(R.id.etFullName)

        val currentName = viewModel.user.value?.data?.fullName
        etFullName.setText(currentName)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("✏️ Editar Perfil")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = etFullName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    viewModel.updateProfile(newName)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🔒 Cambiar Contraseña")
            .setView(dialogView)
            .setPositiveButton("Cambiar") { _, _ ->
                val currentPass = etCurrentPassword.text.toString()
                val newPass = etNewPassword.text.toString()
                val confirmPass = etConfirmPassword.text.toString()

                when {
                    currentPass.isEmpty() -> {
                        Toast.makeText(context, "Ingresa la contraseña actual", Toast.LENGTH_SHORT).show()
                    }
                    newPass.length < 8 -> {
                        Toast.makeText(context, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                    }
                    newPass != confirmPass -> {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        viewModel.changePassword(currentPass, newPass)
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🚪 Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí, cerrar sesión") { _, _ ->
                viewModel.logout()
                findNavController().navigate(R.id.action_profile_to_login)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}