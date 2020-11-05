package org.simple.clinic.registration.confirmpin

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.editorActions
import com.zhuinden.simplestack.Backstack
import io.reactivex.Observable
import io.reactivex.rxkotlin.cast
import kotlinx.android.synthetic.main.screen_registration_confirm_pin.*
import org.simple.clinic.R
import org.simple.clinic.ReportAnalyticsEvents
import org.simple.clinic.SECURITY_PIN_LENGTH
import org.simple.clinic.di.injector
import org.simple.clinic.navigation.FullScreenFragment
import org.simple.clinic.registration.location.RegistrationLocationPermissionScreenKey
import org.simple.clinic.registration.pin.RegistrationPinScreenKey
import org.simple.clinic.user.OngoingRegistrationEntry
import org.simple.clinic.widgets.hideKeyboard
import org.simple.clinic.widgets.showKeyboard
import javax.inject.Inject

class RegistrationConfirmPinScreen :
    FullScreenFragment<RegistrationConfirmPinModel, RegistrationConfirmPinEvent, RegistrationConfirmPinEffect>(R.layout.screen_registration_confirm_pin),
    RegistrationConfirmPinUi,
    RegistrationConfirmPinUiActions {

  @Inject
  lateinit var backstack: Backstack

  @Inject
  lateinit var effectHandlerFactory: RegistrationConfirmPinEffectHandler.Factory

  private val uiRenderer = RegistrationConfirmPinUiRenderer(this)

  override fun defaultModel(): RegistrationConfirmPinModel {
    val screenKey = getKey<RegistrationConfirmPinScreenKey>()

    return RegistrationConfirmPinModel.create(screenKey.registrationEntry)
  }

  override fun onModelUpdate(model: RegistrationConfirmPinModel) {
    uiRenderer.render(model)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    requireContext().injector<Injector>().inject(this)
    super.onViewCreated(view, savedInstanceState)

    backButton.setOnClickListener { backstack.goBack() }

    // Showing the keyboard again in case the user returns from location permission screen.
    confirmPinEditText.showKeyboard()
  }

  override fun events() = Observable
      .merge(
          confirmPinTextChanges(),
          resetPinClicks(),
          doneClicks()
      )
      .compose(ReportAnalyticsEvents())
      .cast<RegistrationConfirmPinEvent>()

  override fun createUpdate() = RegistrationConfirmPinUpdate()

  override fun createInit() = RegistrationConfirmPinInit()

  override fun createEffectHandler() = effectHandlerFactory.create(this).build()

  private fun confirmPinTextChanges() =
      confirmPinEditText
          .textChanges()
          .map(CharSequence::toString)
          .map(::RegistrationConfirmPinTextChanged)

  private fun resetPinClicks() =
      resetPinButton
          .clicks()
          .map { RegistrationResetPinClicked() }

  private fun doneClicks(): Observable<RegistrationConfirmPinDoneClicked>? {
    val imeDoneClicks = confirmPinEditText
        .editorActions() { it == EditorInfo.IME_ACTION_DONE }
        .map { RegistrationConfirmPinDoneClicked() }

    val pinAutoSubmits = confirmPinEditText
        .textChanges()
        .doOnNext { it }
        // Because PIN is auto-submitted when 4 digits are entered, restoring the
        // existing PIN will immediately take the user to the next screen.
        .skip(1)
        .filter { it.length == SECURITY_PIN_LENGTH }
        .map { RegistrationConfirmPinDoneClicked() }

    return imeDoneClicks.mergeWith(pinAutoSubmits)
  }

  override fun showPinMismatchError() {
    errorStateViewGroup.visibility = View.VISIBLE
    pinHintTextView.visibility = View.GONE
  }

  override fun clearPin() {
    confirmPinEditText.text = null
  }

  override fun openFacilitySelectionScreen(entry: OngoingRegistrationEntry) {
    requireView().hideKeyboard()
    backstack.goTo(RegistrationLocationPermissionScreenKey(entry))
  }

  override fun goBackToPinScreen(entry: OngoingRegistrationEntry) {
    backstack.goUp(RegistrationPinScreenKey(entry))
  }

  interface Injector {
    fun inject(target: RegistrationConfirmPinScreen)
  }
}
