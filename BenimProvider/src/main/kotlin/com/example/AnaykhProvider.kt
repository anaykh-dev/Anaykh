package com.example

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.newMovieSearchResponse


class AnaykhProvider : MainAPI() {

    override var mainUrl = "https://api.themoviedb.org/"
    override var name = "Anaykh"
    override val supportedTypes = setOf(TvType.Movie)
    override var lang = "tr"

    override val hasMainPage = false

    data class TmdbSearchResponse(
        val results: List<TmdbMovie> = emptyList()
    )

    data class TmdbMovie(
        val id: Int,
        val title: String = "",
        @JsonProperty("release_date")
        val releaseDate: String = "",
        val overview: String = "",
        @JsonProperty("poster_path")
        val posterPath: String? = null
    )

    override suspend fun search(query: String): List<SearchResponse> {

        val encodedQuery = java.net.URLEncoder.encode(
            query,
            "UTF-8"
        )

        val url =
            "https://api.themoviedb.org/3/search/movie" +
            "?api_key=${BuildConfig.TMDB_API_KEY}" +
            "&query=$encodedQuery" +
            "&language=tr-TR"

        val response = app.get(url).parsed<TmdbSearchResponse>()

        return response.results.map { movie ->

            newMovieSearchResponse(
                movie.title,
                "https://www.themoviedb.org/movie/${movie.id}",
                TvType.Movie
            ) {
                posterUrl = movie.posterPath?.let {
                    "https://image.tmdb.org/t/p/w500$it"
                }

                year = movie.releaseDate
                    .takeIf { it.length >= 4 }
                    ?.substring(0, 4)
                    ?.toIntOrNull()
            }
        }
    }
}