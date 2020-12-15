package org.simple.clinic.navigation.test

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_BACK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.screen_navigation_textentrysheet.*
import org.simple.clinic.R
import org.simple.clinic.navigation.v2.Router
import org.simple.clinic.navigation.v2.ScreenKey
import org.simple.clinic.navigation.v2.Succeeded
import org.simple.clinic.util.unsafeLazy

class TextEntrySheet : BottomSheetDialogFragment() {

  companion object {
    fun readEnteredText(result: Succeeded): String {
      return (result.result as ResultData).text
    }
  }

  private val router: Router by unsafeLazy {
    (requireActivity() as NavigationTestActivity).router
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(DialogFragment.STYLE_NORMAL, R.style.Clinic_V2_Theme_BottomSheetFragment)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.screen_navigation_textentrysheet, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    doneButton.setOnClickListener { router.popWithResult(Succeeded(ResultData(textEntryField.text.toString()))) }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return super.onCreateDialog(savedInstanceState).apply {
      // This is needed because the backstack is not aware of the changes
      // in the history when the bottom sheet dialog is dismissed in the
      // normal fashion.
      setCancelable(false)
      setCanceledOnTouchOutside(false)
      setOnKeyListener { _, keyCode, event ->
        if (event.action == ACTION_UP && keyCode == KEYCODE_BACK) {
          router.pop()
          true
        } else {
          false
        }
      }
    }
  }

  @Parcelize
  private data class ResultData(val text: String) : Parcelable

  @Parcelize
  class Key : ScreenKey() {
    override fun instantiateFragment(): Fragment {
      return TextEntrySheet()
    }

    override val type: ScreenType
      get() = ScreenType.Modal
  }
}
