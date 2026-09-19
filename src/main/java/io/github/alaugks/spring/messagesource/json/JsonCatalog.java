// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alaugks.spring.messagesource.base.records.TransFileInterface;
import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import io.github.alaugks.spring.messagesource.base.records.TransUnitInterface;
import io.github.alaugks.spring.messagesource.json.exception.JsonResourceMessageSourceIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;

/**
 * Catalog implementation that reads translation units from JSON files.
 * <p>Each JSON file is expected to contain a flat map of translation code to
 * value. The {@code locale} and {@code domain} are taken from the
 * {@link TransFileInterface} metadata, not from the file content itself.
 */
class JsonCatalog implements JsonCatalogInterface {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Returns the translation units parsed from all configured JSON files.
	 *
	 * @return list of all translation units across the configured files; never {@code null}.
	 * @throws JsonResourceMessageSourceIOException if a file cannot be read or parsed as JSON.
	 */
	@Override
	public List<TransUnitInterface> getTransUnits(List<TransFileInterface> translationFiles) {
		List<TransUnitInterface> transUnits = new ArrayList<>();

		for (TransFileInterface file : translationFiles) {
			Map<String, @Nullable Object> items;
			try {
				items = OBJECT_MAPPER.readValue(
					file.content(),
					new TypeReference<>() {
					}
				);
			} catch (IOException e) {
				throw new JsonResourceMessageSourceIOException(
					String.format(
						"Failed to parse JSON translation file: %s",
						Optional.ofNullable(file.resource())
							.map(Resource::getDescription)
							.orElse("unknown")
					),
					e
				);
			}

			for (Map.Entry<String, @Nullable Object> item : items.entrySet()) {
				Object value = item.getValue();
				if (value == null) {
					continue;
				}
				transUnits.add(new TransUnit(
					file.locale(),
					item.getKey(),
					value.toString()
				));
			}
		}

		return transUnits;
	}
}
