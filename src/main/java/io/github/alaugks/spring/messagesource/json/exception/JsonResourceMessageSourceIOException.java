// SPDX-License-Identifier: Apache-2.0
// Copyright 2023 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.json.exception;

public class JsonResourceMessageSourceIOException extends RuntimeException {

	public JsonResourceMessageSourceIOException(String message, Throwable cause) {
		super(message, cause);
	}
}
