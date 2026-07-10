package fey.hertzmusic.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fey.hertzmusic.R
import fey.hertzmusic.core.common.Caption
import fey.hertzmusic.core.common.ListRow
import fey.hertzmusic.core.common.SearchRow
import fey.hertzmusic.core.common.tuiClickable
import fey.hertzmusic.domain.model.Artist
import fey.hertzmusic.presentation.player.HertzAction
import fey.hertzmusic.presentation.player.HertzState
import fey.hertzmusic.ui.theme.TuiDim
import fey.hertzmusic.ui.theme.TuiFg
import fey.hertzmusic.ui.theme.TuiLine
import fey.hertzmusic.ui.theme.TuiSurface
import fey.hertzmusic.util.asTime

@Composable
fun ArtistsPane(state: HertzState, dispatch: (HertzAction) -> Unit) {
    val artist: Artist? = state.artists.find { it.name == state.openArtist }

    if (artist == null) {
        ArtistList(state, dispatch)
    } else {
        ArtistDetail(artist, state, dispatch)
    }
}

@Composable
private fun ArtistList(state: HertzState, dispatch: (HertzAction) -> Unit) {
    if (state.artists.isEmpty()) {
        Caption(stringResource(R.string.no_artists))
        return
    }

    Column {
        SearchRow(
            query = state.query,
            hint = pluralStringResource(
                R.plurals.search_artists_hint,
                state.artists.size,
                state.artists.size,
            ),
            shown = state.filteredArtists.size,
            onQuery = { dispatch(HertzAction.Query(it)) },
        )
        if (state.filteredArtists.isEmpty()) {
            Caption(stringResource(R.string.no_match))
        }
        LazyColumn {
            itemsIndexed(state.filteredArtists, key = { _, a -> a.name }) { index, a ->
                ListRow(
                    index = index,
                    line1 = a.name,
                    line2 = "${a.tracks.size} trk".lowercase(),
                    current = false,
                    onClick = { dispatch(HertzAction.OpenArtist(a.name)) },
                    onLongClick = { dispatch(HertzAction.Enqueue(a.tracks, a.name)) },
                )
            }
        }
    }
}

@Composable
private fun ArtistDetail(
    artist: Artist,
    state: HertzState,
    dispatch: (HertzAction) -> Unit,
) {
    LazyColumn {
        item {
            Text(
                text = stringResource(R.string.back),
                style = MaterialTheme.typography.labelMedium,
                color = TuiFg,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .border(1.dp, TuiLine)
                    .background(TuiSurface.copy(alpha = 0.6f))
                    .tuiClickable { dispatch(HertzAction.OpenArtist(null)) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            )
            Text(
                text = artist.name.lowercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TuiDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
        }
        itemsIndexed(artist.tracks) { index, track ->
            ListRow(
                index = index,
                line1 = track.title,
                line2 = "${track.album} · ${track.durationMs.asTime()}".lowercase(),
                current = track.id.toString() == state.nowPlayingId,
                onClick = { dispatch(HertzAction.PlayAt(artist.tracks, index)) },
            )
        }
    }
}
