package net.finiasz.mastermind

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import net.finiasz.mastermind.ui.theme.MastermindTheme
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(settingsManager: SettingsManager, dismissCallback: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            dismissCallback.invoke()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {

            Column(
                modifier = Modifier
                    .weight(1f, false)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // number of pegs
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.weight(1f, true)
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_number_of_pegs),
                            fontSize = 24.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = (settingsManager.pegCount.value - 4) / 4f,
                            onValueChange = {
                                settingsManager.setPegCount((it * 4 + 4).roundToInt())
                            },
                            onValueChangeFinished = {
                                if (settingsManager.pegCount.value > settingsManager.colorCount.value) {
                                    settingsManager.setColorCount(settingsManager.pegCount.value)
                                }
                            },
                            steps = 3,
                            colors = SliderDefaults.colors(
                                activeTickColor = MaterialTheme.colorScheme.outline,
                                inactiveTickColor = MaterialTheme.colorScheme.outline,
                                inactiveTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .width(72.dp)
                            .height(80.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = settingsManager.pegCount.value.toString(),
                            fontSize = 42.sp,
                        )
                    }
                }



                // number of colors
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.weight(1f, true)
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_number_of_colors),
                            fontSize = 24.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = (settingsManager.colorCount.value - 4) / 6f,
                            onValueChange = {
                                settingsManager.setColorCount((it * 6 + 4).roundToInt())
                            },
                            onValueChangeFinished = {
                                if (settingsManager.pegCount.value > settingsManager.colorCount.value) {
                                    settingsManager.setPegCount(settingsManager.colorCount.value)
                                }
                            },
                            steps = 5,
                            colors = SliderDefaults.colors(
                                activeTickColor = MaterialTheme.colorScheme.outline,
                                inactiveTickColor = MaterialTheme.colorScheme.outline,
                                inactiveTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .width(72.dp)
                            .height(80.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = settingsManager.colorCount.value.toString(),
                            fontSize = 42.sp,
                        )
                    }
                }


                // number of guesses
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.weight(1f, true)
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_number_of_guesses),
                            fontSize = 24.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = (settingsManager.guessCount.value - 5) / 15f,
                            onValueChange = {
                                settingsManager.setGuessCount((it * 15 + 5).roundToInt())
                            },
                            steps = 14,
                            colors = SliderDefaults.colors(
                                activeTickColor = MaterialTheme.colorScheme.outline,
                                inactiveTickColor = MaterialTheme.colorScheme.outline,
                                inactiveTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .width(72.dp)
                            .height(80.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = settingsManager.guessCount.value.toString(),
                            fontSize = 42.sp,
                        )
                    }
                }

                // allow duplicates
                val interactionSourceDuplicates = remember {
                    MutableInteractionSource()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interactionSourceDuplicates,
                            indication = null,
                            role = Role.Switch
                        )
                        {
                            settingsManager.setAllowDuplicates(settingsManager.allowDuplicates.value.not())
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = settingsManager.allowDuplicates.value,
                        onCheckedChange = { settingsManager.setAllowDuplicates(it) },
                        modifier = Modifier.padding(end = 16.dp),
                        colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.outlineVariant),
                        interactionSource = interactionSourceDuplicates
                    )
                    Text(
                        text = stringResource(id = R.string.settings_allow_duplicates),
                        fontSize = 24.sp,
                        modifier = Modifier.weight(weight = 1f, fill = true)
                    )
                }


                // colorblind mode
                val interactionSourceColorBlind = remember {
                    MutableInteractionSource()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interactionSourceColorBlind,
                            indication = null,
                            role = Role.Switch
                        )
                        {
                            settingsManager.setColorBlind(settingsManager.colorBlind.value.not())
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = settingsManager.colorBlind.value,
                        onCheckedChange = { settingsManager.setColorBlind(it) },
                        modifier = Modifier.padding(end = 16.dp),
                        colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.outlineVariant),
                        interactionSource = interactionSourceColorBlind
                    )
                    Text(
                        text = stringResource(id = R.string.settings_color_blind_mode),
                        fontSize = 24.sp,
                        modifier = Modifier.weight(weight = 1f, fill = true)
                    )
                }

            }


            // ok button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        dismissCallback.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = stringResource(id = R.string.ok),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsPreview() {
    MastermindTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            SettingsDialog(settingsManager = SettingsManager(LocalContext.current)) {
            }
        }
    }
}