package org.simple.clinic.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChange.BACKWARD
import com.zhuinden.simplestack.StateChange.FORWARD
import com.zhuinden.simplestack.StateChange.REPLACE
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger

class BottomSheetSupportFragmentStateChanger(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerResId: Int
) {

  private val fragmentStateChanger = DefaultFragmentStateChanger(fragmentManager, containerResId)

  fun handleStateChange(stateChange: StateChange) {
    when (val direction = stateChange.direction) {
      FORWARD -> handleForwardNavigation(stateChange)
      BACKWARD -> handleBackwardNavigation(stateChange)
      REPLACE -> fragmentStateChanger.handleStateChange(stateChange)
      else -> throw IllegalArgumentException("Unknown state change direction: $direction")
    }
  }

  private fun handleForwardNavigation(stateChange: StateChange) {
    val incomingKey = stateChange.topNewKey<DefaultFragmentKey>()

    if (incomingKey is BottomSheetFragmentKey) {
      val bottomSheetFragment = incomingKey.createFragment() as BottomSheetDialogFragment
      bottomSheetFragment.show(fragmentManager, incomingKey.fragmentTag)
    } else {
      fragmentStateChanger.handleStateChange(stateChange)
    }
  }

  private fun handleBackwardNavigation(stateChange: StateChange) {
    val outgoingKey = stateChange.topPreviousKey<DefaultFragmentKey>()

    if (outgoingKey is BottomSheetFragmentKey) {
      val bottomSheetFragment = fragmentManager.findFragmentByTag(outgoingKey.fragmentTag) as BottomSheetDialogFragment?
      bottomSheetFragment?.dismiss()
    } else {
      fragmentStateChanger.handleStateChange(stateChange)
    }
  }
}
