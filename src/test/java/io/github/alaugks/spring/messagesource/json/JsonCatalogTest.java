package io.github.alaugks.spring.messagesource.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.alaugks.spring.messagesource.catalog.records.TransUnitInterface;
import io.github.alaugks.spring.messagesource.catalog.records.TranslationFile;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import io.github.alaugks.spring.messagesource.catalog.resources.ResourcesLoader;
import io.github.alaugks.spring.messagesource.json.exception.JsonResourceMessageSourceIOException;
import java.io.IOException;
import java.io.InputStream;
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
		catalog.build();
		var transUnits = catalog.getTransUnits();

		assertEquals("Postcode", this.findInTransUnits(transUnits, "en", "postcode"));
		assertEquals("Postleitzahl", this.findInTransUnits(transUnits, "de", "postcode"));
	}

	@Test
	void test_IOException() {

		try {
			InputStream inputStreamMock = mock(InputStream.class);
			when(inputStreamMock.readAllBytes()).thenThrow(IOException.class);

			List<TranslationFile> list = new ArrayList<>();
			list.add(new TranslationFile(
				"domain",
				Locale.forLanguageTag("en"),
				inputStreamMock
			));

			var catalog = new JsonCatalog(list);

			assertThrows(JsonResourceMessageSourceIOException.class, catalog::build);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private String findInTransUnits(List<TransUnitInterface> transUnits, String locale, String code) {
		return transUnits
				.stream()
				.filter(t -> t.locale().toString().equals(locale) && t.code().equals(code))
				.findFirst()
				.get().value();
	}
}
