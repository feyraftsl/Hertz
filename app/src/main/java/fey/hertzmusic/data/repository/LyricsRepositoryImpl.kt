package fey.hertzmusic.data.repository

import fey.hertzmusic.data.source.local.lyrics.LyricsExtractor
import fey.hertzmusic.data.source.local.lyrics.LyricsParser
import fey.hertzmusic.domain.model.Lyrics
import fey.hertzmusic.domain.repository.LyricsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepositoryImpl @Inject constructor() : LyricsRepository {

    override fun lyricsFor(path: String, mime: String): Lyrics? =
        LyricsExtractor.extract(path, mime)?.let(LyricsParser::parse)
}
