package fey.hertzmusic.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import fey.hertzmusic.domain.model.Spec
import fey.hertzmusic.domain.model.Track

interface TrackMediaRepository {
    fun loadArt(uri: Uri): Bitmap?
    fun techSpecs(uri: Uri, track: Track?): List<Spec>
}
