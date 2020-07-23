package com.y9san9.mafiaboost.mafia

import BOT_ID
import CHAT_ID
import com.github.badoualy.telegram.tl.api.TLKeyboardButtonCallback
import com.github.badoualy.telegram.tl.api.TLKeyboardButtonUrl
import com.github.badoualy.telegram.tl.api.TLReplyInlineMarkup
import com.y9san9.mafiaboost.utils.apihelper.*


const val GAME_MESSAGE = "Ведётся набор в игру"
val INVITE_CODE_REGEX = Regex(".*\\?start=")

fun MafiaController.handler(accountNumber: Int) = updatesHandler {
    message(toChatId = CHAT_ID, fromUser = BOT_ID) {
        println(it.message)
        when(it.message){
            GAME_MESSAGE -> (it.replyMarkup as? TLReplyInlineMarkup)?.also { markup ->
                (markup.rows[0].buttons[0] as? TLKeyboardButtonUrl)?.also { button ->
                    onGameInvitation(accountNumber to button.url.replace(INVITE_CODE_REGEX, ""))
                }
            }
        }
        if(accountNumber == 0 && (it.message.contains("окончена", true)
                        || it.message.contains("закончилась", true))
                || it.message.contains("недостаточно", true))
            gameFinished()
    }
    message(fromUser = BOT_ID, toChat = false){
        println(it.message)
    }
}

fun storage(name: String) = ApiStorage(name)
