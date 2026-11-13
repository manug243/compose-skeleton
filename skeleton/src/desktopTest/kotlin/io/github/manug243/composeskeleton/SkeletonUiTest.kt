package io.github.manug243.composeskeleton

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SkeletonUiTest {

    // region Drawing

    @Test
    fun enabledHost_drawsPlaceholderPerMode() = runComposeUiTest {
        var mode by mutableStateOf(SkeletonMode.Shimmer)
        setContent { SingleElement(enabled = true, mode = mode, animationEnabled = false) }

        assertCenterColor(Base, "Shimmer without animation")
        mode = SkeletonMode.Solid
        assertCenterColor(Base, "Solid")
        mode = SkeletonMode.Keep
        assertCenterColor(Content, "Keep")
        mode = SkeletonMode.Hide
        assertCenterColor(Background, "Hide")
    }

    @Test
    fun disabledHost_drawsContentForEveryMode() = runComposeUiTest {
        var mode by mutableStateOf(SkeletonMode.Shimmer)
        setContent { SingleElement(enabled = false, mode = mode) }

        for (each in listOf(SkeletonMode.Shimmer, SkeletonMode.Solid, SkeletonMode.Keep, SkeletonMode.Hide)) {
            mode = each
            assertCenterColor(Content, each.toString())
        }
    }

    @Test
    fun togglingEnabled_switchesBetweenPlaceholderAndContent() = runComposeUiTest {
        var enabled by mutableStateOf(true)
        setContent { SingleElement(enabled = enabled, mode = SkeletonMode.Solid) }

        assertCenterColor(Base, "enabled")
        enabled = false
        assertCenterColor(Content, "disabled")
        enabled = true
        assertCenterColor(Base, "enabled again")
    }

    @Test
    fun animatedShimmer_drawsGradientInsteadOfContent() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent { SingleElement(enabled = true, mode = SkeletonMode.Shimmer, animationEnabled = true) }
        mainClock.advanceTimeBy(ShimmerStyle.animationDurationMillis / 2L)

        val pixel = centerPixel()
        assertTrue(pixel.green < 0.1f, "Expected shimmer colors, got $pixel")
        assertTrue(pixel.red + pixel.blue > 0.9f, "Expected shimmer colors, got $pixel")
    }

    @Test
    fun animationDisabled_drawsStaticBaseColor() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent { SingleElement(enabled = true, mode = SkeletonMode.Shimmer, animationEnabled = false) }

        assertCenterColor(Base, "initial frame")
        mainClock.advanceTimeBy(ShimmerStyle.animationDurationMillis / 2L)
        assertCenterColor(Base, "after half a sweep")
    }

    @Test
    fun skeletonOutsideHost_isNoOp() = runComposeUiTest {
        var clicks = 0
        setContent {
            Box(Modifier.testTag(HostTag).background(Background)) {
                Box(
                    Modifier
                        .size(ElementSize)
                        .background(Content)
                        .clickable { clicks++ }
                        .skeleton(SkeletonMode.Solid),
                ) {
                    BasicText("label")
                }
            }
        }

        assertCenterColor(Content, "outside host")
        onNodeWithText("label").assertExists()
        onNodeWithTag(HostTag).performTouchInput { click(center) }
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun nestedHosts_innerHostWins() = runComposeUiTest {
        var outerEnabled by mutableStateOf(true)
        var innerEnabled by mutableStateOf(false)
        setContent {
            Box(Modifier.testTag(HostTag).background(Background)) {
                SkeletonHost(enabled = outerEnabled, style = SolidStyle, animationEnabled = false) {
                    SkeletonHost(enabled = innerEnabled, style = SolidStyle, animationEnabled = false) {
                        Box(Modifier.size(ElementSize).background(Content).skeleton(SkeletonMode.Solid))
                    }
                }
            }
        }

        assertCenterColor(Content, "outer enabled, inner disabled")
        outerEnabled = false
        innerEnabled = true
        assertCenterColor(Base, "outer disabled, inner enabled")
    }

    @Test
    fun localSkeletonStyle_isUsedWhenNoStyleIsPassed() = runComposeUiTest {
        val provided = SolidStyle.copy(baseColor = Color.Magenta)
        setContent {
            CompositionLocalProvider(LocalSkeletonStyle provides provided) {
                Box(Modifier.testTag(HostTag).background(Background)) {
                    SkeletonHost(enabled = true, animationEnabled = false) {
                        Box(Modifier.size(ElementSize).background(Content).skeleton(SkeletonMode.Solid))
                    }
                }
            }
        }

        assertCenterColor(Color.Magenta, "LocalSkeletonStyle")
    }

    @Test
    fun explicitStyle_winsOverLocalSkeletonStyle() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalSkeletonStyle provides SolidStyle.copy(baseColor = Color.Magenta)) {
                SingleElement(enabled = true, mode = SkeletonMode.Solid, animationEnabled = false)
            }
        }

        assertCenterColor(Base, "explicit style")
    }

    @Test
    fun defaultStyle_followsDarkThemeParameter() = runComposeUiTest {
        var light: SkeletonStyle? = null
        var dark: SkeletonStyle? = null
        setContent {
            light = SkeletonDefaults.style(darkTheme = false)
            dark = SkeletonDefaults.style(darkTheme = true)
        }

        runOnIdle {
            assertEquals(Color(0xFFE3E6EA), light?.baseColor)
            assertEquals(Color(0xFFF5F6F8), light?.highlightColor)
            assertEquals(Color(0xFF30343A), dark?.baseColor)
            assertEquals(Color(0xFF494E56), dark?.highlightColor)
        }
    }

    @Test
    fun elementColors_overrideHostColors() = runComposeUiTest {
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Box(
                    Modifier
                        .size(ElementSize)
                        .background(Content)
                        .skeleton(SkeletonMode.Solid, colors = SkeletonColors(Color.Cyan, Color.Cyan)),
                )
            }
        }

        assertCenterColor(Color.Cyan, "element colors")
    }

    // endregion

    // region Semantics

    @Test
    fun semantics_clearedForPlaceholderModes_keptForKeep() = runComposeUiTest {
        var enabled by mutableStateOf(true)
        setContent {
            Host(enabled = enabled, animationEnabled = false) {
                Column {
                    BasicText("shimmer", Modifier.skeleton(SkeletonMode.Shimmer))
                    BasicText("solid", Modifier.skeleton(SkeletonMode.Solid))
                    BasicText("hide", Modifier.skeleton(SkeletonMode.Hide))
                    BasicText("keep", Modifier.skeleton(SkeletonMode.Keep))
                }
            }
        }

        onNodeWithText("shimmer", useUnmergedTree = false).assertDoesNotExist()
        onNodeWithText("solid", useUnmergedTree = false).assertDoesNotExist()
        onNodeWithText("hide", useUnmergedTree = false).assertDoesNotExist()
        onNodeWithText("keep", useUnmergedTree = false).assertExists()

        enabled = false
        onNodeWithText("shimmer", useUnmergedTree = false).assertExists()
        onNodeWithText("solid", useUnmergedTree = false).assertExists()
        onNodeWithText("hide", useUnmergedTree = false).assertExists()
        onNodeWithText("keep", useUnmergedTree = false).assertExists()
    }

    @Test
    fun semantics_descendantsOfPlaceholderAreCleared() = runComposeUiTest {
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Box(Modifier.skeleton()) {
                    Column {
                        BasicText("child")
                        BasicText("keep child", Modifier.skeleton(SkeletonMode.Keep))
                    }
                }
            }
        }

        onNodeWithText("child", useUnmergedTree = false).assertDoesNotExist()
        onNodeWithText("keep child", useUnmergedTree = false).assertDoesNotExist()
    }

    @Test
    fun semantics_clickableBeforeSkeleton_isHiddenFromAccessibility() = runComposeUiTest {
        var enabled by mutableStateOf(true)
        setContent {
            Host(enabled = enabled, animationEnabled = false) {
                Box(Modifier.testTag(ElementTag).size(ElementSize).clickable {}.skeleton())
            }
        }

        onNodeWithTag(ElementTag).assert(isHiddenFromAccessibility)
        enabled = false
        onNodeWithTag(ElementTag).assert(!isHiddenFromAccessibility).assertHasClickAction()
    }

    @Test
    fun semantics_keepElementKeepsClickAction() = runComposeUiTest {
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Box(Modifier.testTag(ElementTag).size(ElementSize).skeleton(SkeletonMode.Keep).clickable {})
            }
        }

        onNodeWithTag(ElementTag).assert(!isHiddenFromAccessibility).assertHasClickAction()
    }

    @Test
    fun semantics_keepParentWithPlaceholderChildStaysAccessible() = runComposeUiTest {
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Box(
                    Modifier
                        .testTag(ElementTag)
                        .size(ElementSize)
                        .skeleton(SkeletonMode.Keep)
                        .clickable {},
                ) {
                    BasicText("placeholder", Modifier.skeleton())
                }
            }
        }

        onNodeWithTag(ElementTag).assert(!isHiddenFromAccessibility).assertHasClickAction()
    }

    // endregion

    // region Pointer input

    @Test
    fun click_paddingBetweenClickableAndSkeletonIsNotBlocked() = runComposeUiTest {
        // Documents a limitation: a press outside the skeleton node's bounds never reaches it.
        var clicks = 0
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Box(
                    Modifier
                        .testTag(ElementTag)
                        .size(ElementSize)
                        .clickable { clicks++ }
                        .padding(15.dp)
                        .skeleton(),
                )
            }
        }

        onNodeWithTag(ElementTag).performMouseInput { click(Offset(2f, 2f)) }
        onNodeWithTag(ElementTag).performMouseInput { click(center) }
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun click_blockedWhenSkeletonBeforeClickable() = runComposeUiTest {
        assertClickBlocked { onClick -> Modifier.skeleton().clickable(onClick = onClick) }
    }

    @Test
    fun click_blockedWhenSkeletonAfterClickable() = runComposeUiTest {
        assertClickBlocked { onClick -> Modifier.clickable(onClick = onClick).skeleton() }
    }

    @Test
    fun click_blockedForEveryPlaceholderMode() = runComposeUiTest {
        val modes = listOf(SkeletonMode.Shimmer, SkeletonMode.Solid, SkeletonMode.Hide)
        val clicks = IntArray(modes.size)
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Column {
                    modes.forEachIndexed { index, mode ->
                        Box(
                            Modifier
                                .testTag("$ElementTag$index")
                                .size(ElementSize)
                                .clickable { clicks[index]++ }
                                .skeleton(mode),
                        )
                    }
                }
            }
        }

        modes.indices.forEach { index ->
            onNodeWithTag("$ElementTag$index").performTouchInput { click(center) }
        }
        runOnIdle { assertEquals(listOf(0, 0, 0), clicks.toList()) }
    }

    @Test
    fun click_blockedForDescendants() = runComposeUiTest {
        var clicks = 0
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Box(Modifier.size(ElementSize).skeleton()) {
                    Box(Modifier.testTag(ElementTag).size(ElementSize).clickable { clicks++ })
                }
            }
        }

        onNodeWithTag(ElementTag, useUnmergedTree = true).performTouchInput { click(center) }
        onNodeWithTag(ElementTag, useUnmergedTree = true).performMouseInput { click(center) }
        runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun click_keepElementStaysClickable() = runComposeUiTest {
        var clicks = 0
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Box(
                    Modifier
                        .testTag(ElementTag)
                        .size(ElementSize)
                        .clickable { clicks++ }
                        .skeleton(SkeletonMode.Keep),
                ) {
                    // Placeholder child in a corner; the tap below lands outside of it.
                    Box(Modifier.align(Alignment.TopStart).size(10.dp).skeleton())
                }
            }
        }

        onNodeWithTag(ElementTag).performTouchInput { click(center) }
        onNodeWithTag(ElementTag).performMouseInput { click(center) }
        runOnIdle { assertEquals(2, clicks) }
    }

    @Test
    fun click_worksAgainAfterHostIsDisabled() = runComposeUiTest {
        var clicks = 0
        var enabled by mutableStateOf(true)
        setContent {
            Host(enabled = enabled, animationEnabled = false) {
                Box(Modifier.testTag(ElementTag).size(ElementSize).clickable { clicks++ }.skeleton())
            }
        }

        onNodeWithTag(ElementTag).performTouchInput { click(center) }
        runOnIdle { assertEquals(0, clicks) }
        enabled = false
        onNodeWithTag(ElementTag).performTouchInput { click(center) }
        runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun scroll_parentStillScrollsWhenDragStartsOnPlaceholder() = runComposeUiTest {
        val scrollState = ScrollState(0)
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Column(Modifier.testTag(ElementTag).height(100.dp).verticalScroll(scrollState)) {
                    repeat(10) {
                        Box(Modifier.size(ElementSize).clickable {}.skeleton())
                    }
                }
            }
        }

        onNodeWithTag(ElementTag).performTouchInput { swipeUp() }
        runOnIdle { assertTrue(scrollState.value > 0, "Expected the column to scroll") }
    }

    // endregion

    // region Focus

    @Test
    fun focus_blockedWhenSkeletonBeforeFocusable() = runComposeUiTest {
        val requester = FocusRequester()
        var focused = false
        var enabled by mutableStateOf(true)
        setContent {
            Host(enabled = enabled, animationEnabled = false) {
                Box(
                    Modifier
                        .size(ElementSize)
                        .skeleton()
                        .focusRequester(requester)
                        .onFocusChanged { focused = it.isFocused }
                        .focusable(),
                )
            }
        }

        runOnIdle { requester.requestFocus() }
        runOnIdle { assertFalse(focused) }
        enabled = false
        runOnIdle { requester.requestFocus() }
        runOnIdle { assertTrue(focused) }
    }

    @Test
    fun focus_blockedForDescendants() = runComposeUiTest {
        val requester = FocusRequester()
        var focused = false
        lateinit var focusManager: FocusManager
        setContent {
            focusManager = LocalFocusManager.current
            Host(enabled = true, animationEnabled = false) {
                Box(Modifier.size(ElementSize).skeleton()) {
                    Box(
                        Modifier
                            .size(ElementSize)
                            .focusRequester(requester)
                            .onFocusChanged { focused = it.isFocused }
                            .focusable(),
                    )
                }
            }
        }

        runOnIdle { requester.requestFocus() }
        runOnIdle { assertFalse(focused) }
        runOnIdle { focusManager.moveFocus(FocusDirection.Next) }
        runOnIdle { assertFalse(focused) }
    }

    @Test
    fun focus_blockedForDescendantsOfFocusablePlaceholder() = runComposeUiTest {
        val requester = FocusRequester()
        var focused = false
        var enabled by mutableStateOf(true)
        lateinit var focusManager: FocusManager
        setContent {
            focusManager = LocalFocusManager.current
            Host(enabled = enabled, animationEnabled = false) {
                Box(Modifier.size(ElementSize).skeleton().focusable()) {
                    Box(
                        Modifier
                            .size(ElementSize)
                            .focusRequester(requester)
                            .onFocusChanged { focused = it.isFocused }
                            .focusable(),
                    )
                }
            }
        }

        runOnIdle { focusManager.moveFocus(FocusDirection.Next) }
        runOnIdle { assertFalse(focused) }
        runOnIdle { requester.requestFocus() }
        runOnIdle { assertFalse(focused) }
        enabled = false
        runOnIdle { requester.requestFocus() }
        runOnIdle { assertTrue(focused) }
    }

    @Test
    fun focus_clearedWhenHostBecomesEnabled() = runComposeUiTest {
        val requester = FocusRequester()
        var focused = false
        var enabled by mutableStateOf(false)
        setContent {
            Host(enabled = enabled, animationEnabled = false) {
                Box(
                    Modifier
                        .size(ElementSize)
                        .skeleton()
                        .focusRequester(requester)
                        .onFocusChanged { focused = it.isFocused }
                        .focusable(),
                )
            }
        }

        runOnIdle { requester.requestFocus() }
        runOnIdle { assertTrue(focused) }
        enabled = true
        runOnIdle { assertFalse(focused) }
    }

    @Test
    fun focus_keepElementStaysFocusable() = runComposeUiTest {
        val requester = FocusRequester()
        var focused = false
        setContent {
            Host(enabled = true, animationEnabled = false) {
                Box(
                    Modifier
                        .size(ElementSize)
                        .skeleton(SkeletonMode.Keep)
                        .focusRequester(requester)
                        .onFocusChanged { focused = it.isFocused }
                        .focusable(),
                )
            }
        }

        runOnIdle { requester.requestFocus() }
        runOnIdle { assertTrue(focused) }
    }

    // endregion

    // region Helpers

    private fun ComposeUiTest.assertClickBlocked(elementModifier: (onClick: () -> Unit) -> Modifier) {
        var clicks = 0
        var enabled by mutableStateOf(true)
        setContent {
            Host(enabled = enabled, animationEnabled = false) {
                Box(Modifier.testTag(ElementTag).size(ElementSize).then(elementModifier { clicks++ }))
            }
        }

        onNodeWithTag(ElementTag).performTouchInput { click(center) }
        onNodeWithTag(ElementTag).performMouseInput { click(center) }
        runOnIdle { assertEquals(0, clicks, "Clicks must be blocked while loading") }

        enabled = false
        onNodeWithTag(ElementTag).performTouchInput { click(center) }
        onNodeWithTag(ElementTag).performMouseInput { click(center) }
        runOnIdle { assertEquals(2, clicks, "Clicks must work after loading") }
    }

    @Composable
    private fun Host(
        enabled: Boolean,
        animationEnabled: Boolean = true,
        style: SkeletonStyle = SolidStyle,
        content: @Composable () -> Unit,
    ) {
        Box(Modifier.testTag(HostTag).background(Background)) {
            SkeletonHost(enabled = enabled, style = style, animationEnabled = animationEnabled) {
                content()
            }
        }
    }

    @Composable
    private fun SingleElement(enabled: Boolean, mode: SkeletonMode, animationEnabled: Boolean = false) {
        Host(
            enabled = enabled,
            animationEnabled = animationEnabled,
            style = if (animationEnabled) ShimmerStyle else SolidStyle,
        ) {
            // The background sits after `.skeleton()`, so it is part of the content that the
            // placeholder replaces. Modifiers before `.skeleton()` are always drawn.
            Box(Modifier.size(ElementSize).skeleton(mode).background(Content))
        }
    }

    private fun ComposeUiTest.centerPixel(): Color {
        val pixels = onNodeWithTag(HostTag).captureToImage().toPixelMap()
        return pixels[pixels.width / 2, pixels.height / 2]
    }

    private fun ComposeUiTest.assertCenterColor(expected: Color, message: String) {
        val actual = centerPixel()
        val matches = abs(actual.red - expected.red) < ColorTolerance &&
            abs(actual.green - expected.green) < ColorTolerance &&
            abs(actual.blue - expected.blue) < ColorTolerance
        assertTrue(matches, "$message: expected $expected but was $actual")
    }

    private companion object {
        const val HostTag = "host"
        const val ElementTag = "element"
        const val ColorTolerance = 0.02f
        val ElementSize = 40.dp
        val Background = Color.White
        val Content = Color.Green
        val Base = Color.Red

        val SolidStyle = SkeletonStyle(
            baseColor = Base,
            highlightColor = Base,
            shape = RectangleShape,
            animationDurationMillis = 1_000,
            highlightWidth = 20.dp,
            tiltDegrees = 0f,
            direction = SkeletonDirection.LeftToRight,
        )

        val ShimmerStyle = SolidStyle.copy(highlightColor = Color.Blue, highlightWidth = 400.dp)

        val isHiddenFromAccessibility =
            SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)
    }

    // endregion
}
