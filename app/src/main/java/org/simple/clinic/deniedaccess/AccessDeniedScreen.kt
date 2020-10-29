package org.simple.clinic.deniedaccess

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.screen_access_denied.view.*
import org.simple.clinic.di.injector
import org.simple.clinic.navigation.ScreenKeyProvider
import org.simple.clinic.util.unsafeLazy
import javax.inject.Inject

class AccessDeniedScreen(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

  @Inject
  lateinit var activity: AppCompatActivity

  @Inject
  lateinit var screenKeyProvider: ScreenKeyProvider

  private val screenKey by unsafeLazy {
    screenKeyProvider.provide<AccessDeniedScreenKey>(this)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    if (isInEditMode) {
      return
    }
    context.injector<AccessDeniedScreenInjector>().inject(this)

    userFullNameText.text = screenKey.fullName
  }
}
