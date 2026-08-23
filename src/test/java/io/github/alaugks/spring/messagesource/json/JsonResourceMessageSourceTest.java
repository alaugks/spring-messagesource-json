// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonResourceMessageSourceTest {

	@ParameterizedTest
	@MethodSource("dataProvider_getMessage_code_args_locale")
	void test_messagesource_locationpattern(String expected, String code, Object[] args, String locale) {
		var messageSource = JsonResourceMessageSource
				.builder(Locale.forLanguageTag("en"), new LocationPattern("translations/*"))
				.enableICU4j()
				.build();

		assertEquals(expected, messageSource.getMessage(
				code,
				args,
				Locale.forLanguageTag(locale)
		));
	}

	@ParameterizedTest
	@MethodSource("dataProvider_getMessage_code_args_locale")
	void test_messagesource_string(String expected, String code, Object[] args, String locale) {
		var messageSource = JsonResourceMessageSource
			.builder(Locale.forLanguageTag("en"), "translations/*")
			.enableICU4j()
			.build();

		assertEquals(expected, messageSource.getMessage(
			code,
			args,
			Locale.forLanguageTag(locale)
		));
	}

	@ParameterizedTest
	@MethodSource("dataProvider_getMessage_code_args_locale")
	void test_messagesource_list(String expected, String code, Object[] args, String locale) {
		var messageSource = JsonResourceMessageSource
			.builder(Locale.forLanguageTag("en"), List.of("translations/*"))
			.enableICU4j()
			.build();

		assertEquals(expected, messageSource.getMessage(
			code,
			args,
			Locale.forLanguageTag(locale)
		));
	}

	static Stream<Arguments> dataProvider_getMessage_code_args_locale() {
		return Stream.of(
			Arguments.of("Postcode", "postcode", null, "en"),
			Arguments.of("Postleitzahl", "postcode", null, "de"),
			Arguments.of(
				"Sie haben 5 Dateien gelöscht.",
				"plural.file_deleted_icu4j",
				new Object[] {Map.of("count", 5)},
				"de"
			),
			Arguments.of(
				"Sie haben 5 Dateien gelöscht.",
				"plural.file_deleted",
				new Object[] {5},
				"de"
			)
		);
	}

	@Test
	void test_defaultDomain() {
		var messageSource = JsonResourceMessageSource
				.builder(Locale.forLanguageTag("en"), "translations/*")
				.defaultDomain("payment")
				.enableICU4j()
				.build();

		assertEquals("Expiry date", messageSource.getMessage(
				"expiry_date",
				null,
				Locale.forLanguageTag("en")
		));
	}

	@Test
	void test_domainDivider() {
		var messageSource = JsonResourceMessageSource
			.builder(Locale.forLanguageTag("en"), "translations/*")
			.enableICU4j()
			.domainDivider("__")
			.build();

		assertEquals("Expiry date", messageSource.getMessage(
			"payment__expiry_date",
			null,
			Locale.forLanguageTag("en")
		));
	}
}
