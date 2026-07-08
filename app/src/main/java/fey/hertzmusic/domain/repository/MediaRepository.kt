package fey.hertzmusic.domain.repository

import fey.hertzmusic.domain.model.Track

interface MediaRepository {
    fun scan(): List<Track>
}
