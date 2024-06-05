package com.example.storyappfix.view.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyappfix.R
import com.example.storyappfix.entity.StoryEntity
import com.example.storyappfix.databinding.StoryItemBinding
import com.example.storyappfix.view.detail.DetailStoryActivity
import com.example.storyappfix.utils.CameraUtils.getTimeUpload

class MainStoryAdapter : PagingDataAdapter<StoryEntity, MainStoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        const val EXTRA_STORY = "extra_story"
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result)
    }

    class ViewHolder(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(result: StoryEntity?) {
            with(binding) {
                result?.apply {
                    usernameItem.text = name
                    timeItem.text =
                        " ${itemView.context.getString(R.string.text_uploaded)} ${
                            result.createdAt?.let {
                                getTimeUpload(
                                    itemView.context,
                                    it
                                )
                            }
                        }"
                    Glide.with(binding.root)
                        .load(photoUrl)
                        .into(imgItem)

                    itemView.setOnClickListener {
                        val optionsCompat: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                itemView.context as Activity,
                                Pair(imgItem, "image"),
                                Pair(usernameItem, "name")

                            )
                        Intent(itemView.context, DetailStoryActivity::class.java)
                            .apply {
                                putExtra(EXTRA_STORY, result)
                                itemView.context.startActivity(
                                    this,
                                    optionsCompat.toBundle()
                                )
                            }
                    }
                }
            }
        }
    }
}