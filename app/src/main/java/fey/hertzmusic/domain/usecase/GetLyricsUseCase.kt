package fey.hertzmusic.domain.usecase

import fey.hertzmusic.domain.model.Lyrics
import fey.hertzmusic.domain.model.Track
import fey.hertzmusic.domain.repository.LyricsRepository
import fey.hertzmusic.util.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(track: Track): Lyrics? =
        withContext(dispatchers.io) {
            lyricsRepository.lyricsFor(track.path, track.mime)
        }
}
