package fey.hertzmusic.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fey.hertzmusic.R
import fey.hertzmusic.core.common.Caption
import fey.hertzmusic.core.common.FitScaled
import fey.hertzmusic.core.common.TuiPanel
import fey.hertzmusic.core.common.TuiTab
import fey.hertzmusic.presentation.library.AlbumsPane
import fey.hertzmusic.presentation.library.ArtistsPane
import fey.hertzmusic.presentation.library.LibraryPane
import fey.hertzmusic.presentation.player.HertzAction
import fey.hertzmusic.presentation.player.HertzState
import fey.hertzmusic.presentation.player.HertzView
import fey.hertzmusic.presentation.player.ExpandedPlayer
import fey.hertzmusic.presentation.player.InfoContent
import fey.hertzmusic.presentation.player.MiniPlayer
import fey.hertzmusic.presentation.player.QueueList
import fey.hertzmusic.presentation.player.SheetHeader
import fey.hertzmusic.presentation.player.TuiSheet
import fey.hertzmusic.presentation.settings.SettingsPane
import fey.hertzmusic.presentation.settings.StatsPane
import fey.hertzmusic.ui.theme.LocalAccent
import fey.hertzmusic.ui.theme.TuiBg
import fey.hertzmusic.ui.theme.TuiBright
import fey.hertzmusic.ui.theme.TuiDim

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HertzScreen(
    state: HertzState,
    dispatch: (HertzAction) -> Unit,
    onRequestPermission: () -> Unit,
) {
    var showQueueSheet by remember { mutableStateOf(false) }
    var showInfoSheet by remember { mutableStateOf(false) }
    val imeVisible = WindowInsets.isImeVisible
    val windowSize = LocalWindowInfo.current.containerSize
    val landscape = windowSize.width > windowSize.height

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(state.expanded, state.view, showQueueSheet, showInfoSheet) {
        focusManager.clearFocus()
        keyboard?.hide()
    }

    LaunchedEffect(state.queue.isEmpty()) {
        if (state.queue.isEmpty()) showQueueSheet = false
    }

    val backHandled = state.expanded ||
        (state.view == HertzView.ARTISTS && state.openArtist != null) ||
        (state.view == HertzView.ALBUMS && state.openAlbum != null) ||
        state.view != HertzView.LIBRARY
    BackHandler(enabled = backHandled) {
        when {
            state.expanded -> dispatch(HertzAction.Expand(false))

            state.view == HertzView.STATS -> dispatch(HertzAction.Show(HertzView.SETTINGS))

            state.view == HertzView.ARTISTS && state.openArtist != null ->
                dispatch(HertzAction.OpenArtist(null))

            state.view == HertzView.ALBUMS && state.openAlbum != null ->
                dispatch(HertzAction.OpenAlbum(null))

            else -> dispatch(HertzAction.Show(HertzView.LIBRARY))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TuiBg),
    ) {
        if (landscape) {
            val density = LocalDensity.current
            val windowHeightDp = with(density) { windowSize.height.toDp().value }
            val fitScale = (windowHeightDp / 400f).coerceIn(0.85f, 1f)

            FitScaled(fitScale) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(horizontal = 16.dp),
                ) {
                    SideRail(state, dispatch)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        PaneHost(
                            state = state,
                            dispatch = dispatch,
                            onRequestPermission = onRequestPermission,
                            modifier = Modifier.weight(1f),
                        )

                        NoticeLine(state)

                        if (state.nowPlayingId != null && !imeVisible) {
                            MiniPlayer(
                                state = state,
                                dispatch = dispatch,
                                onLongPress = { showQueueSheet = true },
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 16.dp),
            ) {
                Titlebar(state, dispatch)
                TabsRow(state, dispatch)
                PaneHost(
                    state = state,
                    dispatch = dispatch,
                    onRequestPermission = onRequestPermission,
                    modifier = Modifier.weight(1f),
                )

                NoticeLine(state)

                if (state.nowPlayingId != null && !imeVisible) {
                    MiniPlayer(
                        state = state,
                        dispatch = dispatch,
                        onLongPress = { showQueueSheet = true },
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        AnimatedVisibility(
            visible = state.expanded && state.nowPlayingId != null,
            enter = slideInVertically(tween(240)) { it } + fadeIn(tween(180)),
            exit = slideOutVertically(tween(200)) { it } + fadeOut(tween(140)),
        ) {
            ExpandedPlayer(
                state = state,
                dispatch = dispatch,
                onInfo = { showInfoSheet = true },
                onQueue = { showQueueSheet = true },
            )
        }

        if (showQueueSheet) {
            TuiSheet(onDismiss = { showQueueSheet = false }) {
                val position = (state.queueIndex + 1).coerceAtMost(state.queue.size)
                SheetHeader(
                    title = stringResource(R.string.queue_title),
                    meta = "$position/${state.queue.size}",
                )
                QueueList(
                    state = state,
                    dispatch = dispatch,
                    modifier = Modifier.heightIn(max = 420.dp),
                )
            }
        }

        if (showInfoSheet) {
            TuiSheet(onDismiss = { showInfoSheet = false }) {
                SheetHeader(title = stringResource(R.string.track_info))
                InfoContent(state)
            }
        }
    }
}

@Composable
private fun PaneHost(
    state: HertzState,
    dispatch: (HertzAction) -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when {
            state.view == HertzView.STATS -> StatsPane(state, dispatch)
            state.view == HertzView.SETTINGS -> SettingsPane(state, dispatch)
            !state.hasPermission -> PermissionPane(dispatch, onRequestPermission)
            state.scanning -> Caption(stringResource(R.string.scanning))
            state.view == HertzView.LIBRARY -> LibraryPane(state, dispatch)
            state.view == HertzView.ARTISTS -> ArtistsPane(state, dispatch)
            state.view == HertzView.ALBUMS -> AlbumsPane(state, dispatch)
            else -> LibraryPane(state, dispatch)
        }
    }
}

@Composable
private fun SideRail(state: HertzState, dispatch: (HertzAction) -> Unit) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .fillMaxHeight(),
    ) {
        TuiPanel(modifier = Modifier.padding(top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(LocalAccent.current),
                )
                Text(
                    text = " " + stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                    color = TuiBright,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        TuiTab(
            label = stringResource(R.string.tab_library),
            active = state.view == HertzView.LIBRARY,
            modifier = Modifier.fillMaxWidth(),
        ) {
            dispatch(HertzAction.Show(HertzView.LIBRARY))
        }
        Spacer(modifier = Modifier.height(8.dp))
        TuiTab(
            label = stringResource(R.string.tab_artists),
            active = state.view == HertzView.ARTISTS,
            modifier = Modifier.fillMaxWidth(),
        ) {
            dispatch(HertzAction.Show(HertzView.ARTISTS))
        }
        Spacer(modifier = Modifier.height(8.dp))
        TuiTab(
            label = stringResource(R.string.tab_albums),
            active = state.view == HertzView.ALBUMS,
            modifier = Modifier.fillMaxWidth(),
        ) {
            dispatch(HertzAction.Show(HertzView.ALBUMS))
        }

        Spacer(modifier = Modifier.weight(1f))

        val inConfig = state.view == HertzView.SETTINGS || state.view == HertzView.STATS
        TuiTab(
            label = stringResource(R.string.cfg),
            active = inConfig,
            modifier = Modifier.fillMaxWidth(),
        ) {
            dispatch(HertzAction.Show(if (inConfig) HertzView.LIBRARY else HertzView.SETTINGS))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun Titlebar(state: HertzState, dispatch: (HertzAction) -> Unit) {
    TuiPanel(modifier = Modifier.padding(top = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(LocalAccent.current),
            )
            Text(
                text = " " + stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = TuiBright,
                modifier = Modifier.weight(1f),
            )
            val inConfig = state.view == HertzView.SETTINGS || state.view == HertzView.STATS
            TuiTab(
                label = stringResource(R.string.cfg),
                active = inConfig,
            ) {
                dispatch(
                    HertzAction.Show(if (inConfig) HertzView.LIBRARY else HertzView.SETTINGS),
                )
            }
        }
    }
}

@Composable
private fun TabsRow(state: HertzState, dispatch: (HertzAction) -> Unit) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        TuiTab(
            label = stringResource(R.string.tab_library),
            active = state.view == HertzView.LIBRARY,
        ) {
            dispatch(HertzAction.Show(HertzView.LIBRARY))
        }
        Spacer(modifier = Modifier.width(8.dp))
        TuiTab(
            label = stringResource(R.string.tab_artists),
            active = state.view == HertzView.ARTISTS,
        ) {
            dispatch(HertzAction.Show(HertzView.ARTISTS))
        }
        Spacer(modifier = Modifier.width(8.dp))
        TuiTab(
            label = stringResource(R.string.tab_albums),
            active = state.view == HertzView.ALBUMS,
        ) {
            dispatch(HertzAction.Show(HertzView.ALBUMS))
        }
    }
}

@Composable
private fun NoticeLine(state: HertzState) {
    (state.error ?: state.notice)?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.labelSmall,
            color = if (state.error != null) MaterialTheme.colorScheme.error else TuiDim,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp),
        )
    }
}
