package org.simple.clinic.router.screen

import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import org.simple.clinic.router.ScreenWrapperFragment

/**
 *
 *  Screens can receive payloads inside their associated keys by calling [ScreenRouter.key].
 *
 * Note: use AutoValue or otherwise ensure equals() is overridden.
 * Screen routing is skipped if an outgoing key and an incoming key are equal.
 */
abstract class FullScreenKey : Parcelable, DefaultFragmentKey() {

  abstract val analyticsName: String

  @LayoutRes
  abstract fun layoutRes(): Int

  override fun instantiateFragment(): Fragment = ScreenWrapperFragment.create(this)
}
