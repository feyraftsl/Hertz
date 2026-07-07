package fey.hertzmusic.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import fey.hertzmusic.domain.model.Spec
import fey.hertzmusic.domain.model.Track
import fey.hertzmusic.domain.repository.TrackMediaRepository
import fey.hertzmusic.util.asKHz
import fey.hertzmusic.util.asMB
import fey.hertzmusic.util.codecLabel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackMediaRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : TrackMediaRepository {

    override fun loadArt(uri: Uri): Bitmap? {
        val bitmap = runCatching {
            context.contentResolver.loadThumbnail(uri, Size(512, 512), null)
        }.getOrNull()
        if (bitmap != null) return bitmap

        return runCatching {
            MediaMetadataRetriever().use { retriever ->
                if (uri.scheme == "http" || uri.scheme == "https") {
                    retriever.setDataSource(uri.toString(), HashMap<String, String>())
                } else {
                    retriever.setDataSource(context, uri)
                }
                retriever.embeddedPicture?.let { bytes ->
                    android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }
        }.getOrNull()
    }

    override fun techSpecs(uri: Uri, track: Track?): List<Spec> {
        var mime = track?.mime.orEmpty()
        var bitrate = track?.bitrate ?: 0
        var sampleRate: Int? = null
        var channels: Int? = null
        var bits: Int? = null
        runCatching {
            val extractor = MediaExtractor()
            if (uri.scheme == "http" || uri.scheme == "https") {
                extractor.setDataSource(uri.toString())
            } else {
                extractor.setDataSource(context, uri, null)
            }
            val format = extractor.getTrackFormat(0)
            format.getString(MediaFormat.KEY_MIME)?.let {
                if (mime.isEmpty() || mime == "audio/?") mime = it
            }
            if (format.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
                sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            }
            if (format.containsKey(MediaFormat.KEY_CHANNEL_COUNT)) {
                channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            }
            if (bitrate <= 0 && format.containsKey(MediaFormat.KEY_BIT_RATE)) {
                bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE)
            }
            extractor.release()
        }
        runCatching {
            MediaMetadataRetriever().use { retriever ->
                if (uri.scheme == "http" || uri.scheme == "https") {
                    retriever.setDataSource(uri.toString(), HashMap<String, String>())
                } else {
                    retriever.setDataSource(context, uri)
                }
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE)
                    ?.toIntOrNull()?.takeIf { it > 0 }?.let { bits = it }
                if (sampleRate == null) {
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)
                        ?.toIntOrNull()?.let { sampleRate = it }
                }
                if (bitrate <= 0) {
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                        ?.toIntOrNull()?.let { bitrate = it }
                }
            }
        }
        return buildList {
            if (mime.isNotEmpty()) {
                add(
                    Spec(
                        label = "FMT",
                        value = mime.codecLabel(),
                    ),
                )
            }
            bits?.let {
                add(
                    Spec(
                        label = "BIT",
                        value = "$it",
                    ),
                )
            }
            sampleRate?.let {
                add(
                    Spec(
                        label = "RATE",
                        value = it.asKHz(),
                    ),
                )
            }
            channels?.let {
                add(
                    Spec(
                        label = "CH",
                        value = if (it == 2) "ST" else "$it",
                    ),
                )
            }
            if (bitrate > 0) {
                add(
                    Spec(
                        label = "KBPS",
                        value = "${bitrate / 1000}",
                        hot = true,
                    ),
                )
            }
            track?.size?.takeIf { it > 0 }?.let {
                add(
                    Spec(
                        label = "SIZE",
                        value = it.asMB(),
                    ),
                )
            }
        }
    }
}
