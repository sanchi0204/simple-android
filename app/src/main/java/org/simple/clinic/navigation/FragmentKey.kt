package org.simple.clinic.navigation

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add

abstract class FragmentKey : DefaultFragmentKey(), DefaultServiceProvider.HasServices {

  override fun getScopeTag() = ""

  override fun bindServices(serviceBinder: ServiceBinder) {
    serviceBinder.add(serviceBinder.lookupService<BackPressHandler>("backpress"))
  }
}
