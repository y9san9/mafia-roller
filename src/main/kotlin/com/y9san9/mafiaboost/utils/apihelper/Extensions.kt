package com.y9san9.mafiaboost.utils.apihelper

import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.api.utils.id
import com.github.badoualy.telegram.api.utils.is1v1
import com.github.badoualy.telegram.api.utils.isChannel
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.exception.RpcErrorException


val TLMessage.is1v1 get() = toId.is1v1
val TLMessage.isChannel get() = toId.isChannel

val TLMessage.toIntId get() = toId.id!!


fun TelegramClient.auth(phone: String, codeHandler: () -> String): Unit = try {
    val sentCode = authSendCode(false, phone, true)
    val confirmationCode = codeHandler()
    val auth = authSignIn(phone, sentCode.phoneCodeHash, confirmationCode)
    println("Signed in as ${auth.user.asUser.firstName}")
} catch (e: RpcErrorException) {
    if (e.code == 500) {
        auth(phone, codeHandler)
    } else {
        throw e
    }
}

fun TelegramClient.loadChannel(id: Int): TLAbsChat
        = channelsGetChannels(vectorOf(TLInputChannel(id, 0))).chats[0]
