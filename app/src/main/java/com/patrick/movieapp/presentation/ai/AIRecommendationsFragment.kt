package com.patrick.movieapp.presentation.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patrick.movieapp.R
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.dto.ai.response.AILimitResponse
import com.patrick.movieapp.data.repository.AIRecommendationRepository
import com.patrick.movieapp.databinding.FragmentAiRecommendationsBinding
import com.patrick.movieapp.utils.Resource

class AIRecommendationsFragment : Fragment() {

    private var _binding: FragmentAiRecommendationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AIRecommendationsViewModel
    private lateinit var recommendationsAdapter: AIRecommendationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        checkAILimit()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(requireContext())
        val repository = AIRecommendationRepository(tokenManager)
        val factory = AIRecommendationsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AIRecommendationsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        recommendationsAdapter = AIRecommendationsAdapter { movieId ->
            findNavController().navigate(
                R.id.action_ai_to_details,
                Bundle().apply { putInt("movieId", movieId) }
            )
        }

        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendationsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.aiLimit.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { limit ->
                        updateLimitUI(limit)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        viewModel.recommendations.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnGetRecommendations.isEnabled = false
                    binding.etPrompt.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnGetRecommendations.isEnabled = true
                    binding.etPrompt.isEnabled = true

                    resource.data?.let { response ->
                        Log.d("AIFragment", "Received ${response.recommendations.size} recommendations")
                        response.recommendations.forEachIndexed { index, rec ->
                            Log.d("AIFragment", "Recommendation $index: ID=${rec.movieId}, Title=${rec.title}")
                        }

                        binding.emptyState.visibility = View.GONE
                        binding.resultsContainer.visibility = View.VISIBLE

                        binding.tvExplanation.text = response.explanation
                        recommendationsAdapter.submitList(response.recommendations)

                        response.requestsRemainingToday?.let { remaining ->
                            binding.tvRequestsRemaining.text = "Requests restantes hoy: $remaining"
                        }

                        // Scroll al resultado
                        binding.scrollView.post {
                            binding.scrollView.smoothScrollTo(0, binding.resultsContainer.top)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnGetRecommendations.isEnabled = true
                    binding.etPrompt.isEnabled = true

                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnGetRecommendations.setOnClickListener {
            val prompt = binding.etPrompt.text.toString().trim()

            if (prompt.isEmpty()) {
                binding.tilPrompt.error = "Escribe qué tipo de películas te gustan"
                return@setOnClickListener
            }

            binding.tilPrompt.error = null
            viewModel.getRecommendations(prompt, binding.switchIncludeHistory.isChecked)
        }

        // Sugerencias rápidas
        binding.chipAction.setOnClickListener {
            addPromptSuggestion("películas de acción con mucha adrenalina")
        }

        binding.chipComedy.setOnClickListener {
            addPromptSuggestion("comedias divertidas y ligeras")
        }

        binding.chipDrama.setOnClickListener {
            addPromptSuggestion("dramas emotivos y profundos")
        }

        binding.chipSciFi.setOnClickListener {
            addPromptSuggestion("ciencia ficción futurista")
        }

        binding.chipHorror.setOnClickListener {
            addPromptSuggestion("terror psicológico")
        }

        binding.btnUpgradePremium.setOnClickListener {
            Toast.makeText(context, "🎉 Premium próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPromptSuggestion(suggestion: String) {
        val current = binding.etPrompt.text.toString()
        if (current.isEmpty()) {
            binding.etPrompt.setText("Recomiéndame $suggestion")
        }
    }

    private fun checkAILimit() {
        viewModel.checkAILimit()
    }

    private fun updateLimitUI(limit: AILimitResponse) {
        if (limit.isPremium) {
            binding.cardPremiumBanner.visibility = View.GONE
            binding.tvRequestsRemaining.visibility = View.GONE
            binding.tvRequestsRemaining.text = "✨ Requests ilimitados (Premium)"
        } else {
            binding.tvRequestsRemaining.visibility = View.VISIBLE
            binding.tvRequestsRemaining.text = "Requests restantes hoy: ${limit.requestsRemainingToday}/1"

            if (!limit.canRequest) {
                binding.cardPremiumBanner.visibility = View.VISIBLE
                binding.btnGetRecommendations.isEnabled = false
                binding.btnGetRecommendations.text = "Límite alcanzado"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}