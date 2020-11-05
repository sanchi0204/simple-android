package org.simple.clinic.registration.pin

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding3.widget.editorActions
import com.zhuinden.simplestack.Backstack
import io.reactivex.Observable
import io.reactivex.rxkotlin.cast
import kotlinx.android.synthetic.main.screen_registration_pin.*
import org.simple.clinic.R
import org.simple.clinic.ReportAnalyticsEvents
import org.simple.clinic.SECURITY_PIN_LENGTH
import org.simple.clinic.di.injector
import org.simple.clinic.navigation.FullScreenFragment
import org.simple.clinic.registration.confirmpin.RegistrationConfirmPinScreenKey
import org.simple.clinic.user.OngoingRegistrationEntry
import javax.inject.Inject

class RegistrationPinScreen :
    FullScreenFragment<RegistrationPinModel, RegistrationPinEvent, RegistrationPinEffect>(R.layout.screen_registration_pin),
    RegistrationPinUi,
    RegistrationPinUiActions {

  @Inject
  lateinit var backstack: Backstack

  @Inject
  lateinit var effectHandlerFactory: RegistrationPinEffectHandler.Factory

  private val uiRenderer = RegistrationPinUiRenderer(this)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    requireContext().injector<Injector>().inject(this)
    super.onViewCreated(view, savedInstanceState)

    pinEditText.isSaveEnabled = false

    backButton.setOnClickListener { backstack.goBack() }

    view.post { pinEditText.requestFocus() }
  }

  override fun defaultModel(): RegistrationPinModel {
    val screenKey = getKey<RegistrationPinScreenKey>()

    return RegistrationPinModel.create(screenKey.registrationEntry)
  }

  override fun onModelUpdate(model: RegistrationPinModel) {
    uiRenderer.render(model)
  }

  override fun events() = Observable
      .merge(
          pinTextChanges(),
          doneClicks()
      )
      .compose(ReportAnalyticsEvents())
      .cast<RegistrationPinEvent>()

  override fun createUpdate() = RegistrationPinUpdate(requiredPinLength = SECURITY_PIN_LENGTH)

  override fun createInit() = RegistrationPinInit()

  override fun createEffectHandler() = effectHandlerFactory
      .create(this)
      .build()

  private fun pinTextChanges() =
      pinEditText
          .textChanges()
          .skip(1)
          .map(CharSequence::toString)
          .map(::RegistrationPinTextChanged)

  private fun doneClicks(): Observable<RegistrationPinDoneClicked>? {
    val imeDoneClicks = pinEditText
        .editorActions() { it == EditorInfo.IME_ACTION_DONE }
        .map { RegistrationPinDoneClicked() }

    val pinAutoSubmits = pinEditText
        .textChanges()
        .skip(1)
        .filter { it.length == SECURITY_PIN_LENGTH }
        .map { RegistrationPinDoneClicked() }

    return imeDoneClicks.mergeWith(pinAutoSubmits)
  }

  override fun showIncompletePinError() {
    pinHintTextView.visibility = View.GONE
    errorTextView.visibility = View.VISIBLE
    errorTextView.text = resources.getString(R.string.registrationpin_error_incomplete_pin)
  }

  override fun hideIncompletePinError() {
    pinHintTextView.visibility = View.VISIBLE
    errorTextView.visibility = View.GONE
  }

  override fun openRegistrationConfirmPinScreen(registrationEntry: OngoingRegistrationEntry) {
    backstack.goTo(RegistrationConfirmPinScreenKey(registrationEntry))
  }

  interface Injector {
    fun inject(target: RegistrationPinScreen)
  }
}
