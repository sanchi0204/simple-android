package org.simple.clinic.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.zhuinden.simplestack.ScopedServices

class BackPressHandler(
    @IdRes private val containerId: Int,
    private val fragmentManager: FragmentManager
) : ScopedServices.HandlesBack {

  override fun onBackEvent(): Boolean {
    val fragment = fragmentManager.findFragmentById(containerId)

    return if (fragment == null || fragment !is ScopedServices.HandlesBack)
      false
    else (fragment as ScopedServices.HandlesBack).onBackEvent()
  }
}
