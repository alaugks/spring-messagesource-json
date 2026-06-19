// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonResourceMessageSourceTest {

	@Test
	void test_getMessage_code_args_locale() {
		var messageSource = JsonResourceMessageSource
				.builder(Locale.forLanguageTag("en"), new LocationPattern("translations/*"))
				.enableICU4j()
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

		assertEquals("Sie haben 5 Dateien gelöscht.", messageSource.getMessage(
			"plural.file_deleted_icu4j",
			new Object[] {Map.of("count", 5)},
			Locale.forLanguageTag("de")
		));

		assertEquals("Sie haben 5 Dateien gelöscht.", messageSource.getMessage(
			"plural.file_deleted",
			new Object[] {5},
			Locale.forLanguageTag("de")
		));
	}

	@Test
	void test_defaultDomain() {
		var messageSource = JsonResourceMessageSource
				.builder(Locale.forLanguageTag("en"), new LocationPattern("translations/*"))
				.defaultDomain("payment")
				.enableICU4j()
				.build();

		assertEquals("Expiry date", messageSource.getMessage(
				"expiry_date",
				null,
				Locale.forLanguageTag("en")
		));
	}
}
