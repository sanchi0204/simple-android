package org.simple.clinic.registration.pin

import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.android.parcel.Parcelize
import org.simple.clinic.user.OngoingRegistrationEntry

@Parcelize
data class RegistrationPinScreenKey(
    val registrationEntry: OngoingRegistrationEntry
) : DefaultFragmentKey() {

  override fun instantiateFragment(): Fragment {
    return RegistrationPinScreen()
  }
}
