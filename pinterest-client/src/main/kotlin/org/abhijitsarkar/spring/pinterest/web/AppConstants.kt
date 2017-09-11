package org.abhijitsarkar.spring.pinterest.web

/**
 * @author Abhijit Sarkar
 */
const val OAUTH = "/oauth"
const val OAUTH_TOKEN = "$OAUTH/token"
const val PINTEREST = "/pinterest"
const val BOARD = "$PINTEREST/boards/{name}"
const val PIN = "$PINTEREST/pins"

const val ACCESS_TOKEN_CACHE_KEY = "accessToken"