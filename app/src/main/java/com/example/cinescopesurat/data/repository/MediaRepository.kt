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
        MediaItem(2, "Inception", "8.8", posterUrl = "$basePosterPath/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg", backdropUrl = "$baseBackdropPath/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg", type = "Movie"),
        MediaItem(3, "Interstellar", "8.7", posterUrl = "$basePosterPath/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg", backdropUrl = "$baseBackdropPath/pbrkL804c8yAv3zBZR4QPEafpAR.jpg", type = "Movie"),
        MediaItem(4, "Fight Club", "8.8", posterUrl = "$basePosterPath/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg", backdropUrl = "$baseBackdropPath/hZkgoQYus5vesKepmARPhLpGRo0.jpg", type = "Movie"),
        MediaItem(5, "Pulp Fiction", "8.9", posterUrl = "$basePosterPath/d5iIlFn5s0ImszYzBPbOYKQszB.jpg", backdropUrl = "$baseBackdropPath/suaEOtk1N1sgg2MTM7oZd2cfVp3.jpg", type = "Movie"),
        MediaItem(6, "The Godfather", "9.2", posterUrl = "$basePosterPath/3bhkrj58Vtu7enYsRolD1fZdja1.jpg", backdropUrl = "$baseBackdropPath/tmU7GeKVybMWFButWEGl2M4GeiP.jpg", type = "Movie"),
        MediaItem(7, "The Matrix", "8.7", posterUrl = "$basePosterPath/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg", backdropUrl = "$baseBackdropPath/lMnoYqPIAVL0YaLP5YjRy7iwaYv.jpg", type = "Movie"),
        MediaItem(8, "Se7en", "8.6", posterUrl = "$basePosterPath/6yoghtyTpznpBik8EngEmJskVPh.jpg", backdropUrl = "$baseBackdropPath/1BwqMVgA4vB5Tioh2U62jSNCF36.jpg", type = "Movie"),
        MediaItem(9, "Gladiator", "8.5", posterUrl = "$basePosterPath/ty8TGRuvJLPUmAR1H1nRIsgwvqV.jpg", backdropUrl = "$baseBackdropPath/mKqRofX6z9eJmEQ7QZks2bH9K0E.jpg", type = "Movie"),
        MediaItem(10, "The Silence of the Lambs", "8.6", posterUrl = "$basePosterPath/uS9m8OBk1A8eM9I042bx8XXpqAq.jpg", backdropUrl = "$baseBackdropPath/a1MlbNsoUfApg5A9U3s3XfJIfgW.jpg", type = "Movie"),
        MediaItem(11, "Saving Private Ryan", "8.6", posterUrl = "$basePosterPath/1wY4psJ5N51eIGm2rEreW0eH229.jpg", backdropUrl = "$baseBackdropPath/wKhtTqA2HnnB01WlT0NXXzXQ2r2.jpg", type = "Movie"),
        MediaItem(12, "Avatar", "7.8", posterUrl = "$basePosterPath/vL5LR6WdxWPjLPFRLe133jXWsh5.jpg", backdropUrl = "$baseBackdropPath/9BBTo63ANSmhC4e6r62OJFuK2GL.jpg", type = "Movie"),
        MediaItem(13, "The Avengers", "8.0", posterUrl = "$basePosterPath/RYMX2wcKCBAr24UyPD7xwmja8y.jpg", backdropUrl = "$baseBackdropPath/nNmJRkg8wWnRmzQDe2FwKbPIsNd.jpg", type = "Movie"),
        MediaItem(14, "Jurassic World", "7.0", posterUrl = "$basePosterPath/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg", backdropUrl = "$baseBackdropPath/dkMD5qlogeRMiEixC4YNPUvax2T.jpg", type = "Movie"),
        MediaItem(15, "The Martian", "8.0", posterUrl = "$basePosterPath/kqjL17yufvn9OVLyXYpvtyrFfak.jpg", backdropUrl = "$baseBackdropPath/sy3e2e4JwdAtd2oZGA2uUilZe8j.jpg", type = "Movie"),
        MediaItem(16, "Goodfellas", "8.7", posterUrl = "$basePosterPath/aKuFiU82s5ISJpGZp7YkIr3kCUd.jpg", backdropUrl = "$baseBackdropPath/sw7mordbZxgITU877yTpZCud90M.jpg", type = "Movie"),
        MediaItem(17, "Parasite", "8.5", posterUrl = "$basePosterPath/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg", backdropUrl = "$baseBackdropPath/hiKmpZMGZsrkA3cdce8a7Dpos1j.jpg", type = "Movie"),
        MediaItem(18, "Star Wars: The Force Awakens", "7.9", posterUrl = "$basePosterPath/fYzpM9GmpBlIC893fNjoWCwE24H.jpg", backdropUrl = "$baseBackdropPath/c2Ax8Rox5g6CneChwy1gmu4UbSb.jpg", type = "Movie"),
        MediaItem(19, "Spirited Away", "8.6", posterUrl = "$basePosterPath/39wmItIWsg5sZMyRUHLkBg8lWOb.jpg", backdropUrl = "$baseBackdropPath/bSXfU4dwZyBA1vMmXvejdRXBvuF.jpg", type = "Movie"),
        MediaItem(20, "Terminator 2: Judgment Day", "8.5", posterUrl = "$basePosterPath/5M0j0B18abtBI5gi2RhfjjurTqb.jpg", backdropUrl = "$baseBackdropPath/aWeKITrf26MBEacvZ9mbzX54Y2d.jpg", type = "Movie")
    )

    private val sampleTvShows = listOf(
        MediaItem(101, "Breaking Bad", "9.5", posterUrl = "$basePosterPath/ggFHVNu6YYI5L9pCfOacjizRGt.jpg", backdropUrl = "$baseBackdropPath/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg", type = "TV Show"),
        MediaItem(102, "Stranger Things", "8.6", posterUrl = "$basePosterPath/49WJfeN0moxb9IPfGn8ANYqZ6Ze.jpg", backdropUrl = "$baseBackdropPath/56v2KjBlU4XaOv9rVYEQypROD7P.jpg", type = "TV Show"),
        MediaItem(103, "Game of Thrones", "9.2", posterUrl = "$basePosterPath/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg", backdropUrl = "$baseBackdropPath/suopoADq0k8YZr4dQXcU6pToj6s.jpg", type = "TV Show"),
        MediaItem(104, "Chernobyl", "9.4", posterUrl = "$basePosterPath/hlLXt2tOPT6RRnjiUmF51oVR6pb.jpg", backdropUrl = "$baseBackdropPath/30Er1yB6P9YxXgYwX7kGz7uN7iC.jpg", type = "TV Show"),
        MediaItem(105, "The Wire", "9.3", posterUrl = "$basePosterPath/4lbcl0b0UuYq6uH4L208K9KkQ0g.jpg", backdropUrl = "$baseBackdropPath/xO1y8fF9H4A2YQY8q9KkH3V8S2H.jpg", type = "TV Show"),
        MediaItem(106, "The Sopranos", "9.2", posterUrl = "$basePosterPath/2nuNzX1x2fE8h8N4j6Tf9P1p2w8.jpg", backdropUrl = "$baseBackdropPath/5iG9yO4k9A5W7fX6U2M0U8Y4A5.jpg", type = "TV Show"),
        MediaItem(107, "The Office", "8.9", posterUrl = "$basePosterPath/qWnJylZsm7HIyGjc3Qh0OMbOIl.jpg", backdropUrl = "$baseBackdropPath/pXjpqrx65al8P4w4w42Lq0rA5N9.jpg", type = "TV Show"),
        MediaItem(108, "Dark", "8.7", posterUrl = "$basePosterPath/apbrbWs8M9lyOpJYU5WXrpFbk1Z.jpg", backdropUrl = "$baseBackdropPath/5uXW6y9XJjK9X9Q9X1k8zR9J2M.jpg", type = "TV Show"),
        MediaItem(109, "The Boys", "8.4", posterUrl = "$basePosterPath/stTEycfG9928HYGEISBFaG1ngjM.jpg", backdropUrl = "$baseBackdropPath/mzzHr6g1yvl05xcPMamxeEcgX0a.jpg", type = "TV Show"),
        MediaItem(110, "Peaky Blinders", "8.8", posterUrl = "$basePosterPath/vUUqzWa2LnHIVqkaKVlVGkVcZIW.jpg", backdropUrl = "$baseBackdropPath/bGk2yqBty6H0iZzB4H9mHk19D0I.jpg", type = "TV Show"),
        MediaItem(111, "True Detective", "8.9", posterUrl = "$basePosterPath/aowrZrxCLK1R3l1BqA8t82H0C2i.jpg", backdropUrl = "$baseBackdropPath/hPGB2N21oZ4rA2c6Q6BvC6Q0K4.jpg", type = "TV Show"),
        MediaItem(112, "Black Mirror", "8.3", posterUrl = "$basePosterPath/he609rnU3tiwBjRklKNa4n2jQSd.jpg", backdropUrl = "$baseBackdropPath/rMCew7St2vy9iV3QOPzx15sAkFJ.jpg", type = "TV Show"),
        MediaItem(113, "Rick and Morty", "9.1", posterUrl = "$basePosterPath/8kOWDBK6XlPUzckuHDo3wwVRFwt.jpg", backdropUrl = "$baseBackdropPath/kV27j3xz40TNOCClLgQApUAc60W.jpg", type = "TV Show"),
        MediaItem(114, "Better Call Saul", "9.0", posterUrl = "$basePosterPath/fC2HDm5t0kHlAMOINtDpWdipEAY.jpg", backdropUrl = "$baseBackdropPath/hPea3Qy5Gd6z4kJLUruBbwAH8Rm.jpg", type = "TV Show"),
        MediaItem(115, "Succession", "8.9", posterUrl = "$basePosterPath/7bLkVhZ2D3mO7D3dD8E4x1k5yB0.jpg", backdropUrl = "$baseBackdropPath/7o8Xk9I4w7P2J3W7d6QyE9Z0bA4.jpg", type = "TV Show")
    )

    private val samplePeople = listOf(
        Person(201, "Christopher Nolan", "Director", profileUrl = "$basePosterPath/xuAIuYSs4dZNwgwtZNyD6S5EseT.jpg", knownFor = "The Dark Knight"),
        Person(202, "Leonardo DiCaprio", "Actor", profileUrl = "$basePosterPath/wo2hJpn04vbtmh0B9utCFdsQhxM.jpg", knownFor = "Inception"),
        Person(203, "Meryl Streep", "Actor", profileUrl = "$basePosterPath/vB6qYlFXgONGVwwxWXE4X0HQARz.jpg", knownFor = "The Devil Wears Prada"),
        Person(204, "Tom Hanks", "Actor", profileUrl = "$basePosterPath/xndWFsBlClOJFRdhStHQYvcQQ6K.jpg", knownFor = "Forrest Gump"),
        Person(205, "Brad Pitt", "Actor", profileUrl = "$basePosterPath/cckcYc2v0yh1tc9QjRelptcOBko.jpg", knownFor = "Fight Club"),
        Person(206, "Margot Robbie", "Actor", profileUrl = "$basePosterPath/1K4T8hG2H7h0P0zQ2Q2r2K0hT8h.jpg", knownFor = "Barbie"),
        Person(207, "Cillian Murphy", "Actor", profileUrl = "$basePosterPath/i8dO4JEepTWsUymF5iQz9gA2RWI.jpg", knownFor = "Peaky Blinders"),
        Person(208, "Quentin Tarantino", "Director", profileUrl = "$basePosterPath/1gjcpAa99FAOWGnrUvHEXXsRsLS.jpg", knownFor = "Pulp Fiction"),
        Person(209, "Robert De Niro", "Actor", profileUrl = "$basePosterPath/cT8htcckIuyI1Lqwt1CvD02zBv8.jpg", knownFor = "Taxi Driver"),
        Person(210, "Martin Scorsese", "Director", profileUrl = "$basePosterPath/9AdbXN6oIq3H3qV8k9A6F7aV7K8.jpg", knownFor = "Goodfellas"),
        Person(211, "Steven Spielberg", "Director", profileUrl = "$basePosterPath/tZxcg19YQ3e8fJ0emK4V1H9aT9c.jpg", knownFor = "Jurassic Park"),
        Person(212, "Denis Villeneuve", "Director", profileUrl = "$basePosterPath/t3x3rW2FfX8hF1X2X7A5kG5fC5H.jpg", knownFor = "Dune"),
        Person(213, "Anya Taylor-Joy", "Actor", profileUrl = "$basePosterPath/1NrwL6p1P8j3H2H8aH2kF2A5aT8.jpg", knownFor = "The Queen's Gambit"),
        Person(214, "Pedro Pascal", "Actor", profileUrl = "$basePosterPath/nmsGj9K5K1aAALa9L3bH2UuNksy.jpg", knownFor = "The Last of Us"),
        Person(215, "Greta Gerwig", "Director", profileUrl = "$basePosterPath/1Q2Z4T2G8vV8hR3A4R5H2L4c6B8.jpg", knownFor = "Barbie")
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