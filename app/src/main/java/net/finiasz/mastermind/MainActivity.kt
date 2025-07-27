package net.finiasz.mastermind

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import net.finiasz.mastermind.ui.theme.MastermindTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels<GameViewModel>()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color(color = 0x7f000000).toArgb()
            )
        )
        super.onCreate(savedInstanceState)
        gameViewModel.restore(this)

        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        setContent {
            MastermindTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
                    Plateau()
                }
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        gameViewModel.save(this)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
fun Plateau(gameViewModel : GameViewModel = viewModel()) {
    val state : GameState by gameViewModel.state.collectAsState()
    var reloadConfirmation by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val showReveal : MutableState<Int?> = remember { mutableStateOf(null) }

    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    fun reset() {
        gameViewModel.reset(
            settingsManager.pegCount.intValue,
            settingsManager.colorCount.intValue,
            settingsManager.allowDuplicates.value,
            settingsManager.guessCount.intValue
        )
    }

    LaunchedEffect(Unit) {
        if (gameViewModel.state.value.target == null) {
            reset()
        }
    }

    val configuration = LocalConfiguration.current

    state.target?.let { target ->
        BoxWithConstraints(
            Modifier
                .safeDrawingPadding()
                .fillMaxSize()
        ) {
            val sizes : Sizes = remember(maxWidth, maxHeight, configuration.densityDpi, configuration.fontScale, state.target, state.guesses.size, state.colorCount) {
                val defaultSpaceDp: Dp = maxWidth * .02f
                val buttonSizeDp: Dp = (maxWidth - defaultSpaceDp * 7) / 6f
                val h: Dp = (maxHeight - 24.dp - 4 * defaultSpaceDp - 2 * buttonSizeDp) / (1.5f + state.guesses.size)
                Sizes(
                    landscape = false,
                    defaultSpaceDp = defaultSpaceDp,
                    pegWidthDp = state.target?.let { (maxWidth - 2 * buttonSizeDp - defaultSpaceDp * 4) / it.size }
                        ?: 0.dp,
                    pegHeightDp = h,
                    plateauWidthDp = maxWidth - 2 * defaultSpaceDp,
                    buttonSizeDp = buttonSizeDp,
                    placementsTextSizeSp = (buttonSizeDp.coerceAtMost(h) * .7f / configuration.fontScale).value
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(sizes.defaultSpaceDp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // target
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sizes.pegHeightDp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsButton(
                        height = sizes.buttonSizeDp.coerceAtMost(sizes.pegHeightDp * 1.5f),
                        width = sizes.buttonSizeDp
                    ) {
                        showSettings = true
                    }

                    Spacer(modifier = Modifier.width(sizes.defaultSpaceDp))

                    Box(
                        Modifier
                            .weight(1f, true)
                            .height(sizes.pegHeightDp),
                        contentAlignment = Alignment.Center
                    ) {
                        // background
                        Box(
                            Modifier
                                .width(
                                    (target.size * sizes.pegWidthDp - (sizes.pegWidthDp - sizes.pegHeightDp).coerceAtLeast(
                                        0f.dp
                                    ))
                                )
                                .height(sizes.pegHeightDp.coerceAtMost(sizes.pegWidthDp))
                                .background(
                                    color = when (state.won) {
                                        Won.NOT_WON -> MaterialTheme.colorScheme.primaryContainer
                                        Won.LOST -> MaterialTheme.colorScheme.error
                                        Won.WON -> MaterialTheme.colorScheme.tertiary
                                    },
                                    shape = RoundedCornerShape(
                                        (sizes.pegHeightDp.coerceAtMost(sizes.pegWidthDp) / 2)
                                    )
                                )
                        ) { }


                        // pegs
                        Row {
                            for (i in 0..<(target.size)) {
                                Peg(
                                    sizes = sizes,
                                    color = if (state.won == Won.NOT_WON && state.revealedTargets[i].not()) null else target[i],
                                    selected = false,
                                    colorBlind = settingsManager.colorBlind.value,
                                    pegState = PegState.TARGET
                                ) {
                                    if (state.revealedTargets[i].not()) {
                                        showReveal.value = i
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(sizes.defaultSpaceDp))

                    if (state.won == Won.NOT_WON) {
                        ResetButton(
                            height = sizes.buttonSizeDp.coerceAtMost(sizes.pegHeightDp * 1.5f),
                            width = sizes.buttonSizeDp
                        ) {
                            if (state.won == Won.NOT_WON) {
                                reloadConfirmation = true
                            } else {
                                reset()
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(sizes.buttonSizeDp))
                    }
                }

                // explanations
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((.5 * sizes.pegHeightDp)),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val size = (.5f * sizes.pegHeightDp).coerceAtMost(.7f * sizes.buttonSizeDp)
                    Spacer(modifier = Modifier.width((sizes.buttonSizeDp / 2 - size / 2.5f)))
                    Image(
                        modifier = Modifier
                            .height(size)
                            .aspectRatio(1f)
                            .padding(top = (.4 * size)),
                        painter = painterResource(id = R.drawable.arrow_left_down),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
                    )
                    Text(
                        modifier = Modifier.padding(bottom = (.16 * size)),
                        text = stringResource(id = R.string.exact),
                        fontSize = (sizes.placementsTextSizeSp / 2f).sp,
                        lineHeight = (sizes.placementsTextSizeSp / 2f).sp,
                        color = MaterialTheme.colorScheme.outline,
                    )

                    Spacer(modifier = Modifier.weight(1f, true))


                    Text(
                        modifier = Modifier.padding(bottom = (.16 * size)),
                        text = stringResource(id = R.string.misplaced),
                        fontSize = (sizes.placementsTextSizeSp / 2f).sp,
                        lineHeight = (sizes.placementsTextSizeSp / 2f).sp,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Image(
                        modifier = Modifier
                            .height(size)
                            .aspectRatio(1f)
                            .padding(top = (.4 * size)),
                        painter = painterResource(id = R.drawable.arrow_right_down),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.width((sizes.buttonSizeDp / 2 - size / 2.5f)))
                }


                // rows
                for (row in (state.guesses.size - 1) downTo 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sizes.pegHeightDp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlacementCounter(
                            sizes = sizes,
                            counter = state.exactPlacements[row],
                            exact = true
                        )
                        Spacer(modifier = Modifier.width(sizes.defaultSpaceDp))

                        PegRow(
                            sizes = sizes,
                            colors = state.guesses[row],
                            selected = if (row == state.validatedCount) state.selectedIndex else -1,
                            colorBlind = settingsManager.colorBlind.value,
                            pegState = if (row == state.validatedCount) PegState.ACTIVE else PegState.NONE,
                        ) { col ->
                            gameViewModel.pegClicked(row, col)
                        }
                        Spacer(modifier = Modifier.width(sizes.defaultSpaceDp))

                        PlacementCounter(
                            sizes = sizes,
                            counter = state.badPlacements[row],
                            exact = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(sizes.defaultSpaceDp))

                // colors
                if (state.won == Won.NOT_WON) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f, true)
                                .height((sizes.defaultSpaceDp + 2 * sizes.buttonSizeDp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(sizes.buttonSizeDp),
                                horizontalArrangement = spacedBy(sizes.defaultSpaceDp)
                            ) {
                                for (i in 0..<(state.colorCount + 1) / 2) {
                                    ColorButton(
                                        modifier = Modifier
                                            .height(sizes.buttonSizeDp)
                                            .weight(1f, true),
                                        color = i,
                                        colorBlind = settingsManager.colorBlind.value
                                    ) {
                                        gameViewModel.colorClicked(i)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(sizes.defaultSpaceDp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(sizes.buttonSizeDp),
                                horizontalArrangement = spacedBy(sizes.defaultSpaceDp)
                            ) {
                                for (i in (state.colorCount + 1) / 2..<state.colorCount) {
                                    ColorButton(
                                        modifier = Modifier
                                            .height(sizes.buttonSizeDp)
                                            .weight(1f, true),
                                        color = i,
                                        colorBlind = settingsManager.colorBlind.value
                                    ) {
                                        gameViewModel.colorClicked(i)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(sizes.defaultSpaceDp))

                        ValidateButton(sizes = sizes, validateEnabled = state.validateEnabled) {
                            gameViewModel.validate()
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((sizes.defaultSpaceDp + 2 * sizes.buttonSizeDp))
                    ) {
                        val textSize =
                            (sizes.buttonSizeDp * .7f / LocalConfiguration.current.fontScale).value
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(sizes.buttonSizeDp),
                            text = stringResource(id = if (state.won == Won.WON) R.string.well_done else R.string.game_over),
                            fontSize = textSize.sp,
                            lineHeight = textSize.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            color = if (state.won == Won.WON) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(sizes.defaultSpaceDp))

                        ResetButton(
                            height = sizes.buttonSizeDp,
                            width = sizes.plateauWidthDp
                        ) {
                            reset()
                        }
                    }
                }
            }
        }
    }


    if (reloadConfirmation) {
        BasicAlertDialog(
            onDismissRequest = {
                reloadConfirmation = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.reload_confirmation),
                    fontSize = TextUnit(24f, TextUnitType.Sp),
                    lineHeight = TextUnit(32f, TextUnitType.Sp),
                )
                Row(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            reloadConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            fontSize = TextUnit(24f, TextUnitType.Sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            reloadConfirmation = false
                            reset()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = stringResource(id = R.string.ok),
                            fontSize = TextUnit(24f, TextUnitType.Sp),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    }

    showReveal.value?.let {position ->
        BasicAlertDialog(
            onDismissRequest = {
                showReveal.value = null
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.reveal_peg),
                    fontSize = TextUnit(24f, TextUnitType.Sp),
                    lineHeight = TextUnit(32f, TextUnitType.Sp),
                )
                Row(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            showReveal.value = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            fontSize = TextUnit(24f, TextUnitType.Sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            gameViewModel.reveal(pos = position)
                            showReveal.value = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            text = stringResource(id = R.string.ok),
                            fontSize = TextUnit(24f, TextUnitType.Sp),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    }

    if (showSettings) {
        SettingsDialog(settingsManager = settingsManager) { changed ->
            showSettings = false
            if (changed) {
                if (gameViewModel.state.value.won == Won.NOT_WON) {
                    reloadConfirmation = true
                } else {
                    reset()
                }
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun PegRow(sizes: Sizes, colors: List<Int?>, selected: Int, colorBlind: Boolean, pegState: PegState, click: (Int) -> Unit) {
    Row {
        for (col in 0..<(colors.size)) {
            Peg(
                sizes = sizes,
                color = colors[col],
                selected = col == selected,
                colorBlind = colorBlind,
                pegState = pegState
            ) {
                click(col)
            }
        }
    }
}


@Composable
fun Peg(sizes: Sizes, color: Int?, selected: Boolean, colorBlind: Boolean, pegState: PegState, click: () -> Unit) {
    Box(
        modifier = Modifier
            .height(sizes.pegHeightDp)
            .width(sizes.pegWidthDp)
            .padding(
                vertical = ((sizes.pegHeightDp - sizes.pegWidthDp) / 2f).coerceAtLeast(0.dp),
                horizontal = ((sizes.pegWidthDp - sizes.pegHeightDp) / 2f).coerceAtLeast(0.dp)
            )
            .then(
                if (selected)
                    if (color == null || colorBlind.not())
                        Modifier.border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape
                        )
                    else
                        Modifier.background(
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape
                        )
                else
                    Modifier
            )
            .padding(if (color == null || colorBlind.not()) 3.dp else 0.dp)
            .then(
                if (pegState != PegState.NONE) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { click() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (color == null || colorBlind.not()) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (pegState == PegState.NONE && color == null) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape
                    )
                    .background(getPegColor(color = color)),
                contentAlignment = Alignment.TopCenter
            ) {
                if (pegState == PegState.TARGET && color == null) {
                    val sizeSp =
                        (sizes.pegHeightDp.coerceAtMost(sizes.pegWidthDp) * .65f / (LocalConfiguration.current.fontScale)).value
                    Text(
                        text = "?",
                        fontSize = sizeSp.sp,
                        lineHeight = sizeSp.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Image(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize(),
                painter = painterResource(id = getPegDrawableId(color)),
                contentDescription = null,
                colorFilter = if (MaterialTheme.colorScheme.surface == Color.Black) {
                    if (color == 9 && selected)
                        ColorFilter.tint(color = Color.Black)
                    else {
                        val pegColor = getPegColor(color)
                        // we compute a mix of inverse and color multiply --> black becomes white, white becomes the pegColor
                        ColorFilter.colorMatrix(
                            ColorMatrix(
                                floatArrayOf(
                                    pegColor.red - 0.8666f, 0f, 0f, 0f, 221f,
                                    0f, pegColor.green - 0.8666f, 0f, 0f, 221f,
                                    0f, 0f, pegColor.blue - 0.8666f, 0f, 221f,
                                    0f, 0f, 0f, 1f, 0f
                                )
                            )
                        )
                    }
                } else {
                    ColorFilter.tint(color = getPegColor(if (color == 8 && selected) 9 else color), blendMode = BlendMode.Modulate)
                }
            )
        }
    }
}

enum class PegState {
    TARGET,
    ACTIVE,
    NONE
}

@Composable
fun PlacementCounter(sizes : Sizes, counter : Int?, exact : Boolean) {
    val size = sizes.buttonSizeDp.coerceAtMost(sizes.pegHeightDp) - 4.dp
    counter?.let {
        Text(
            modifier = Modifier
                .width(sizes.buttonSizeDp)
                .height(size)
                .background(
                    color = if (exact) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(top = (size*.05f)),
            text = "$it",
            textAlign = TextAlign.Center,
            fontSize = sizes.placementsTextSizeSp.sp,
            lineHeight = sizes.placementsTextSizeSp.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondary
        )
    } ?: Box (
        modifier = Modifier
            .width(sizes.buttonSizeDp)
            .height(size)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
    ) { }
}

@Composable
fun ColorButton(modifier : Modifier, color : Int, colorBlind: Boolean, click : () -> Unit) {
    Box (
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = getPegColor(color))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { click() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (colorBlind) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = getPegDrawableId(color)),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = getPegColor(if (color == 8) 9 else color), blendMode = BlendMode.Modulate)
            )
        }
    }
}


fun getPegColor(color: Int?) : Color = when(color) {
    null -> Color.Transparent
    0 -> Color(0xffe80035)
    1 -> Color(0xff61ca34)
    2 -> Color(0xff954cff)
    3 -> Color(0xff61b4fc)
    4 -> Color(0xffedc300)
    5 -> Color(0xfff8adcc)
    6 -> Color(0xff985800)
    7 -> Color(0xff7f7f7f)
    8 -> Color(0xff000000)
    else -> Color(0xffffffff)
}

@DrawableRes
fun getPegDrawableId(color: Int) : Int = when(color) {
    0 -> R.drawable.shape_0
    1 -> R.drawable.shape_1
    2 -> R.drawable.shape_2
    3 -> R.drawable.shape_3
    4 -> R.drawable.shape_4
    5 -> R.drawable.shape_5
    6 -> R.drawable.shape_6
    7 -> R.drawable.shape_7
    8 -> R.drawable.shape_8
    else -> R.drawable.shape_9
}

@Composable
fun SettingsButton(height: Dp, width: Dp, click: () -> Unit) {
    Image(painter = painterResource(id = R.drawable.settings),
        contentDescription = null,
        modifier = Modifier
            .width(width)
            .height(height)
            .border(
                2.dp,
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                click()
            }
            .padding(height / 8),
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primaryContainer),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun ResetButton(height: Dp, width: Dp, click: () -> Unit) {
    Image(painter = painterResource(id = R.drawable.reset),
        contentDescription = null,
        modifier = Modifier
            .width(width)
            .height(height)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.error)
            .clickable {
                click()
            }
            .padding(height / 8),
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onError),
        contentScale = ContentScale.Fit
    )
}


@Composable
fun ValidateButton(sizes: Sizes, validateEnabled: Boolean, click: () -> Unit) {
    Image(painter = painterResource(id = R.drawable.validate),
        contentDescription = null,
        modifier = Modifier
            .alpha(if (validateEnabled) 1f else .25f)
            .width(sizes.buttonSizeDp)
            .height((2 * sizes.buttonSizeDp + sizes.defaultSpaceDp))
            .border(3.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.tertiary)
            .clickable(enabled = validateEnabled) { click() }
            .padding((sizes.buttonSizeDp / 8)),
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onTertiary),
        contentScale = ContentScale.Fit
    )
}



data class Sizes(
    val landscape: Boolean,
    val defaultSpaceDp: Dp,
    val pegHeightDp: Dp,
    val pegWidthDp: Dp,
    val buttonSizeDp: Dp,
    val plateauWidthDp: Dp,
    val placementsTextSizeSp: Float,
)


@Preview
@Composable
fun PlateauPreview() {
    MastermindTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Plateau()
        }
    }
}

