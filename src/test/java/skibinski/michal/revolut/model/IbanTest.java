package skibinski.michal.revolut.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class IbanTest {

  @Test
  public void canBeCreateValid() {
    new Iban("AL35202111090000000001234567");
    new Iban("AD1400080001001234567890");
    new Iban("AT483200000012345864");
    new Iban("BY86AKBB10100000002966000000");
    new Iban("BG18RZBB91550123456789");
    new Iban("HR1723600001101234565");
  }

  @Test(expected = IbanFormatException.class)
  public void cannotBeCreatedInvalid() {
    Assertions.assertThat(new Iban("AL35202111090000000001234568"));
  }
}