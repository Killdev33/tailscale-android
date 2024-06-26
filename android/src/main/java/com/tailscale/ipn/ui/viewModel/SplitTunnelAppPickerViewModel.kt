package com.tailscale.ipn.ui.viewModel

import androidx.lifecycle.ViewModel
import com.tailscale.ipn.App
import com.tailscale.ipn.ui.util.InstalledApp
import com.tailscale.ipn.ui.util.InstalledAppsManager
import com.tailscale.ipn.ui.util.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SplitTunnelAppPickerViewModel : ViewModel() {
  val installedAppsManager = InstalledAppsManager(packageManager = App.get().packageManager)
  val excludedPackageNames: StateFlow<List<String>> = MutableStateFlow(listOf())
  val installedApps: StateFlow<List<InstalledApp>> = MutableStateFlow(listOf())

  init {
    excludedPackageNames.set(App.get().disallowedPackageNames())
    installedApps.set(installedAppsManager.fetchInstalledApps())
  }

  fun exclude(packageName: String) {
    if (excludedPackageNames.value.contains(packageName)) {
      return
    }
    excludedPackageNames.set(excludedPackageNames.value + packageName)
    App.get().addDisallowedPackageName(packageName)
  }

  fun unexclude(packageName: String) {
    excludedPackageNames.set(excludedPackageNames.value - packageName)
    App.get().removeDisallowedPackageName(packageName)
  }
}
