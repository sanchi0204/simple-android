package org.simple.clinic.home.overdue

import androidx.paging.PagingSource
import java.util.UUID

interface OverdueUiActions {
  fun openPhoneMaskBottomSheet(patientUuid: UUID)
  fun showOverdueAppointments(dataSource: PagingSource<Int, OverdueAppointment>)
  fun openPatientSummary(patientUuid: UUID)
}
