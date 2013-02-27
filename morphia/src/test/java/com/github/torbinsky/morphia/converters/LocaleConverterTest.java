/**
 *
 */
package com.github.torbinsky.morphia.converters;

import java.util.Locale;


import org.junit.Test;

import com.github.torbinsky.morphia.TestBase;
import org.junit.Assert;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 *
 */
public class LocaleConverterTest extends TestBase {
	@Test
	public void testConv() throws Exception {
		LocaleConverter c = new LocaleConverter();

		Locale l = Locale.CANADA_FRENCH;
		Locale l2 = (Locale) c.decode(Locale.class, c.encode(l));
		Assert.assertEquals(l, l2);

		l = new Locale("de", "DE", "bavarian");
		l2 = (Locale) c.decode(Locale.class, c.encode(l));
		Assert.assertEquals(l, l2);
		Assert.assertEquals("de", l2.getLanguage());
		Assert.assertEquals("DE", l2.getCountry());
		Assert.assertEquals("bavarian", l2.getVariant());

	}
}
