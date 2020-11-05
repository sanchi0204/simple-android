package org.simple.clinic.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger

class BottomSheetSupportSimpleStateChanger(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerResId: Int
) {

  private val fragmentStateChanger = DefaultFragmentStateChanger(fragmentManager, containerResId)

  fun handleStateChange(stateChange: StateChange) {
    val incomingKey = stateChange.topNewKey<DefaultFragmentKey>()

    if(incomingKey is BottomSheetFragmentKey) {
      val bottomSheetFragment = incomingKey.createFragment() as BottomSheetDialogFragment
      bottomSheetFragment.show(fragmentManager, null)
    } else {
      fragmentStateChanger.handleStateChange(stateChange)
    }
  }
}
