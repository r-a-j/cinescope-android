package com.example.cinescopesurat.data.model

import com.example.cinescopesurat.R

data class Person(
    val id: Int,
    val name: String,
    val role: String, // e.g., "Actor", "Director"
    val imageRes: Int = R.drawable.placeholder,
    val knownFor: String
)
