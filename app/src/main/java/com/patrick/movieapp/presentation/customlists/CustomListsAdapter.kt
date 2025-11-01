package com.patrick.movieapp.presentation.customlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.patrick.movieapp.data.remote.dto.CustomListResponse
import com.patrick.movieapp.databinding.ItemCustomListBinding

class CustomListsAdapter(
    private val onListClick: (CustomListResponse) -> Unit,
    private val onDeleteClick: (CustomListResponse) -> Unit
) : ListAdapter<CustomListResponse, CustomListsAdapter.ListViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemCustomListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding, onListClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ListViewHolder(
        private val binding: ItemCustomListBinding,
        private val onListClick: (CustomListResponse) -> Unit,
        private val onDeleteClick: (CustomListResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(list: CustomListResponse) {
            binding.apply {
                tvListName.text = list.name
                tvListDescription.text = list.description ?: "Sin descripción"
                tvMovieCount.text = "${list.movieCount} películas"

                // Mostrar icono de público/privado
                if (list.isPublic) {
                    tvVisibility.text = "🌐 Pública"
                } else {
                    tvVisibility.text = "🔒 Privada"
                }

                // Click en la card
                root.setOnClickListener {
                    onListClick(list)
                }

                // Click en el menú de opciones
                btnOptions.setOnClickListener {
                    onDeleteClick(list)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CustomListResponse>() {
        override fun areItemsTheSame(oldItem: CustomListResponse, newItem: CustomListResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CustomListResponse, newItem: CustomListResponse): Boolean {
            return oldItem == newItem
        }
    }
}