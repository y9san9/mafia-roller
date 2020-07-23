package com.y9san9.mafiaboost.utils

import java.io.File


fun File.refresh(isDirectory: Boolean = false) = if(isDirectory){
    mkdirs()
} else {
    parentFile.mkdirs()
    createNewFile()
}.let {  }
