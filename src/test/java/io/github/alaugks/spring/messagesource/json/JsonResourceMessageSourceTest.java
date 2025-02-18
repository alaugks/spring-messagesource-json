package io.github.alaugks.spring.messagesource.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class JsonResourceMessageSourceTest {

	@Test
	void test_getMessage_code_args_locale() {
		var messageSource = JsonResourceMessageSource
				.builder(Locale.forLanguageTag("en"), new LocationPattern("translations/*"))
				.build();

		assertEquals("Postcode", messageSource.getMessage(
				"postcode",
				null,
				Locale.forLanguageTag("en")
		));

		assertEquals("Postleitzahl", messageSource.getMessage(
				"postcode",
				null,
				Locale.forLanguageTag("de")
		));
	}

	@Test
	void test_defaultDomain() {
		var messageSource = JsonResourceMessageSource
				.builder(Locale.forLanguageTag("en"), new LocationPattern("translations/*"))
				.defaultDomain("payment")
				.build();

		assertEquals("Expiry date", messageSource.getMessage(
				"expiry_date",
				null,
				Locale.forLanguageTag("en")
		));
	}
}
