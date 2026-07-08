package fey.hertzmusic.domain.usecase

import fey.hertzmusic.domain.model.LibrarySnapshot
import fey.hertzmusic.domain.model.toAlbums
import fey.hertzmusic.domain.model.toArtists
import fey.hertzmusic.domain.model.toFolders
import fey.hertzmusic.domain.repository.MediaRepository
import fey.hertzmusic.domain.repository.SettingsRepository
import fey.hertzmusic.di.Local
import fey.hertzmusic.di.Remote
import fey.hertzmusic.util.DispatcherProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScanLibraryUseCase @Inject constructor(
    @Local private val localRepository: MediaRepository,
    @Remote private val remoteRepository: MediaRepository,
    private val settingsRepository: SettingsRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(): LibrarySnapshot =
        withContext(dispatchers.io) {
            val settings = settingsRepository.settings.first()
            val tracks = if (settings.onlineMode) {
                remoteRepository.scan()
            } else {
                localRepository.scan()
            }
            LibrarySnapshot(
                tracks = tracks,
                albums = tracks.toAlbums(),
                artists = tracks.toArtists(),
                folders = tracks.toFolders(),
            )
        }
}
