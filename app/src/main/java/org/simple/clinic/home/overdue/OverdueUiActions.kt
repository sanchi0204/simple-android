package org.simple.clinic.home.overdue

import org.simple.clinic.storage.paging.RoomInvalidatingDataSource
import java.util.UUID

interface OverdueUiActions {
  fun openPhoneMaskBottomSheet(patientUuid: UUID)
  fun showOverdueAppointments(dataSource: OverdueAppointmentRowDataSource.Factory)
  fun showOverdueAppointments(dataSource: RoomInvalidatingDataSource.Factory<OverdueAppointmentRow>)
  fun openPatientSummary(patientUuid: UUID)
}
