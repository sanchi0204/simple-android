package org.simple.clinic.registration.confirmfacility

import kotlinx.android.parcel.Parcelize
import org.simple.clinic.navigation.BottomSheetFragmentKey
import java.util.UUID

@Parcelize
data class ConfirmFacilitySheetKey(
    val facilityId: UUID,
    val facilityName: String
): BottomSheetFragmentKey() {

  override fun instantiateFragment() = ConfirmFacilitySheet.create()
}
