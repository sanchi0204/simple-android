package org.simple.clinic.registration.pin

import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import org.simple.clinic.navigation.FragmentKey
import org.simple.clinic.user.OngoingRegistrationEntry

@Parcelize
data class RegistrationPinScreenKey(
    val registrationEntry: OngoingRegistrationEntry
) : FragmentKey() {

  override fun instantiateFragment(): Fragment {
    return RegistrationPinScreen()
  }
}
