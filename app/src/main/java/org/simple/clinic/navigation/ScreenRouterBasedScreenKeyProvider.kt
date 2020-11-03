package org.simple.clinic.navigation

import android.view.View
import org.simple.clinic.router.screen.ScreenRouter
import javax.inject.Inject

class ScreenRouterBasedScreenKeyProvider @Inject constructor(
    private val screenRouter: ScreenRouter
) : ScreenKeyProvider {

  override fun <T> provide(view: View): T {
    return screenRouter.key(view)
  }
}
