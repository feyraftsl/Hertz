package fey.hertzmusic.presentation.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import fey.hertzmusic.R
import fey.hertzmusic.core.common.Caption
import fey.hertzmusic.core.common.ListRow
import fey.hertzmusic.core.common.SearchRow
import fey.hertzmusic.core.common.TuiKey
import fey.hertzmusic.presentation.player.DmtAction
import fey.hertzmusic.presentation.player.DmtState
import fey.hertzmusic.util.asTime

@Composable
fun LibraryPane(state: DmtState, dispatch: (DmtAction) -> Unit) {
    if (state.tracks.isEmpty()) {
        Caption(stringResource(R.string.no_audio))
        TuiKey(stringResource(R.string.rescan)) { dispatch(DmtAction.Rescan) }
        return
    }

    Column {
        SearchRow(
            query = state.query,
            hint = pluralStringResource(
                R.plurals.search_tracks_hint,
                state.tracks.size,
                state.tracks.size,
            ),
            shown = state.filtered.size,
            onQuery = { dispatch(DmtAction.Query(it)) },
        )
        if (state.filtered.isEmpty()) {
            Caption(stringResource(R.string.no_match))
        }
        LazyColumn {
            itemsIndexed(state.filtered, key = { _, track -> track.id }) { index, track ->
                ListRow(
                    index = index,
                    line1 = track.title,
                    line2 = "${track.artist} · ${track.durationMs.asTime()}".lowercase(),
                    current = track.id.toString() == state.nowPlayingId,
                    onClick = { dispatch(DmtAction.PlayAt(state.filtered, index)) },
                    onLongClick = { dispatch(DmtAction.Enqueue(listOf(track), track.title)) },
                )
            }
        }
    }
}
