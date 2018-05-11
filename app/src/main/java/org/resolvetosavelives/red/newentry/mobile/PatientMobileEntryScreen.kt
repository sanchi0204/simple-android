package org.resolvetosavelives.red.newentry.mobile

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import kotterknife.bindView
import org.resolvetosavelives.red.R
import org.resolvetosavelives.red.TheActivity
import org.resolvetosavelives.red.home.HomeScreen
import org.resolvetosavelives.red.newentry.search.OngoingPatientEntry
import java.util.UUID

class PatientMobileEntryScreen(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

  companion object {
    val KEY = PatientMobileEntryScreenKey()
  }

  private val primaryNumberEditText by bindView<EditText>(R.id.patientmobile_primary_number)
  private val proceedButton by bindView<Button>(R.id.patiententry_mobile_proceed)

  override fun onFinishInflate() {
    super.onFinishInflate()
    if (isInEditMode) {
      return
    }

    // TODO: threading.
    val repository = TheActivity.patientRepository()
    repository
        .ongoingEntry()
        .subscribe({ entry ->
          primaryNumberEditText.setText(entry.mobileNumber)
        })

    nextButton.setOnClickListener({
      // TODO: threading.
      repository.ongoingEntry()
          .map { entry -> entry.copy(mobileNumber = primaryNumberEditText.text.toString()) }
          .flatMapCompletable { entry: OngoingPatientEntry -> repository.save(entry) }
          .andThen(repository.saveOngoingEntry(UUID.randomUUID()))
          .subscribe({
            TheActivity.screenRouter().push(HomeScreen.KEY)
          })
    })
  }
}

