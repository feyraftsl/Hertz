package fey.hertzmusic.domain.repository

import fey.hertzmusic.domain.model.HertzStats
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    val stats: Flow<HertzStats>

    suspend fun recordPlayback(playedMs: Long, trackId: Long?)
}
