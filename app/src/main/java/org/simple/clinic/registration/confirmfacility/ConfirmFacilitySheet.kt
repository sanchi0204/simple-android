package org.simple.clinic.registration.confirmfacility

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.zhuinden.simplestack.Backstack
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.sheet_registration_confirm_facility.*
import org.simple.clinic.R
import org.simple.clinic.di.injector
import org.simple.clinic.navigation.BottomSheetFragment
import org.simple.clinic.util.unsafeLazy
import java.util.UUID
import javax.inject.Inject

class ConfirmFacilitySheet : BottomSheetFragment<EmptyModel, Nothing, Nothing>() {

  companion object {

    fun create() = ConfirmFacilitySheet()
  }

  @Inject
  lateinit var backstack: Backstack

  private val key by unsafeLazy { getKey<ConfirmFacilitySheetKey>() }

  private val facilityName: String by unsafeLazy { key.facilityName }

  private val facilityUuid: UUID by unsafeLazy { key.facilityId }

  override fun layoutResId() = R.layout.sheet_registration_confirm_facility

  override fun defaultModel() = EmptyModel

  override fun onModelUpdate(model: EmptyModel) {
    // Nothing to do here
  }

  override fun backstack(): Backstack {
    return backstack
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    requireContext().injector<Injector>().inject(this)
    super.onViewCreated(view, savedInstanceState)

    facilityNameTextView.text = facilityName
    yesButton.setOnClickListener { backstack.goBack() }
    cancelButton.setOnClickListener { backstack.goBack() }
  }

  /*override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.sheet_registration_confirm_facility)


  }

  private fun confirmFacilitySelection() {
    val intent = Intent()
    intent.putExtra(EXTRA_FACILITY_UUID, facilityUuid)
    setResult(Activity.RESULT_OK, intent)
    finish()
  }*/

  interface Injector {
    fun inject(target: ConfirmFacilitySheet)
  }
}

@Parcelize object EmptyModel: Parcelable
