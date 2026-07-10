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
import fey.hertzmusic.domain.model.Folder
import fey.hertzmusic.presentation.player.HertzAction
import fey.hertzmusic.presentation.player.HertzState
import fey.hertzmusic.ui.theme.TuiDim
import fey.hertzmusic.ui.theme.TuiFg
import fey.hertzmusic.ui.theme.TuiLine
import fey.hertzmusic.ui.theme.TuiSurface
import fey.hertzmusic.util.asTime

@Composable
fun FilesPane(state: HertzState, dispatch: (HertzAction) -> Unit) {
    val folder: Folder? = state.folders.find { it.path == state.openFolder }

    if (folder == null) {
        FolderList(state, dispatch)
    } else {
        FolderDetail(folder, state, dispatch)
    }
}

@Composable
private fun FolderList(state: HertzState, dispatch: (HertzAction) -> Unit) {
    if (state.folders.isEmpty()) {
        Caption(stringResource(R.string.no_files))
        return
    }

    Column {
        SearchRow(
            query = state.query,
            hint = pluralStringResource(
                R.plurals.search_folders_hint,
                state.folders.size,
                state.folders.size,
            ),
            shown = state.filteredFolders.size,
            onQuery = { dispatch(HertzAction.Query(it)) },
        )
        if (state.filteredFolders.isEmpty()) {
            Caption(stringResource(R.string.no_match))
        }
        LazyColumn {
            itemsIndexed(state.filteredFolders, key = { _, f -> f.path }) { index, f ->
                ListRow(
                    index = index,
                    line1 = f.name,
                    line2 = "${f.tracks.size} trk",
                    current = false,
                    onClick = { dispatch(HertzAction.OpenFolder(f.path)) },
                    onLongClick = { dispatch(HertzAction.Enqueue(f.tracks, f.name)) },
                )
            }
        }
    }
}

@Composable
private fun FolderDetail(
    folder: Folder,
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
                    .tuiClickable { dispatch(HertzAction.OpenFolder(null)) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            )
            Text(
                text = folder.name.lowercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TuiDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
        }
        itemsIndexed(folder.tracks) { index, track ->
            ListRow(
                index = index,
                line1 = track.title,
                line2 = "${track.artist} · ${track.durationMs.asTime()}".lowercase(),
                current = track.id.toString() == state.nowPlayingId,
                onClick = { dispatch(HertzAction.PlayAt(folder.tracks, index)) },
            )
        }
    }
}
