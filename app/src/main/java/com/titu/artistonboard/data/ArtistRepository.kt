package com.titu.artistonboard.data

class ArtistRepository(private val dao: ArtistDao) {
    suspend fun saveArtist(artist: Artist): Long = dao.insert(artist)
    suspend fun getAll(): List<Artist> = dao.getAll()
}