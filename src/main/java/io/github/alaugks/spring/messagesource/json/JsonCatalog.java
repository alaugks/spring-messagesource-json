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
import org.springframework.stereotype.Component;

@Component
public class JsonCatalog extends AbstractCatalog {

	private final List<TransUnitInterface> transUnits = new ArrayList<>();
	private final List<TranslationFile> translationFiles;

	public JsonCatalog(List<TranslationFile> translationFiles) {
		this.translationFiles = translationFiles;
	}

	@Override
	public List<TransUnitInterface> getTransUnits() {
		return this.transUnits;
	}

	@Override
	public void build() {

		try {
			ObjectMapper mapper = new ObjectMapper();

			for (TranslationFile file : translationFiles) {

				HashMap<String, Object> items = mapper.readValue(
					new String(file.inputStream().readAllBytes()),
					new TypeReference<>() {
					}
				);

				for (Map.Entry<String, Object> item : items.entrySet()) {
					this.transUnits.add(new TransUnit(
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
	}
}
