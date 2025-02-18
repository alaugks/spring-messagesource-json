package io.github.alaugks.spring.messagesource.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alaugks.spring.messagesource.catalog.records.TransUnitInterface;
import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import io.github.alaugks.spring.messagesource.catalog.resources.ResourcesLoader;
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

	private String findInTransUnits(List<TransUnitInterface> transUnits, String locale, String code) {
		return transUnits
				.stream()
				.filter(t -> t.locale().toString().equals(locale) && t.code().equals(code))
				.findFirst()
				.get().value();
	}
}
