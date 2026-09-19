// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import io.github.alaugks.spring.messagesource.base.records.TransUnitInterface;
import io.github.alaugks.spring.messagesource.base.resources.ResourceLoaderBuilder;
import io.github.alaugks.spring.messagesource.json.exception.JsonResourceMessageSourceIOException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonCatalogTest {

	@Test
	void test_getTransUnits() {

		var resourceLoader = ResourceLoaderBuilder.builder(
				Locale.forLanguageTag("en"),
				List.of("translations/messages.json", "translations/messages_de.json")
		).fileExtensions(List.of("json")).build();

		var catalog = new JsonCatalog();
		var transUnits = catalog.getTransUnits(resourceLoader.getTranslationFiles());

		assertEquals("Postcode", this.findInTransUnits(transUnits, "en", "postcode"));
		assertEquals("Postleitzahl", this.findInTransUnits(transUnits, "de", "postcode"));
		assertFalse(transUnits.stream().anyMatch(t -> t.code().equals("null_value")));
	}

	@Test
	void test_IOException() {
		var resourceLoader = ResourceLoaderBuilder.builder(
			Locale.forLanguageTag("en"),
			List.of("fixtures/*")
		).fileExtensions(List.of("json")).build();

		var catalog = new JsonCatalog();
		var translationFiles = resourceLoader.getTranslationFiles();

		assertThrows(
			JsonResourceMessageSourceIOException.class,
			() -> catalog.getTransUnits(translationFiles)
		);
	}

	private String findInTransUnits(List<TransUnitInterface> transUnits, String locale, String code) {
		return transUnits
				.stream()
				.filter(t -> t.locale().toString().equals(locale) && t.code().equals(code))
				.findFirst()
				.get().value();
	}
}
