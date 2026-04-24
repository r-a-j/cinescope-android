package com.example.cinescopesurat.data.model

import com.example.cinescopesurat.R

data class MediaItem(
    val id: Int,
    val title: String,
    val rating: String,
    val posterRes: Int = R.drawable.placeholder,
    val backdropRes: Int = R.drawable.placeholder_backdrop,
    val type: String = "Movie"
)
