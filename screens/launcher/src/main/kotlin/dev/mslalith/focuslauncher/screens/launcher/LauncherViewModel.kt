package dev.mslalith.focuslauncher.screens.launcher

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mslalith.focuslauncher.core.common.appcoroutinedispatcher.AppCoroutineDispatcher
import dev.mslalith.focuslauncher.core.data.repository.AppDrawerRepo
import dev.mslalith.focuslauncher.core.data.repository.FavoritesRepo
import dev.mslalith.focuslauncher.core.data.repository.HiddenAppsRepo
import dev.mslalith.focuslauncher.core.model.App
import dev.mslalith.focuslauncher.core.ui.extensions.launchInIO
import dev.mslalith.focuslauncher.screens.launcher.utils.appDrawerApps
import javax.inject.Inject

@HiltViewModel
internal class LauncherViewModel @Inject constructor(
    private val appDrawerRepo: AppDrawerRepo,
    private val favoritesRepo: FavoritesRepo,
    private val hiddenAppsRepo: HiddenAppsRepo,
    private val appCoroutineDispatcher: AppCoroutineDispatcher
) : ViewModel() {

    fun setAppsIfCacheEmpty(context: Context, checkCache: Boolean = true) {
        appCoroutineDispatcher.launchInIO {
            appDrawerRepo.apply {
                if (checkCache) {
                    if (areAppsEmptyInDatabase()) {
                        addApps(context.appDrawerApps)
                    }
                    return@apply
                }
                addApps(context.appDrawerApps)
            }
        }
    }

    fun handleAppInstall(app: App) {
        appCoroutineDispatcher.launchInIO { appDrawerRepo.addApp(app) }
    }

    fun handleAppUninstall(packageName: String) {
        appCoroutineDispatcher.launchInIO {
            appDrawerRepo.getAppBy(packageName)?.let { app ->
                favoritesRepo.removeFromFavorites(app.packageName)
                hiddenAppsRepo.removeFromHiddenApps(app.packageName)
                appDrawerRepo.removeApp(app)
            }
        }
    }
}
