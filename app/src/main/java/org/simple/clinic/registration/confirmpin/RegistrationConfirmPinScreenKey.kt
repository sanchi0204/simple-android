package org.simple.clinic.registration.confirmpin

import kotlinx.android.parcel.Parcelize
import org.simple.clinic.navigation.FragmentKey
import org.simple.clinic.user.OngoingRegistrationEntry

@Parcelize
data class RegistrationConfirmPinScreenKey(val registrationEntry: OngoingRegistrationEntry) : FragmentKey() {

  override fun instantiateFragment() = RegistrationConfirmPinScreen()
}
