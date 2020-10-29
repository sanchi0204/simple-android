package org.simple.clinic.activity

import dagger.BindsInstance
import org.simple.clinic.router.ScreenResultBus

interface BindsScreenResults<B> {

  @BindsInstance
  fun screenResults(screenResults: ScreenResultBus): B
}
