package fey.hertzmusic.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fey.hertzmusic.BuildConfig
import fey.hertzmusic.R
import fey.hertzmusic.core.base.BaseViewModel
import fey.hertzmusic.core.common.Caption
import fey.hertzmusic.core.common.Hairline
import fey.hertzmusic.core.common.TuiKey
import fey.hertzmusic.core.common.TuiPanel
import fey.hertzmusic.core.common.tuiClickable
import fey.hertzmusic.presentation.player.HertzAction
import fey.hertzmusic.presentation.player.HertzState
import fey.hertzmusic.presentation.player.HertzView
import fey.hertzmusic.ui.theme.LocalAccent
import fey.hertzmusic.ui.theme.TuiBg
import fey.hertzmusic.ui.theme.TuiDim
import fey.hertzmusic.ui.theme.TuiFaint
import fey.hertzmusic.ui.theme.TuiFg
import fey.hertzmusic.ui.theme.TuiLine
import androidx.compose.foundation.text.BasicTextField

private val COVER_COLS_STEPS = listOf(48, 64, 80)

@Composable
fun SettingsPane(state: HertzState, dispatch: (HertzAction) -> Unit) {
    val settings = state.settings
    val on = stringResource(R.string.on)
    val off = stringResource(R.string.off)

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Caption(stringResource(R.string.config))

        SettingRow(
            label = stringResource(R.string.set_wave),
            value = if (settings.wave) on else off,
        ) {
            dispatch(HertzAction.Config(settings.copy(wave = !settings.wave)))
        }
        SettingRow(
            label = stringResource(R.string.set_detail),
            value = stringResource(R.string.set_detail_value, settings.cols),
        ) {
            val currentIndex = COVER_COLS_STEPS.indexOf(settings.cols)
            val next = COVER_COLS_STEPS[(currentIndex + 1).mod(COVER_COLS_STEPS.size)]
            dispatch(HertzAction.Config(settings.copy(cols = next)))
        }
        SettingRow(
            label = stringResource(R.string.set_raw),
            value = if (settings.rawArt) on else off,
        ) {
            dispatch(HertzAction.Config(settings.copy(rawArt = !settings.rawArt)))
        }
        SettingRow(
            label = stringResource(R.string.set_specs),
            value = if (settings.listSpecs) on else off,
        ) {
            dispatch(HertzAction.Config(settings.copy(listSpecs = !settings.listSpecs)))
        }
        SettingRow(
            label = stringResource(R.string.set_accent),
            value = settings.accent.label,
        ) {
            dispatch(
                HertzAction.Config(
                    settings.copy(accent = settings.accent.next()),
                ),
            )
        }
        SettingRow(
            label = stringResource(R.string.set_online),
            value = if (settings.onlineMode) on else off,
        ) {
            dispatch(HertzAction.Config(settings.copy(onlineMode = !settings.onlineMode)))
        }
        SettingRow(
            label = stringResource(R.string.set_server),
            value = settings.serverUrl.takeIf { it.isNotBlank() } ?: "---",
        ) {
            dispatch(HertzAction.ShowUrlDialog(true))
        }
        Caption(stringResource(R.string.tools))
        SettingRow(
            label = stringResource(R.string.set_eq),
            value = stringResource(R.string.set_eq_open),
        ) {
            dispatch(HertzAction.OpenEqualizer)
        }
        SettingRow(
            label = stringResource(R.string.stats),
            value = stringResource(R.string.stat_view),
        ) {
            dispatch(HertzAction.Show(HertzView.STATS))
        }
        SettingRow(
            label = stringResource(R.string.set_rescan),
            value = stringResource(R.string.run),
        ) {
            dispatch(HertzAction.Rescan)
        }

        Caption(stringResource(R.string.about))
        Text(
            text = stringResource(R.string.about_title),
            style = MaterialTheme.typography.bodyMedium,
            color = TuiFg,
        )
        Text(
            text = stringResource(R.string.about_body),
            style = MaterialTheme.typography.labelSmall,
            color = TuiDim,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelSmall,
            color = TuiFaint,
            modifier = Modifier.padding(top = 6.dp),
        )

        val uriHandler = LocalUriHandler.current
        val creditUrl = stringResource(R.string.credit_url)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 12.dp)
                .tuiClickable { runCatching { uriHandler.openUri(creditUrl) } },
        ) {
            Text(
                text = "▪ ",
                style = MaterialTheme.typography.labelMedium,
                color = LocalAccent.current,
            )
            Text(
                text = stringResource(R.string.credit),
                style = MaterialTheme.typography.labelMedium,
                color = TuiDim,
            )
            Text(
                text = " ↗",
                style = MaterialTheme.typography.labelMedium,
                color = LocalAccent.current,
            )
        }

        val feyUrl = stringResource(R.string.fey_url)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .tuiClickable { runCatching { uriHandler.openUri(feyUrl) } },
        ) {
            Text(
                text = "▪ ",
                style = MaterialTheme.typography.labelMedium,
                color = LocalAccent.current,
            )
            Text(
                text = stringResource(R.string.fey),
                style = MaterialTheme.typography.labelMedium,
                color = TuiDim,
            )
            Text(
                text = " ↗",
                style = MaterialTheme.typography.labelMedium,
                color = LocalAccent.current,
            )
        }
    }

    if (state.showUrlDialog) {
        UrlDialog(
            current = settings.serverUrl,
            onDismiss = { dispatch(HertzAction.ShowUrlDialog(false)) },
            onSave = {
                dispatch(HertzAction.Config(settings.copy(serverUrl = it)))
                dispatch(HertzAction.ShowUrlDialog(false))
            },
        )
    }
}

@Composable
fun UrlDialog(
    current: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var text by remember { mutableStateOf(current) }
    val accent = LocalAccent.current

    Dialog(onDismissRequest = onDismiss) {
        TuiPanel {
            Text(
                text = stringResource(R.string.set_url_title),
                style = MaterialTheme.typography.labelMedium,
                color = TuiDim,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TuiFg),
                cursorBrush = SolidColor(accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                decorationBox = { inner ->
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.set_url_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TuiFaint,
                        )
                    }
                    inner()
                },
            )
            Hairline(fraction = 1f, modifier = Modifier.padding(bottom = 20.dp))
            TuiKey(
                label = stringResource(R.string.set_url_save),
                modifier = Modifier.align(Alignment.End),
                onClick = { onSave(text) },
            )
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = TuiFg,
            )
            TuiKey(
                label = "[ $value ]",
                onClick = onClick,
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
