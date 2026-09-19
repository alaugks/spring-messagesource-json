// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alaugks.spring.messagesource.base.records.TransFileInterface;
import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocale;
import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import io.github.alaugks.spring.messagesource.base.records.TransUnitInterface;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the README example that combines a content-based {@code targetLocaleResolver}
 * (locale read from the {@code targetLanguage} field) with a custom {@code jsonCatalog}
 * (translations read from the nested {@code translation} node).
 */
class JsonResourceMessageSourceCustomCatalogTest {

	@Test
	void test_messagesource_resolvesTranslationsFromNestedNode() {
		MessageSource messageSource = this.buildMessageSource();

		assertEquals("Postcode", messageSource.getMessage("postcode", null, Locale.forLanguageTag("en")));
		assertEquals("Postleitzahl", messageSource.getMessage("postcode", null, Locale.forLanguageTag("de")));
		assertEquals("Payment", messageSource.getMessage("payment.headline", null, Locale.forLanguageTag("en")));
		assertEquals("Zahlung", messageSource.getMessage("payment.headline", null, Locale.forLanguageTag("de")));
	}

	private MessageSource buildMessageSource() {
		return JsonResourceMessageSource
			.builder(Locale.forLanguageTag("en"), "custom-catalog/*")
			.targetLocaleResolver(resource -> {
				try (InputStream inputStream = resource.getInputStream()) {
					JsonNode json = new ObjectMapper().readTree(inputStream);
					String language = json.path("targetLanguage").asText();
					return new TransFileTargetLocale(language, null);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			})
			.jsonCatalog(translationFiles -> {
				List<TransUnitInterface> transUnits = new ArrayList<>();
				ObjectMapper objectMapper = new ObjectMapper();

				for (TransFileInterface file : translationFiles) {
					try {
						JsonNode translation = objectMapper.readTree(file.content()).path("translation");
						translation.properties().forEach(entry -> transUnits.add(
							new TransUnit(file.locale(), entry.getKey(), entry.getValue().asText())
						));
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}

				return transUnits;
			})
			.build();
	}
}
