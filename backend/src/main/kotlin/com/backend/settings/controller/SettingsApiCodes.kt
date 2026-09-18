package com.backend.settings.controller

object SettingsApiCodes {
    const val OK = "200"
    const val CREATED = "201"
    const val NO_CONTENT = "204"
    const val UNAUTHORIZED = "401"
    const val FORBIDDEN = "403"
    const val NOT_FOUND = "404"
    const val CONFLICT = "409"
}

object SettingsApiMessages {
    const val OK = "Successful response"
    const val CREATED = "Settings created"
    const val NO_CONTENT = "Settings deleted"
    const val UNAUTHORIZED = "Authentication required"
    const val FORBIDDEN = "Admin role required"
    const val NOT_FOUND = "Settings not found"
    const val CONFLICT = "Settings already exist"
}
