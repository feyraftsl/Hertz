package fey.hertzmusic.data.repository

import android.net.Uri
import fey.hertzmusic.domain.model.Track
import fey.hertzmusic.domain.repository.MediaRepository
import fey.hertzmusic.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteMediaRepositoryImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : MediaRepository {

    override suspend fun scan(): List<Track> {
        val settings = settingsRepository.settings.first()
        val serverUrl = settings.serverUrl.removeSuffix("/")
        if (serverUrl.isBlank()) return emptyList()

        val jsonUrl = "$serverUrl/music.json"
        
        return runCatching {
            val url = URL(jsonUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            val content = connection.inputStream.bufferedReader().use { it.readText() }
            val array = JSONArray(content)
            
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val trackUrl = obj.getString("url")
                    val fullUrl = if (trackUrl.startsWith("http")) trackUrl else "$serverUrl/$trackUrl"
                    
                    add(
                        Track(
                            id = obj.optLong("id", i.toLong()),
                            uri = Uri.parse(fullUrl),
                            title = obj.optString("title", "unknown title"),
                            artist = obj.optString("artist", "unknown artist"),
                            album = obj.optString("album", "unknown album"),
                            albumId = obj.optLong("albumId", -1L),
                            path = fullUrl,
                            durationMs = obj.optLong("duration", 0L),
                            mime = obj.optString("mime", "audio/mpeg"),
                            bitrate = obj.optInt("bitrate", 0),
                            size = obj.optLong("size", 0L),
                            trackNumber = obj.optInt("trackNumber", 0),
                        )
                    )
                }
            }
        }.getOrElse { 
            it.printStackTrace()
            emptyList()
        }
    }
}
