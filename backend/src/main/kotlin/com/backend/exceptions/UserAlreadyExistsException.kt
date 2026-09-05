package com.backend.exceptions

class UserAlreadyExistsException(
    message: String,
) : RuntimeException(message)
