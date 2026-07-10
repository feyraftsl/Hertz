package fey.hertzmusic.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fey.hertzmusic.R
import fey.hertzmusic.core.common.Caption
import fey.hertzmusic.core.common.TuiKey
import fey.hertzmusic.presentation.player.HertzAction
import fey.hertzmusic.ui.theme.TuiFg

@Composable
fun PermissionPane(dispatch: (HertzAction) -> Unit, onRequestPermission: () -> Unit) {
    Column {
        Caption(stringResource(R.string.permission_title))
        Text(
            text = stringResource(R.string.permission_body),
            style = MaterialTheme.typography.bodyMedium,
            color = TuiFg,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Row {
            TuiKey(
                label = stringResource(R.string.grant),
                bright = true,
                onClick = onRequestPermission,
            )
            Spacer(modifier = Modifier.width(10.dp))
            TuiKey(stringResource(R.string.rescan)) { dispatch(HertzAction.Rescan) }
        }
    }
}
