package fey.hertzmusic.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fey.hertzmusic.util.DefaultDispatcherProvider
import fey.hertzmusic.util.DispatcherProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun dispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
