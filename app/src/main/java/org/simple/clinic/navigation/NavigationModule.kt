package org.simple.clinic.navigation

import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.navigator.Navigator
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {

  @Provides
  fun provideBackstack(activity: AppCompatActivity): Backstack {
    return Navigator.getBackstack(activity)
  }

  @Provides
  fun provideScreenKeyProvider(backstack: Backstack): ScreenKeyProvider {
    return BackstackBasedScreenKeyProvider(backstack)
  }
}
