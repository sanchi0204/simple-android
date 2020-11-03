package org.simple.clinic.router

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ScopedServices
import org.simple.clinic.router.screen.FullScreenKey

/**
 * Wrapper fragment used for embedding older View-based screens so that
 * we can migrate older screens to fragments over time. Do not use this
 * for new screens.
 **/
class ScreenWrapperFragment : Fragment(), ScopedServices.HandlesBack {

  companion object {
    private const val ARG_SCREEN_KEY = "ScreenWrapperFragment.ARG_SCREEN_KEY"

    fun create(key: FullScreenKey): Fragment {
      val fragment = ScreenWrapperFragment()

      val args = Bundle().apply {
        putParcelable(ARG_SCREEN_KEY, key)
      }

      fragment.arguments = args
      return fragment
    }
  }

  val key by lazy(LazyThreadSafetyMode.NONE) {
    requireArguments().getParcelable<FullScreenKey>(ARG_SCREEN_KEY)!!
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(key.layoutRes(), container, false)
  }

  override fun onBackEvent(): Boolean {
    val contentView = requireView()

    return if (contentView is ScopedServices.HandlesBack)
      contentView.onBackEvent()
    else
      false
  }
}
