package com.backend.card.controller

object CardApiCodes {
    const val OK = "200"
    const val UNAUTHORIZED = "401"
    const val FORBIDDEN = "403"
    const val NOT_FOUND = "404"
}

object CardApiMessages {
    const val OK = "Card updated successfully"
    const val UNAUTHORIZED = "Authentication is required"
    const val FORBIDDEN = "Admin role is required"
    const val NOT_FOUND = "Card not found"
}
