package org.simple.clinic.home.overdue

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import org.simple.clinic.facility.Facility
import org.simple.clinic.storage.paging.ItemTransformingDataSource
import org.simple.clinic.util.UserClock
import java.time.format.DateTimeFormatter
import javax.inject.Named

class OverdueAppointmentRowDataSourceFactoryV2 @AssistedInject constructor(
    private val userClock: UserClock,
    @Named("full_date") private val dateFormatter: DateTimeFormatter,
    @Assisted private val currentFacility: Facility,
    @Assisted private val source: PositionalDataSource<OverdueAppointment>
) : DataSource.Factory<Int, OverdueAppointmentRow>() {

  @AssistedInject.Factory
  interface InjectionFactory {
    fun create(
        currentFacility: Facility,
        source: PositionalDataSource<OverdueAppointment>
    ): OverdueAppointmentRowDataSourceFactoryV2
  }

  override fun create(): DataSource<Int, OverdueAppointmentRow> {
    val transformer = OverdueItemTransformer(userClock, dateFormatter, currentFacility.config.diabetesManagementEnabled)

    return ItemTransformingDataSource(source, transformer)
  }

  private class OverdueItemTransformer(
      private val userClock: UserClock,
      private val dateFormatter: DateTimeFormatter,
      private val diabetesManagementEnabled: Boolean
  ) : ItemTransformingDataSource.ItemTransformer<OverdueAppointment, OverdueAppointmentRow> {

    override fun transform(itemPosition: Int, item: OverdueAppointment, next: OverdueAppointment?): List<OverdueAppointmentRow> {
      return listOf(OverdueAppointmentRow.from(
          overdueAppointment = item,
          clock = userClock,
          dateFormatter = dateFormatter,
          isDiabetesManagementEnabled = diabetesManagementEnabled
      ))
    }
  }
}
