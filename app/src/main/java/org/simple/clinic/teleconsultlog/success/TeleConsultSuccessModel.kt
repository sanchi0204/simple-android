package org.simple.clinic.teleconsultlog.success

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.simple.clinic.patient.Patient
import java.util.UUID

@Parcelize
data class TeleConsultSuccessModel(
    val patientUuid: UUID,
    val patient: Patient?
) : Parcelable {

  val hasPatient: Boolean
    get() = patient != null

  companion object {
    fun create(patientUuid: UUID) = TeleConsultSuccessModel(patientUuid, null)
  }

  fun patientDetailLoaded(patient: Patient?): TeleConsultSuccessModel =
      copy(patient = patient)
}