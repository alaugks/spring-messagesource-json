/*
 * Copyright 2023-2025 André Laugks <alaugks@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alaugks.spring.messagesource.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.alaugks.spring.messagesource.catalog.records.TransUnitInterface;
import io.github.alaugks.spring.messagesource.catalog.records.TranslationFile;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import io.github.alaugks.spring.messagesource.catalog.resources.ResourcesLoader;
import io.github.alaugks.spring.messagesource.json.exception.JsonResourceMessageSourceIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class JsonCatalogTest {

	@Test
	void test_getTransUnits() {

		var ressourceLoader = new ResourcesLoader(
				Locale.forLanguageTag("en"),
				new LocationPattern(List.of("translations/messages.json", "translations/messages_de.json")),
				List.of("json")
		);

		var catalog = new JsonCatalog(ressourceLoader.getTranslationFiles());
		var transUnits = catalog.getTransUnits();

		assertEquals("Postcode", this.findInTransUnits(transUnits, "en", "postcode"));
		assertEquals("Postleitzahl", this.findInTransUnits(transUnits, "de", "postcode"));
	}

	@Test
	void test_IOException() {
		List<TranslationFile> list = new ArrayList<>();
		list.add(new TranslationFile(
			"domain",
			Locale.forLanguageTag("en"),
			"{ invalid json".getBytes()
		));

		var catalog = new JsonCatalog(list);

		assertThrows(JsonResourceMessageSourceIOException.class, catalog::getTransUnits);
	}

	private String findInTransUnits(List<TransUnitInterface> transUnits, String locale, String code) {
		return transUnits
				.stream()
				.filter(t -> t.locale().toString().equals(locale) && t.code().equals(code))
				.findFirst()
				.get().value();
	}
}
