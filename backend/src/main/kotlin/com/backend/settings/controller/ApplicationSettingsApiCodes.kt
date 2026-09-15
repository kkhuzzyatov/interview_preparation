package com.backend.settings.controller

object ApplicationSettingsApiCodes {
    const val OK = "200"
    const val CREATED = "201"
    const val NO_CONTENT = "204"
    const val UNAUTHORIZED = "401"
    const val FORBIDDEN = "403"
    const val NOT_FOUND = "404"
    const val CONFLICT = "409"
}

object ApplicationSettingsApiMessages {
    const val OK = "Successful response"
    const val CREATED = "Application settings created"
    const val NO_CONTENT = "Application settings deleted"
    const val UNAUTHORIZED = "Authentication required"
    const val FORBIDDEN = "Admin role required"
    const val NOT_FOUND = "Application settings not found"
    const val CONFLICT = "Application settings already exist"
}
