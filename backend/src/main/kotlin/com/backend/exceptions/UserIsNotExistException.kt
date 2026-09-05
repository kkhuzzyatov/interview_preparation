package com.backend.exceptions

class UserIsNotExistException(
    message: String,
) : RuntimeException(message)
