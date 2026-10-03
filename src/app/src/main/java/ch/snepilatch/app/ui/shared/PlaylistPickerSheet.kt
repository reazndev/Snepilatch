package ch.snepilatch.app.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.snepilatch.app.R
import ch.snepilatch.app.data.LibraryItem
import ch.snepilatch.app.ui.theme.SnepilatchElevated
import ch.snepilatch.app.ui.theme.SnepilatchGray
import ch.snepilatch.app.ui.theme.SnepilatchLightGray
import ch.snepilatch.app.ui.theme.SnepilatchWhite

/** Bottom sheet for selecting one or more playlists for the pending track list. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistPickerSheet(
    playlists: List<LibraryItem>,
    onSave: (List<LibraryItem>) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
    )
    var selectedUris by remember { mutableStateOf<Set<String>>(emptySet()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        sheetMaxWidth = Dp.Unspecified,
        containerColor = SnepilatchElevated,
        dragHandle = { SheetDragHandle() },
    ) {
        SheetNavBarFix()
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.85f)) {
            Text(
                stringResource(R.string.add_to_playlist),
                color = SnepilatchWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )

            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            ) {
                itemsIndexed(playlists, key = { _, playlist -> playlist.uri }) { index, playlist ->
                    val selected = playlist.uri in selectedUris
                    PlaylistPickerRow(
                        playlist = playlist,
                        selected = selected,
                        hasSelectedAbove = index > 0 && playlists[index - 1].uri in selectedUris,
                        hasSelectedBelow = index < playlists.lastIndex && playlists[index + 1].uri in selectedUris,
                        onClick = {
                            selectedUris = if (selected) selectedUris - playlist.uri else selectedUris + playlist.uri
                        },
                    )
                }
            }

            Button(
                onClick = { onSave(playlists.filter { it.uri in selectedUris }) },
                enabled = selectedUris.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
private fun PlaylistPickerRow(
    playlist: LibraryItem,
    selected: Boolean,
    hasSelectedAbove: Boolean,
    hasSelectedBelow: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                if (selected) SnepilatchGray else Color.Transparent,
                RoundedCornerShape(
                    topStart = if (hasSelectedAbove) 0.dp else 8.dp,
                    topEnd = if (hasSelectedAbove) 0.dp else 8.dp,
                    bottomStart = if (hasSelectedBelow) 0.dp else 8.dp,
                    bottomEnd = if (hasSelectedBelow) 0.dp else 8.dp,
                ),
            )
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() })
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpfyImage(
            url = playlist.imageUrl,
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(6.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                playlist.name,
                color = SnepilatchWhite,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            playlist.owner?.let {
                Text(
                    it,
                    color = SnepilatchLightGray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Icon(
            imageVector = if (selected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary else SnepilatchLightGray,
            modifier = Modifier.size(24.dp),
        )
    }
}
