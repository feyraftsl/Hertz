package fey.hertzmusic.domain.usecase

import fey.hertzmusic.domain.model.LibrarySnapshot
import fey.hertzmusic.domain.model.toAlbums
import fey.hertzmusic.domain.model.toArtists
import fey.hertzmusic.domain.model.toFolders
import fey.hertzmusic.domain.repository.MediaRepository
import fey.hertzmusic.util.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScanLibraryUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(): LibrarySnapshot =
        withContext(dispatchers.io) {
            val tracks = mediaRepository.scan()
            LibrarySnapshot(
                tracks = tracks,
                albums = tracks.toAlbums(),
                artists = tracks.toArtists(),
                folders = tracks.toFolders(),
            )
        }
}
