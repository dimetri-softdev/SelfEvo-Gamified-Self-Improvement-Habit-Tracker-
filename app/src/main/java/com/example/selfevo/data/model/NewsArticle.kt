package com.example.selfevo.data.model

data class NewsArticle(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val imageUrl: String? = null,
    val date: String,
    val isFeatured: Boolean = false
)
