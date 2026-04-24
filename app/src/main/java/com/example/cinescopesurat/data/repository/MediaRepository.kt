package com.example.cinescopesurat.data.repository

import com.example.cinescopesurat.data.model.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor() {
    
    private val sampleMovies = listOf(
        MediaItem(1, "The Dark Knight", "9.0"),
        MediaItem(2, "Inception", "8.8"),
        MediaItem(3, "Interstellar", "8.7"),
        MediaItem(4, "The Prestige", "8.5"),
        MediaItem(5, "Dunkirk", "7.8")
    )

    fun getTrendingMovies(): Flow<List<MediaItem>> = flow {
        // Simulating network delay
        emit(sampleMovies)
    }
}
