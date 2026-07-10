package fey.hertzmusic.presentation.player

import android.graphics.Bitmap
import androidx.media3.common.Player
import fey.hertzmusic.domain.model.Album
import fey.hertzmusic.domain.model.Artist
import fey.hertzmusic.domain.model.HertzSettings
import fey.hertzmusic.domain.model.HertzStats
import fey.hertzmusic.domain.model.Folder
import fey.hertzmusic.domain.model.Lyrics
import fey.hertzmusic.domain.model.Spec
import fey.hertzmusic.domain.model.Track

enum class HertzView { LIBRARY, ARTISTS, ALBUMS, SETTINGS, STATS }

const val DIVIDER_ID = "queue_divider"

data class HertzState(
    val hasPermission: Boolean = false,
    val scanning: Boolean = true,
    val tracks: List<Track> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val query: String = "",
    val filtered: List<Track> = emptyList(),
    val filteredAlbums: List<Album> = emptyList(),
    val filteredArtists: List<Artist> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val filteredFolders: List<Folder> = emptyList(),
    val view: HertzView = HertzView.LIBRARY,
    val openAlbum: String? = null,
    val openArtist: String? = null,
    val openFolder: String? = null,
    val nowPlayingId: String? = null,
    val title: String = "",
    val artist: String = "",
    val isPlaying: Boolean = false,
    val shuffle: Boolean = false,
    val repeat: Int = Player.REPEAT_MODE_OFF,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val queue: List<String> = emptyList(),
    val queueIndex: Int = 0,
    val manualQueueCount: Int = 0,
    val album: String = "",
    val cover: Bitmap? = null,
    val artRaw: Bitmap? = null,
    val lyrics: Lyrics? = null,
    val expanded: Boolean = false,
    val sleepMinutes: Int = 0,
    val sleepLeftMs: Long = 0L,
    val speed: Float = 1f,
    val settings: HertzSettings = HertzSettings(),
    val stats: HertzStats = HertzStats(),
    val tech: List<Spec> = emptyList(),
    val error: String? = null,
    val notice: String? = null,
    val showUrlDialog: Boolean = false,
)

sealed interface HertzAction {
    data class Permission(val granted: Boolean) : HertzAction
    data object Rescan : HertzAction
    data class Query(val value: String) : HertzAction
    data class Show(val view: HertzView) : HertzAction
    data class OpenAlbum(val name: String?) : HertzAction
    data class OpenArtist(val name: String?) : HertzAction
    data class OpenFolder(val path: String?) : HertzAction
    data class PlayAt(val list: List<Track>, val index: Int) : HertzAction
    data class Enqueue(val list: List<Track>, val label: String) : HertzAction
    data class Jump(val index: Int) : HertzAction
    data object TogglePlay : HertzAction
    data object Next : HertzAction
    data object Prev : HertzAction
    data object ToggleShuffle : HertzAction
    data object CycleRepeat : HertzAction
    data class Seek(val fraction: Float) : HertzAction
    data class Expand(val value: Boolean) : HertzAction
    data class RemoveAt(val index: Int) : HertzAction
    data object CycleSleep : HertzAction
    data object CycleSpeed : HertzAction
    data object OpenEqualizer : HertzAction
    data object NoEqualizer : HertzAction
    data class Config(val settings: HertzSettings) : HertzAction
    data class ShowUrlDialog(val show: Boolean) : HertzAction
}

sealed interface PlayerEffect {
    data class OpenEqualizer(val audioSessionId: Int) : PlayerEffect
}
