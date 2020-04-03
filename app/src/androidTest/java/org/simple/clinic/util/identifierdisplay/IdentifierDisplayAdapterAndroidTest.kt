package org.simple.clinic.util.identifierdisplay

import com.google.common.truth.Truth.assertWithMessage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.simple.clinic.TestClinicApp
import org.simple.clinic.patient.businessid.Identifier
import org.simple.clinic.patient.businessid.Identifier.IdentifierType
import org.simple.clinic.patient.businessid.Identifier.IdentifierType.BangladeshNationalId
import org.simple.clinic.patient.businessid.Identifier.IdentifierType.BpPassport
import org.simple.clinic.patient.businessid.Identifier.IdentifierType.Unknown
import org.simple.clinic.util.Rules


class IdentifierDisplayAdapterAndroidTest {

  @get:Rule
  val ruleChain: RuleChain = Rules.global()

  @Before
  fun setUp() {
    TestClinicApp.appComponent().inject(this)
  }

  @Test
  fun all_types_of_identifiers_should_have_a_display_value() {
    // given
    val testData = IdentifierType
        .values()
        .map(::generateIdentifierTestData)

    // then
    testData.forEach { (identifier, expectedValue) ->

      assertWithMessage("formatting Identifier of type [${identifier.type}]")
          .that(identifier.displayValue())
          .isEqualTo(expectedValue)
    }
  }

  private fun generateIdentifierTestData(type: IdentifierType): IdentifierTestData {
    return when (type) {
      BpPassport -> {
        val bpPassportId = "4b1de973-48e6-4f27-a384-65174748f0b1"
        val bpPassport = Identifier(value = bpPassportId, type = BpPassport)

        IdentifierTestData(identifier = bpPassport, expectedDisplayValue = "419\u00A07348")
      }
      BangladeshNationalId -> {
        val bangladeshNationalIdValue = "123456783456"
        val bangladeshNationalId = Identifier(value = bangladeshNationalIdValue, type = BangladeshNationalId)

        IdentifierTestData(identifier = bangladeshNationalId, expectedDisplayValue = bangladeshNationalIdValue)
      }
      is Unknown -> {
        val someOtherIdValue = "asdf-2345-rftg"
        val unknownId = Identifier(value = someOtherIdValue, type = Unknown("some-other-id"))

        IdentifierTestData(identifier = unknownId, expectedDisplayValue = someOtherIdValue)
      }
    }
  }

  private data class IdentifierTestData(
      val identifier: Identifier,
      val expectedDisplayValue: String
  )
}
