// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json.exception;

/**
 * Thrown when a JSON translation file cannot be read or parsed.
 * <p>Wraps the underlying {@link java.io.IOException} (or other parser error)
 * as the cause and adds context about the affected domain and locale in the
 * message.
 */
public class JsonResourceMessageSourceIOException extends RuntimeException {

	/**
	 * Creates a new exception with the given message and underlying cause.
	 *
	 * @param message description of the failure, typically including the
	 *                affected domain and locale.
	 * @param cause   the underlying parsing or I/O error.
	 */
	public JsonResourceMessageSourceIOException(String message, Throwable cause) {
		super(message, cause);
	}
}
