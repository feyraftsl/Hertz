package fey.hertzmusic.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fey.hertzmusic.data.repository.LyricsRepositoryImpl
import fey.hertzmusic.data.repository.MediaRepositoryImpl
import fey.hertzmusic.data.repository.PreferencesRepositoryImpl
import fey.hertzmusic.data.repository.RemoteMediaRepositoryImpl
import fey.hertzmusic.data.repository.TrackMediaRepositoryImpl
import fey.hertzmusic.domain.repository.LyricsRepository
import fey.hertzmusic.domain.repository.MediaRepository
import fey.hertzmusic.domain.repository.SettingsRepository
import fey.hertzmusic.domain.repository.StatsRepository
import fey.hertzmusic.domain.repository.TrackMediaRepository
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Local

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Remote

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Local
    abstract fun localMediaRepository(impl: MediaRepositoryImpl): MediaRepository

    @Binds
    @Remote
    abstract fun remoteMediaRepository(impl: RemoteMediaRepositoryImpl): MediaRepository

    @Binds
    abstract fun lyricsRepository(impl: LyricsRepositoryImpl): LyricsRepository

    @Binds
    abstract fun settingsRepository(impl: PreferencesRepositoryImpl): SettingsRepository

    @Binds
    abstract fun statsRepository(impl: PreferencesRepositoryImpl): StatsRepository

    @Binds
    abstract fun trackMediaRepository(impl: TrackMediaRepositoryImpl): TrackMediaRepository
}
