package com.y9san9.mafiaboost.utils

import com.github.badoualy.telegram.tl.api.TLAbsKeyboardButton
import com.github.badoualy.telegram.tl.api.TLKeyboardButton
import com.github.badoualy.telegram.tl.api.TLKeyboardButtonCallback
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.Message


fun TLAbsKeyboardButton.click(message: Message, game: Boolean = false): Unit = when(this){
    is TLKeyboardButton -> message.client.sendMessage(
        message.to.input, text
    )
    is TLKeyboardButtonCallback -> message.client.client.messagesGetBotCallbackAnswer(
        game, message.to.input, message.id, data
    ).let { }
    else -> throw UnsupportedOperationException()
}
