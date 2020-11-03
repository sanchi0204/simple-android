package org.simple.clinic.navigation

import android.view.View
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import org.simple.clinic.router.screen.ScreenRouter

/**
 * Interface used to make migration from [ScreenRouter] to [Backstack]
 * easier.
 *
 * [Deprecated] For new screens, implement [KeyedFragment] and fetch
 * the key from the convenience method.
 **/
interface ScreenKeyProvider {

  fun <T> provide(view: View): T
}
