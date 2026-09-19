package io.github.alaugks.spring.messagesource.json;

import io.github.alaugks.spring.messagesource.base.records.TransFileInterface;
import io.github.alaugks.spring.messagesource.base.records.TransUnitInterface;
import java.util.List;

public interface JsonCatalogInterface {

	/**
	 * Retrieves a list of translation units available in the catalog.
	 *
	 * @return a list of translation units, representing all parsed translations
	 *         across the configured JSON files; never null but may be empty.
	 */
	List<TransUnitInterface> getTransUnits(List<TransFileInterface> translationFiles);
}
