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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alaugks.spring.messagesource.catalog.catalog.AbstractCatalog;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnitInterface;
import io.github.alaugks.spring.messagesource.catalog.records.TranslationFile;
import io.github.alaugks.spring.messagesource.json.exception.JsonResourceMessageSourceIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonCatalog extends AbstractCatalog {

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
		try {
			ObjectMapper mapper = new ObjectMapper();

			for (TranslationFile file : translationFiles) {

				HashMap<String, Object> items = mapper.readValue(
					new String(file.content()),
					new TypeReference<>() {
					}
				);

				for (Map.Entry<String, Object> item : items.entrySet()) {
					transUnits.add(new TransUnit(
						file.locale(),
						item.getKey(),
						item.getValue().toString(),
						file.domain()
					));
				}
			}
		} catch (IOException e) {
			throw new JsonResourceMessageSourceIOException(e);
		}

		return transUnits;
	}
}
