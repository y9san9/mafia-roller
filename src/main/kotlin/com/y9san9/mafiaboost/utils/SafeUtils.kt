package com.y9san9.mafiaboost.utils


fun <T> safe(handler: () -> T) = try {
    handler()
} catch (_: Throwable){ null }
