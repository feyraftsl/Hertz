package fey.hertzmusic.domain.repository

import fey.hertzmusic.domain.model.DmtSettings
import fey.hertzmusic.domain.model.LastSession
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<DmtSettings>
    suspend fun save(settings: DmtSettings)
    suspend fun savedSpeed(): Float
    suspend fun saveSpeed(speed: Float)
    suspend fun savedShuffle(): Boolean
    suspend fun saveShuffle(enabled: Boolean)
    suspend fun lastSession(): LastSession?

    suspend fun saveSession(session: LastSession)
}
