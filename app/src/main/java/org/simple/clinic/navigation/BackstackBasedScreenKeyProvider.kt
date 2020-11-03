package org.simple.clinic.navigation

import android.view.View
import com.zhuinden.simplestack.Backstack
import javax.inject.Inject

class BackstackBasedScreenKeyProvider @Inject constructor(
    private val backstack: Backstack
): ScreenKeyProvider {

  override fun <T> provide(view: View): T {
    // This is a hack, but it works for now since only one the current
    // fragment will be on the top. We might run into race conditions,
    // but those will eventually get resolved when we migrate the screens
    // to using the [com.zhuinden.simplestackextensions.fragments.KeyedFragment]
    // type.
    return backstack.top()
  }
}
