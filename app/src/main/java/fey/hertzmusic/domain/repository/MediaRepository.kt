package fey.hertzmusic.domain.repository

import fey.hertzmusic.domain.model.Track

interface MediaRepository {
    suspend fun scan(): List<Track>
}
