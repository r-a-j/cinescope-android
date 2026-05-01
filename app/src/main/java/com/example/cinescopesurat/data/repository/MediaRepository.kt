package com.example.cinescopesurat.data.repository

import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.data.model.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor() {

    // Base paths for TMDB Images
    private val basePosterPath = "https://image.tmdb.org/t/p/w500"
    private val baseBackdropPath = "https://image.tmdb.org/t/p/w780"

    // Vault storage (saved/favorited movies)
    private val savedVaultMovies = mutableListOf<MediaItem>()

    private val sampleMovies = listOf(
        MediaItem(1, "The Dark Knight", "9.0", posterUrl = "$basePosterPath/qJ2tW6WMUDux911r6m7haRef0WH.jpg", backdropUrl = "$baseBackdropPath/dqK9Hag1054tghRQSqLSfrkvQnA.jpg", type = "Movie"),
        MediaItem(2, "Inception", "8.8", posterUrl = "$basePosterPath/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg", backdropUrl = "$baseBackdropPath/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg", type = "Movie"),
        MediaItem(3, "Interstellar", "8.7", posterUrl = "$basePosterPath/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg", backdropUrl = "$baseBackdropPath/pbrkL804c8yAv3zBZR4QPEafpAR.jpg", type = "Movie"),
        MediaItem(4, "The Prestige", "8.5", type = "Movie"),
        MediaItem(5, "Dunkirk", "7.8", type = "Movie"),
        MediaItem(6, "The Godfather", "9.2", type = "Movie"),
        MediaItem(7, "Pulp Fiction", "8.9", type = "Movie"),
        MediaItem(8, "Fight Club", "8.8", type = "Movie"),
        MediaItem(9, "The Matrix", "8.7", type = "Movie"),
        MediaItem(10, "Se7en", "8.6", type = "Movie"),
        MediaItem(11, "Gladiator", "8.5", type = "Movie"),
        MediaItem(12, "The Silence of the Lambs", "8.6", type = "Movie"),
        MediaItem(13, "Saving Private Ryan", "8.6", type = "Movie"),
        MediaItem(14, "Schindler's List", "9.0", type = "Movie"),
        MediaItem(15, "The Departed", "8.5", type = "Movie"),
        MediaItem(16, "Goodfellas", "8.7", type = "Movie"),
        MediaItem(17, "Parasite", "8.5", type = "Movie"),
        MediaItem(18, "The Lion King", "8.5", type = "Movie"),
        MediaItem(19, "Spirited Away", "8.6", type = "Movie"),
        MediaItem(20, "Whiplash", "8.5", type = "Movie")
    )

    private val sampleTvShows = listOf(
        MediaItem(101, "Breaking Bad", "9.5", posterUrl = "$basePosterPath/ggFHVNu6YYI5L9pCfOacjizRGt.jpg", backdropUrl = "$baseBackdropPath/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg", type = "TV Show"),
        MediaItem(102, "Better Call Saul", "9.0", posterUrl = "$basePosterPath/fC2HDm5t0kHlAMOINtDpWdipEAY.jpg", backdropUrl = "$baseBackdropPath/hPea3Qy5Gd6z4kJLUruBbwAH8Rm.jpg", type = "TV Show"),
        MediaItem(103, "The Last of Us", "8.8", posterUrl = "$basePosterPath/uKvVjHNqB5VmOrdxqAt2F7J78ED.jpg", backdropUrl = "$baseBackdropPath/uDgy6hyPd82kRVt6pVTIj12IdBI.jpg", type = "TV Show"),
        MediaItem(104, "Succession", "8.9", type = "TV Show"),
        MediaItem(105, "The Wire", "9.3", type = "TV Show"),
        MediaItem(106, "The Sopranos", "9.2", type = "TV Show"),
        MediaItem(107, "Chernobyl", "9.4", type = "TV Show"),
        MediaItem(108, "Stranger Things", "8.7", type = "TV Show"),
        MediaItem(109, "The Bear", "8.6", type = "TV Show"),
        MediaItem(110, "Beef", "8.0", type = "TV Show"),
        MediaItem(111, "Dark", "8.7", type = "TV Show"),
        MediaItem(112, "Mindhunter", "8.6", type = "TV Show"),
        MediaItem(113, "True Detective", "8.9", type = "TV Show"),
        MediaItem(114, "Fargo", "8.9", type = "TV Show"),
        MediaItem(115, "The Boys", "8.7", type = "TV Show")
    )

    private val samplePeople = listOf(
        Person(201, "Christopher Nolan", "Director", profileUrl = "$basePosterPath/xuAIuYSs4dZNwgwtZNyD6S5EseT.jpg", knownFor = "Oppenheimer"),
        Person(202, "Cillian Murphy", "Actor", profileUrl = "$basePosterPath/i8dO4JEepTWsUymF5iQz9gA2RWI.jpg", knownFor = "Peaky Blinders"),
        Person(203, "Pedro Pascal", "Actor", profileUrl = "$basePosterPath/nmsGj9K5K1aAALa9L3bH2UuNksy.jpg", knownFor = "The Mandalorian"),
        Person(204, "Greta Gerwig", "Director", knownFor = "Barbie"),
        Person(205, "Quentin Tarantino", "Director", knownFor = "Pulp Fiction"),
        Person(206, "Leonardo DiCaprio", "Actor", knownFor = "Inception"),
        Person(207, "Meryl Streep", "Actor", knownFor = "The Devil Wears Prada"),
        Person(208, "Tom Hanks", "Actor", knownFor = "Forrest Gump"),
        Person(209, "Brad Pitt", "Actor", knownFor = "Fight Club"),
        Person(210, "Margot Robbie", "Actor", knownFor = "Barbie"),
        Person(211, "Robert De Niro", "Actor", knownFor = "Taxi Driver"),
        Person(212, "Martin Scorsese", "Director", knownFor = "The Irishman"),
        Person(213, "Steven Spielberg", "Director", knownFor = "Jurassic Park"),
        Person(214, "Denis Villeneuve", "Director", knownFor = "Dune"),
        Person(215, "Anya Taylor-Joy", "Actor", knownFor = "The Queen's Gambit")
    )

    fun getTrendingMovies(): Flow<List<MediaItem>> = flow {
        emit(sampleMovies)
    }

    fun searchMovies(query: String): Flow<List<MediaItem>> = flow {
        emit(sampleMovies.filter { it.title.contains(query, ignoreCase = true) })
    }

    fun searchTvShows(query: String): Flow<List<MediaItem>> = flow {
        emit(sampleTvShows.filter { it.title.contains(query, ignoreCase = true) })
    }

    fun searchPeople(query: String): Flow<List<Person>> = flow {
        emit(samplePeople.filter { it.name.contains(query, ignoreCase = true) })
    }

    fun getMovieById(id: Int): Flow<MediaItem?> = flow {
        emit(sampleMovies.find { it.id == id })
    }

    fun getTvShowById(id: Int): Flow<MediaItem?> = flow {
        emit(sampleTvShows.find { it.id == id })
    }

    fun getPersonById(id: Int): Flow<Person?> = flow {
        emit(samplePeople.find { it.id == id })
    }

    fun getVaultMovies(): Flow<List<MediaItem>> = flow {
        emit(savedVaultMovies.toList())
    }

    fun addToVault(movie: MediaItem) {
        if (!savedVaultMovies.any { it.id == movie.id }) {
            savedVaultMovies.add(movie)
        }
    }

    fun removeFromVault(movieId: Int) {
        savedVaultMovies.removeAll { it.id == movieId }
    }

    fun isInVault(movieId: Int): Boolean = savedVaultMovies.any { it.id == movieId }
}