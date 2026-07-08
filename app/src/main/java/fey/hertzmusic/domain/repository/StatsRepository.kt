package fey.hertzmusic.domain.repository

import fey.hertzmusic.domain.model.DmtStats
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    val stats: Flow<DmtStats>

    suspend fun recordPlayback(playedMs: Long, trackId: Long?)
}
