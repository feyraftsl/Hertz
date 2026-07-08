package fey.hertzmusic.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import fey.hertzmusic.domain.repository.TrackMediaRepository
import fey.hertzmusic.util.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCoverArtUseCase @Inject constructor(
    private val trackMediaRepository: TrackMediaRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(uri: Uri): Bitmap? =
        withContext(dispatchers.io) {
            trackMediaRepository.loadArt(uri)
        }
}
