package org.simple.clinic.drugs.selection.entry

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.focusChanges
import com.jakewharton.rxbinding3.widget.editorActions
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import io.reactivex.subjects.PublishSubject
import org.simple.clinic.ClinicApp
import org.simple.clinic.R
import org.simple.clinic.ReportAnalyticsEvents
import org.simple.clinic.databinding.SheetCustomPrescriptionEntryBinding
import org.simple.clinic.di.InjectorProviderContextWrapper
import org.simple.clinic.drugs.selection.entry.confirmremovedialog.ConfirmRemovePrescriptionDialog
import org.simple.clinic.drugs.selection.entry.di.CustomPrescriptionEntrySheetComponent
import org.simple.clinic.feature.Features
import org.simple.clinic.mobius.MobiusDelegate
import org.simple.clinic.util.unsafeLazy
import org.simple.clinic.util.withLocale
import org.simple.clinic.util.wrap
import org.simple.clinic.widgets.BottomSheetActivity
import org.simple.clinic.widgets.ScreenDestroyed
import org.simple.clinic.widgets.UiEvent
import org.simple.clinic.widgets.setTextAndCursor
import org.simple.clinic.widgets.textChanges
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class CustomPrescriptionEntrySheet : BottomSheetActivity(), CustomPrescriptionEntryUi, CustomPrescriptionEntryUiActions {

  @Inject
  lateinit var locale: Locale

  @Inject
  lateinit var effectHandlerFactory: CustomPrescriptionEntryEffectHandler.Factory

  @Inject
  lateinit var features: Features

  private lateinit var component: CustomPrescriptionEntrySheetComponent

  private val onDestroys = PublishSubject.create<ScreenDestroyed>()

  private val openAs by unsafeLazy {
    intent.getParcelableExtra<OpenAs>(KEY_OPEN_AS)!!
  }

  private val events by unsafeLazy {
    Observable
        .mergeArray(
            drugNameChanges(),
            drugDosageChanges(),
            drugDosageFocusChanges(),
            saveClicks(),
            removeClicks())
        .compose(ReportAnalyticsEvents())
  }

  private val uiRenderer = CustomPrescriptionEntryUiRenderer(this)

  private val delegate: MobiusDelegate<CustomPrescriptionEntryModel, CustomPrescriptionEntryEvent, CustomPrescriptionEntryEffect> by unsafeLazy {
    MobiusDelegate.forActivity(
        events = events.ofType(),
        defaultModel = CustomPrescriptionEntryModel.create(openAs),
        update = CustomPrescriptionEntryUpdate(),
        effectHandler = effectHandlerFactory.create(this).build(),
        init = CustomPrescriptionEntryInit(),
        modelUpdateListener = uiRenderer::render
    )
  }

  private lateinit var binding: SheetCustomPrescriptionEntryBinding

  private val rootLayout
    get() = binding.rootLayout

  private val drugNameEditText
    get() = binding.drugNameEditText

  private val drugDosageEditText
    get() = binding.drugDosageEditText

  private val saveButton
    get() = binding.saveButton

  private val removeMedicineButton
    get() = binding.removeMedicineButton

  private val enterMedicineTextView
    get() = binding.enterMedicineTextView

  private val editMedicineTextView
    get() = binding.editMedicineTextView

  @SuppressLint("CheckResult")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = SheetCustomPrescriptionEntryBinding.inflate(layoutInflater)
    setContentView(binding.root)

    delegate.onRestoreInstanceState(savedInstanceState)

    // Dismiss this sheet when the keyboard is dismissed.
    rootLayout.backKeyPressInterceptor = { super.onBackgroundClick() }
  }

  override fun attachBaseContext(baseContext: Context) {
    setupDiGraph()

    val wrappedContext = baseContext
        .wrap { InjectorProviderContextWrapper.wrap(it, component) }
        .wrap { ViewPumpContextWrapper.wrap(it) }

    super.attachBaseContext(wrappedContext)
    applyOverrideConfiguration(Configuration())
  }

  override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
    super.applyOverrideConfiguration(overrideConfiguration.withLocale(locale, features))
  }

  private fun setupDiGraph() {
    component = ClinicApp.appComponent
        .customPrescriptionEntrySheetComponentBuilder()
        .activity(this)
        .build()

    component.inject(this)
  }

  override fun onStart() {
    super.onStart()
    delegate.start()
  }

  override fun onStop() {
    delegate.stop()
    super.onStop()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    delegate.onSaveInstanceState(outState)
    super.onSaveInstanceState(outState)
  }

  override fun onDestroy() {
    onDestroys.onNext(ScreenDestroyed())
    super.onDestroy()
  }

  override fun onBackgroundClick() {
    val drugNameEmpty = drugNameEditText.text.isNullOrBlank()
    val dosageEmpty = drugDosageEditText.text.isNullOrBlank()
        || drugDosageEditText.text.toString().trim() == getString(R.string.customprescription_dosage_placeholder)

    if (drugNameEmpty && dosageEmpty) {
      super.onBackgroundClick()
    }
  }

  private fun drugNameChanges() = drugNameEditText.textChanges(::CustomPrescriptionDrugNameTextChanged)

  private fun drugDosageChanges() = drugDosageEditText.textChanges(::CustomPrescriptionDrugDosageTextChanged)

  private fun drugDosageFocusChanges() = drugDosageEditText
      .focusChanges()
      .map(::CustomPrescriptionDrugDosageFocusChanged)

  private fun saveClicks(): Observable<UiEvent> {
    val dosageImeClicks = drugDosageEditText
        .editorActions { it == EditorInfo.IME_ACTION_DONE }
        .map { Unit }

    return saveButton
        .clicks()
        .mergeWith(dosageImeClicks)
        .map { SaveCustomPrescriptionClicked }
  }

  private fun removeClicks(): Observable<UiEvent> =
      removeMedicineButton
          .clicks()
          .map { RemoveCustomPrescriptionClicked }

  override fun setSaveButtonEnabled(enabled: Boolean) {
    saveButton.isEnabled = enabled
  }

  override fun setDrugDosageText(text: String) {
    drugDosageEditText.setText(text)
  }

  override fun moveDrugDosageCursorToBeginning() {
    // Posting to EditText's handler is intentional. The cursor gets overridden otherwise.
    drugDosageEditText.post { drugDosageEditText.setSelection(0) }
  }

  override fun showEnterNewPrescriptionTitle() {
    enterMedicineTextView.visibility = VISIBLE
  }

  override fun showEditPrescriptionTitle() {
    editMedicineTextView.visibility = VISIBLE
  }

  override fun showRemoveButton() {
    removeMedicineButton.visibility = VISIBLE
  }

  override fun hideRemoveButton() {
    removeMedicineButton.visibility = GONE
  }

  override fun setMedicineName(drugName: String) {
    drugNameEditText.setTextAndCursor(drugName)
  }

  override fun setDosage(dosage: String?) {
    drugDosageEditText.setTextAndCursor(dosage ?: "")
  }

  override fun showConfirmRemoveMedicineDialog(prescribedDrugUuid: UUID) {
    ConfirmRemovePrescriptionDialog.showForPrescription(prescribedDrugUuid, supportFragmentManager)
  }

  companion object {
    private const val KEY_OPEN_AS = "openAs"

    fun intentForAddNewPrescription(context: Context, patientUuid: UUID): Intent {
      val intent = Intent(context, CustomPrescriptionEntrySheet::class.java)
      intent.putExtra(KEY_OPEN_AS, OpenAs.New(patientUuid))
      return intent
    }

    fun intentForUpdatingPrescription(context: Context, patientUuid: UUID, prescribedDrugUuid: UUID): Intent {
      val intent = Intent(context, CustomPrescriptionEntrySheet::class.java)
      intent.putExtra(KEY_OPEN_AS, OpenAs.Update(patientUuid, prescribedDrugUuid))
      return intent
    }
  }
}
