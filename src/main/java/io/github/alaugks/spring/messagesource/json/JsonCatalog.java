// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alaugks.spring.messagesource.catalog.catalog.AbstractCatalog;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnitInterface;
import io.github.alaugks.spring.messagesource.catalog.records.TranslationFile;
import io.github.alaugks.spring.messagesource.json.exception.JsonResourceMessageSourceIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonCatalog extends AbstractCatalog {

	private final ObjectMapper mapper = new ObjectMapper();

	private final List<TranslationFile> translationFiles;

	/**
	 * Creates a new catalog that parses the given JSON translation files.
	 *
	 * @param translationFiles JSON files to parse.
	 */
	public JsonCatalog(List<TranslationFile> translationFiles) {
		this.translationFiles = translationFiles;
	}

	/**
	 * Returns the translation units parsed from all configured JSON files.
	 *
	 * @return list of all translation units across the configured files; never {@code null}.
	 * @throws JsonResourceMessageSourceIOException if a file cannot be read or parsed as JSON.
	 */
	@Override
	public List<TransUnitInterface> getTransUnits() {
		List<TransUnitInterface> transUnits = new ArrayList<>();

		for (TranslationFile file : translationFiles) {
			Map<String, Object> items;
			try {
				items = this.mapper.readValue(
					file.content(),
					new TypeReference<>() {
					}
				);
			} catch (IOException e) {
				throw new JsonResourceMessageSourceIOException(
					String.format(
						"Failed to parse JSON translation file (domain=%s, locale=%s)",
						file.domain(),
						file.locale()
					),
					e
				);
			}

			for (Map.Entry<String, Object> item : items.entrySet()) {
				Object value = item.getValue();
				if (value == null) {
					continue;
				}
				transUnits.add(new TransUnit(
					file.locale(),
					item.getKey(),
					value.toString(),
					file.domain()
				));
			}
		}

		return transUnits;
	}
}
