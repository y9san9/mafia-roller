package com.y9san9.mafiaboost.utils.apihelper

import com.github.badoualy.telegram.tl.core.TLVector


fun <T> vectorOf(vararg items: T) = TLVector<T>().apply { addAll(items) }
