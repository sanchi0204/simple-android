package org.simple.clinic.router.screen

import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import org.simple.clinic.navigation.BackPressHandler
import org.simple.clinic.router.ScreenWrapperFragment

/**
 *
 *  Screens can receive payloads inside their associated keys by calling [ScreenRouter.key].
 *
 * Note: use AutoValue or otherwise ensure equals() is overridden.
 * Screen routing is skipped if an outgoing key and an incoming key are equal.
 */
abstract class FullScreenKey : Parcelable, DefaultFragmentKey(), DefaultServiceProvider.HasServices {

  abstract val analyticsName: String

  @LayoutRes
  abstract fun layoutRes(): Int

  override fun instantiateFragment(): Fragment = ScreenWrapperFragment.create(this)

  override fun getScopeTag() = ""

  override fun bindServices(serviceBinder: ServiceBinder) {
    serviceBinder.add(serviceBinder.lookupService<BackPressHandler>("backpress"))
  }
}
