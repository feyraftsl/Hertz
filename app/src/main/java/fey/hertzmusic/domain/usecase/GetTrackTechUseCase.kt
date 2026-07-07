package fey.hertzmusic.domain.usecase

import android.net.Uri
import fey.hertzmusic.domain.model.Spec
import fey.hertzmusic.domain.model.Track
import fey.hertzmusic.domain.repository.TrackMediaRepository
import fey.hertzmusic.util.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTrackTechUseCase @Inject constructor(
    private val trackMediaRepository: TrackMediaRepository,
    private val dispatchers: DispatcherProvider,
) {
    suspend operator fun invoke(uri: Uri, track: Track?): List<Spec> =
        withContext(dispatchers.io) {
            trackMediaRepository.techSpecs(uri, track)
        }
}
