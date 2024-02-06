package com.example.blogapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.ItemsBlogModel
import com.example.blogapp.databinding.ArticleItemBinding

class ArticleAdapter(
    private  val context: Context,
    private var blogList: List<ItemsBlogModel>,
    private val itemClick: OnItemClickListener
) : RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(private val binding: ArticleItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItem: ItemsBlogModel) {
            binding.heading.text= blogItem.heading
            Glide.with(binding.profile.context)
                .load(blogItem.profileImage)
                .into(binding.profile)
            binding.username.text=blogItem.userName
            binding.date.text=blogItem.date
            binding.post.text= blogItem.post

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleAdapter.BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleItemBinding.inflate(inflater,parent,false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleAdapter.BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

   
}