package com.bangkit.submissionstoryapps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.submissionstoryapps.databinding.ItemStoryBinding
import com.bangkit.submissionstoryapps.model.response.ListStory
import com.bumptech.glide.Glide

class AdapterStory (private var onItemClickCallback: OnItemClickCallback) :
    PagingDataAdapter<ListStory, AdapterStory.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    inner class ListViewHolder (private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(story: ListStory){
            binding.apply {
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(ivUser)
                tvUsername.text = story.name
                tvDescription.text =story.description

            }
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(story)
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: ListStory)

    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}