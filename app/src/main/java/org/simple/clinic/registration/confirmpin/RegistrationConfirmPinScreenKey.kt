package org.simple.clinic.registration.confirmpin

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.android.parcel.Parcelize
import org.simple.clinic.user.OngoingRegistrationEntry

@Parcelize
data class RegistrationConfirmPinScreenKey(val registrationEntry: OngoingRegistrationEntry) : DefaultFragmentKey() {

  override fun instantiateFragment() = RegistrationConfirmPinScreen()
}
