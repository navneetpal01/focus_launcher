package dev.mslalith.focuslauncher.feature.settingspage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mslalith.focuslauncher.core.model.Screen
import dev.mslalith.focuslauncher.core.model.WidgetType
import dev.mslalith.focuslauncher.core.ui.VerticalSpacer
import dev.mslalith.focuslauncher.core.ui.managers.LauncherViewManager
import dev.mslalith.focuslauncher.core.ui.providers.LocalLauncherViewManager
import dev.mslalith.focuslauncher.core.ui.providers.LocalNavController
import dev.mslalith.focuslauncher.feature.clock24.settings.ClockSettingsSheet
import dev.mslalith.focuslauncher.feature.lunarcalendar.model.LunarPhaseSettingsProperties
import dev.mslalith.focuslauncher.feature.lunarcalendar.settings.LunarPhaseSettingsSheet
import dev.mslalith.focuslauncher.feature.quoteforyou.settings.QuotesSettingsSheet
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.About
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.AppDrawer
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.ChangeTheme
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.EditFavorites
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.HideApps
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.IconPack
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.PullDownNotifications
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.SetAsDefaultLauncher
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.SettingsHeader
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.ToggleStatusBar
import dev.mslalith.focuslauncher.feature.settingspage.settingsitems.Widgets

@Composable
fun SettingsPage() {
    val navController = LocalNavController.current

    SettingsPageInternal(
        navigateTo = { navController.navigate(it.id) }
    )
}

@Composable
internal fun SettingsPageInternal(
    settingsPageViewModel: SettingsPageViewModel = hiltViewModel(),
    navigateTo: (Screen) -> Unit
) {
    val context = LocalContext.current
    val viewManager = LocalLauncherViewManager.current

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.Center
    ) {
        SettingsHeader()
        VerticalSpacer(spacing = 12.dp)

        ChangeTheme(
            currentTheme = settingsPageViewModel.currentThemeStateFlow.collectAsStateWithLifecycle().value,
            changeTheme = settingsPageViewModel::changeTheme
        )

        EditFavorites { navigateTo(Screen.EditFavorites) }
        HideApps { navigateTo(Screen.HideApps) }

        ToggleStatusBar(
            showStatusBar = settingsPageViewModel.statusBarVisibilityStateFlow.collectAsStateWithLifecycle().value,
            onClick = settingsPageViewModel::toggleStatusBarVisibility
        )
        PullDownNotifications(
            enableNotificationShade = settingsPageViewModel.notificationShadeStateFlow.collectAsStateWithLifecycle().value,
            onClick = settingsPageViewModel::toggleNotificationShade
        )

        IconPack(
            shouldShow = settingsPageViewModel.canShowIconPackStateFlow.collectAsStateWithLifecycle().value,
            onClick = { navigateTo(Screen.IconPack) }
        )

        AppDrawer {
            viewManager.showBottomSheet {
                AppDrawerSettingsSheet()
            }
        }

        Widgets(
            viewManager = viewManager,
            navigateTo = navigateTo
        )

        SetAsDefaultLauncher(
            isDefaultLauncher = settingsPageViewModel.isDefaultLauncherStateFlow.collectAsStateWithLifecycle().value,
            refreshIsDefaultLauncher = { settingsPageViewModel.refreshIsDefaultLauncher(context = context) }
        )

        About { navigateTo(Screen.About) }

        VerticalSpacer(spacing = 12.dp)
    }
}

@Composable
private fun Widgets(
    viewManager: LauncherViewManager,
    navigateTo: (Screen) -> Unit
) {
    Widgets { widgetType ->
        when (widgetType) {
            WidgetType.CLOCK -> {
                viewManager.showBottomSheet { ClockSettingsSheet() }
            }
            WidgetType.LUNAR_PHASE -> {
                viewManager.showBottomSheet {
                    LunarPhaseSettingsSheet(
                        properties = LunarPhaseSettingsProperties(
                            navigateToCurrentPlace = {
                                viewManager.hideBottomSheet()
                                navigateTo(Screen.CurrentPlace)
                            }
                        )
                    )
                }
            }
            WidgetType.QUOTES -> {
                viewManager.showBottomSheet { QuotesSettingsSheet() }
            }
        }
    }
}
