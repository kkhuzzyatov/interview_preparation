package com.backend.exceptions

class ApplicationSettingsNotFoundException(
    message: String,
) : RuntimeException(message)
