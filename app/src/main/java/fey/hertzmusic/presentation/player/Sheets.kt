package fey.hertzmusic.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fey.hertzmusic.R
import fey.hertzmusic.core.common.tuiClickable
import fey.hertzmusic.domain.model.Track
import fey.hertzmusic.ui.theme.LocalAccent
import fey.hertzmusic.ui.theme.TuiBg
import fey.hertzmusic.ui.theme.TuiBright
import fey.hertzmusic.ui.theme.TuiDim
import fey.hertzmusic.ui.theme.TuiFaint
import fey.hertzmusic.ui.theme.TuiFg
import fey.hertzmusic.ui.theme.TuiLine
import fey.hertzmusic.util.asTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuiSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TuiBg,
        shape = RectangleShape,
        dragHandle = null,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TuiLine),
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                content()
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun SheetHeader(title: String, meta: String? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(LocalAccent.current),
            )
            Text(
                text = " $title",
                style = MaterialTheme.typography.labelMedium,
                color = TuiDim,
            )
        }
        if (meta != null) {
            Text(
                text = meta,
                style = MaterialTheme.typography.labelMedium,
                color = TuiFaint,
            )
        }
    }
}

@Composable
fun QueueList(
    state: HertzState,
    dispatch: (HertzAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = LocalAccent.current

    LazyColumn(modifier = modifier) {
        itemsIndexed(state.queue) { index, label ->
            if (label == DIVIDER_ID) {
                QueueDivider()
            } else {
                val realIndex = if (state.manualQueueCount > 0 && index > state.queueIndex + state.manualQueueCount) {
                    index - 1
                } else {
                    index
                }
                val current = realIndex == state.queueIndex
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .tuiClickable { dispatch(HertzAction.Jump(realIndex)) }
                        .padding(vertical = 8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if (current) accent else TuiFaint),
                    )
                    Text(
                        text = " %02d ".format(realIndex + 1),
                        style = MaterialTheme.typography.labelSmall,
                        color = TuiFaint,
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (current) TuiBright else TuiDim,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(R.string.clear),
                        style = MaterialTheme.typography.labelMedium,
                        color = TuiFaint,
                        modifier = Modifier
                            .tuiClickable { dispatch(HertzAction.RemoveAt(realIndex)) }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(TuiLine)
        )
        Text(
            text = " queue ",
            style = MaterialTheme.typography.labelSmall,
            color = TuiFaint,
        )
        Box(
            modifier = Modifier
                .weight(3f)
                .height(1.dp)
                .background(TuiLine)
        )
    }
}

@Composable
fun InfoContent(state: HertzState) {
    val track: Track? = state.tracks.find { it.id.toString() == state.nowPlayingId }

    InfoRow(
        label = stringResource(R.string.info_title),
        value = state.title,
    )
    InfoRow(
        label = stringResource(R.string.info_artist),
        value = state.artist.lowercase(),
    )
    if (state.album.isNotBlank()) {
        InfoRow(
            label = stringResource(R.string.info_album),
            value = state.album.lowercase(),
        )
    }
    InfoRow(
        label = stringResource(R.string.info_duration),
        value = state.durationMs.asTime(),
    )
    state.tech.forEach { spec ->
        InfoRow(
            label = spec.label.lowercase(),
            value = spec.value.lowercase(),
        )
    }
    track?.let {
        InfoRow(
            label = stringResource(R.string.info_uri),
            value = it.uri.toString(),
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TuiDim,
                modifier = Modifier.padding(end = 16.dp),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = TuiFg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(TuiLine),
        )
    }
}
