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

import io.github.alaugks.spring.messagesource.catalog.resources.LocationPattern;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class JsonResourceMessageSourceTest {

	@Test
	void test_getMessage_code_args_locale() {
		var messageSource = JsonResourceMessageSource
				.builder(Locale.forLanguageTag("en"), new LocationPattern("translations/*"))
				.build();

		assertEquals("Postcode", messageSource.getMessage(
				"postcode",
				null,
				Locale.forLanguageTag("en")
		));

		assertEquals("Postleitzahl", messageSource.getMessage(
				"postcode",
				null,
				Locale.forLanguageTag("de")
		));
	}

	@Test
	void test_defaultDomain() {
		var messageSource = JsonResourceMessageSource
				.builder(Locale.forLanguageTag("en"), new LocationPattern("translations/*"))
				.defaultDomain("payment")
				.build();

		assertEquals("Expiry date", messageSource.getMessage(
				"expiry_date",
				null,
				Locale.forLanguageTag("en")
		));
	}
}
