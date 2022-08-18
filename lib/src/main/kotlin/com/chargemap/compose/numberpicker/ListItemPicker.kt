package com.chargemap.compose.numberpicker

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.chargemap.extensions.getElementAtOffsetIndexValue
import com.chargemap.extensions.getNWithinBounds
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun <T> ListItemPicker(
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    value: T,
    onValueChange: (T) -> Unit,
    dividersColor: Color = MaterialTheme.colors.primary,
    list: List<T>,
    textStyle: TextStyle = LocalTextStyle.current,
    itemWidth: Dp = 67.dp,
    itemHeight: Dp = 67.dp,
    itemVerticalPadding: Dp = 22.dp,
    spacingFactor: Float = 1f,
    dividersHeight: Dp = 2.dp
) {
    val spacing = itemHeight * spacingFactor
    val spacingFactorPx = with(LocalDensity.current) { spacing.toPx() }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffset = remember { Animatable(0f) }
    val coercedAnimatedOffset = animatedOffset.value % spacingFactorPx

    // Flicker fixing vars
    var offset by remember { mutableStateOf(animatedOffset.value) }
    var prevValue by remember {
        mutableStateOf(list.getElementAtOffsetIndexValue(-1,
            list.indexOf(value)))
    }

    // Haptic feedback vars
    var prevAnimatedOffset by remember { mutableStateOf(0f) }
    var hapticFeedbackTriggered by remember { mutableStateOf(true) }
    var isDragging by remember { mutableStateOf(false) }


    if (animatedOffset.value != 0f) {
        offset = animatedOffset.value
    } else if (prevValue != value) {
        offset = 0f
        prevValue = value
    }

    val indexOfElement = getItemIndexForOffset(list, value, offset, spacingFactorPx)
    var prevIndexOfElement by remember { mutableStateOf(indexOfElement) }


    Layout(
        modifier = modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStarted = {
                    isDragging = true
                    // Prevent triggering haptic feedback when initial view is in the middle
                    hapticFeedbackTriggered = true
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % spacingFactorPx
                                val coercedAnchors =
                                    listOf(-spacingFactorPx, 0f, spacingFactorPx)
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base = spacingFactorPx * (target / spacingFactorPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        val result = list.elementAt(
                            getItemIndexForOffset(list, value, endValue, spacingFactorPx)
                        )
                        // Only change value when done with fling
                        onValueChange(result)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
            .padding(vertical = itemHeight / 3 + (itemVerticalPadding * 1.5f)),
        content = {
            Box(
                modifier
                    .width(itemWidth)
                    .height(dividersHeight)
                    .background(color = dividersColor)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(itemWidth)
                    .padding(vertical = itemVerticalPadding)
                    .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
            ) {
                val dragOffset =
                    if (coercedAnimatedOffset < 0) coercedAnimatedOffset * -1 else coercedAnimatedOffset

                val dragPercentage = coercedAnimatedOffset / spacingFactorPx
                val deltaAlpha = 0.5f * dragPercentage

                val nm2Alpha =
                    if (coercedAnimatedOffset < 0) 0f else maxOf(-0.5f + (deltaAlpha * 2), 0f)
                val nm1Alpha = if (coercedAnimatedOffset < 0) maxOf(0.5f + (deltaAlpha * 2),
                    0f) else 0.5f + deltaAlpha

                val n0Alpha = 1 - (0.5f * ((dragOffset / spacingFactorPx)))

                val n1Alpha = if (coercedAnimatedOffset > 0) maxOf(0.5f - (deltaAlpha * 2),
                    0f) else 0.5f - deltaAlpha
                val n2Alpha =
                    if (coercedAnimatedOffset > 0) 0f else maxOf(-0.5f - (deltaAlpha * 2), 0f)

                if (prevIndexOfElement != indexOfElement) {
                    val animationOffSetDiff = animatedOffset.value - prevAnimatedOffset

                    // Prevent multiple haptic feedback when swiping a value over the center
                    if (!(-20 < animationOffSetDiff && animationOffSetDiff < 20) || (prevAnimatedOffset == 0f && animatedOffset.value == 0f)) {
                        LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    hapticFeedbackTriggered = false
                    prevIndexOfElement = indexOfElement
                    prevAnimatedOffset = animatedOffset.value
                } else if (isDragging && indexOfElement == list.indexOf(value) && !hapticFeedbackTriggered && dragOffset < 5f) {
                    LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.LongPress)
                    hapticFeedbackTriggered = true
                }




                ProvideTextStyle(textStyle) {
                    DisableSelection {
                        Label(
                            text = label(list.getElementAtOffsetIndexValue(-2, indexOfElement)),
                            modifier = Modifier
                                .offset(y = -spacing * 2)
                                .alpha(nm2Alpha)
                        )
                        Label(
                            text = label(list.getElementAtOffsetIndexValue(-1, indexOfElement)),
                            modifier = Modifier
                                .offset(y = -spacing)
                                .alpha(nm1Alpha)
                        )
                        Label(
                            text = label(list.elementAt(indexOfElement)),
                            modifier = Modifier
                                .alpha(n0Alpha)
                        )
                        Label(
                            text = label(list.getElementAtOffsetIndexValue(1, indexOfElement)),
                            modifier = Modifier
                                .offset(y = spacing)
                                .alpha(n1Alpha)
                        )
                        Label(
                            text = label(list.getElementAtOffsetIndexValue(2, indexOfElement)),
                            modifier = Modifier
                                .offset(y = spacing * 2)
                                .alpha(n2Alpha)
                        )
                    }
                }
            }
            Box(
                modifier
                    .width(itemWidth)
                    .height(dividersHeight)
                    .background(color = dividersColor)
            )
        }
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }

        // Set the size of the layout as big as it can
        layout(
            itemWidth.toPx().toInt(),
            placeables.sumOf {
                it.height
            }
        ) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->

                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

@Composable
private fun Label(text: String, modifier: Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)
    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}

private fun <T> getItemIndexForOffset(
    range: List<T>,
    value: T,
    offset: Float,
    halfNumbersColumnHeightPx: Float
): Int {
    val indexOf = range.indexOf(value) - (offset / halfNumbersColumnHeightPx).toInt()
    val indexOfInBounds = getNWithinBounds(indexOf, range.size)

    return when {
        indexOfInBounds <= -1 -> range.size + indexOfInBounds
        indexOfInBounds >= range.size -> indexOfInBounds - range.size
        else -> indexOfInBounds
    }
}
