package org.simple.clinic.navigation

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.spotify.mobius.Connection
import com.spotify.mobius.EventSource
import com.spotify.mobius.First.first
import com.spotify.mobius.Init
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.Next.noChange
import com.spotify.mobius.Update
import com.spotify.mobius.android.MobiusAndroid
import com.spotify.mobius.extras.Connectables
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.rx2.RxMobius
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey.ARGS_KEY
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import org.simple.clinic.mobius.eventSources
import org.simple.clinic.util.unsafeLazy

abstract class BottomSheetFragment<M : Parcelable, E, F> : BottomSheetDialogFragment() {

  companion object {
    private const val KEY_MODEL = "model"
  }

  private val loop: MobiusLoop.Builder<M, E, F> by unsafeLazy {
    RxMobius
        .loop(createUpdate()::update, createEffectHandler())
        .init(createInit())
        .eventSources(additionalEventSources())
  }

  private val controller: MobiusLoop.Controller<M, E> by unsafeLazy {
    MobiusAndroid.controller(loop, defaultModel())
  }

  @LayoutRes
  abstract fun layoutResId(): Int

  abstract fun defaultModel(): M

  abstract fun onModelUpdate(model: M)

  open fun events(): Observable<E> = Observable.never()

  open fun createUpdate(): Update<M, E, F> = Update { _, _ -> noChange() }

  open fun createInit(): Init<M, F> = Init { model -> first(model) }

  open fun createEffectHandler(): ObservableTransformer<F, E> = ObservableTransformer { Observable.never() }

  open fun additionalEventSources(): List<EventSource<E>> = emptyList()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(layoutResId(), container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    controller.connect(Connectables.contramap({ it }, ::connectViews))

    if (savedInstanceState != null) {
      val savedModel = savedInstanceState.getParcelable<M>(KEY_MODEL)!!
      controller.replaceModel(savedModel)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    controller.disconnect()
  }

  override fun onResume() {
    super.onResume()
    controller.start()
  }

  override fun onPause() {
    super.onPause()
    controller.stop()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(KEY_MODEL, controller.model)
  }

  fun <T: BottomSheetFragmentKey> getKey(): T {
    // Set in `com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey#createFragment`
    return requireArguments().getParcelable<T>(ARGS_KEY)!!
  }

  private fun connectViews(output: Consumer<E>): Connection<M> {
    val eventsDisposable = events().subscribe(output::accept)

    return object : Connection<M> {

      override fun dispose() {
        eventsDisposable.dispose()
      }

      override fun accept(model: M) {
        onModelUpdate(model)
      }
    }
  }
}
