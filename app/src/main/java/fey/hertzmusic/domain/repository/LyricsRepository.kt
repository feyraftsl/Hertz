package fey.hertzmusic.domain.repository

import fey.hertzmusic.domain.model.Lyrics

interface LyricsRepository {
    fun lyricsFor(path: String, mime: String): Lyrics?
}
