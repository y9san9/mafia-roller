package com.y9san9.mafiaboost.mafia

import BOT_ID
import com.github.badoualy.telegram.tl.api.TLKeyboardButtonCallback
import com.github.badoualy.telegram.tl.api.TLKeyboardButtonUrl
import com.github.badoualy.telegram.tl.api.TLReplyInlineMarkup
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.utils.assert
import com.y9san9.mafiaboost.utils.click
import com.y9san9.mafiaboost.utils.safe
import java.lang.Thread.sleep


const val GAME_MESSAGE = "Ведётся набор в игру"
const val HEAL_MESSAGE = "Кого будем лечить?"
const val GAME_END = "Игра завершена"
const val ALL_DIED = "Все игроки мертвы!"
const val PLAYER_LACK = "Недостаточно игроков для начала игры..."

val ROLE_MESSAGES = arrayOf(
    "Ты - \uD83D\uDC68\uD83C\uDFFC Мирный житель. \n" +
            "Твоя задача вычислить мафию и на городском собрании линчевать засранцев",
    "Ты - \uD83D\uDC68\uD83C\uDFFC\u200D⚕️ Доктор! \n" +
            "Тебе решать кого спасти этой ночью...",
    "Ты - \uD83E\uDD35\uD83C\uDFFB Дон (глава мафии)! \n" +
            "Тебе решать кто не проснётся этой ночью..."
)
val INVITE_CODE_REGEX = Regex(".*\\?start=")

fun KotlogramClient.handler(controller: MafiaController, accountNumber: Int) = controller.apply {
    val newGameRunnable = Runnable {
        safe { sleep(25000) }
        if(!Thread.interrupted())
            gameFinished(0)
    }
    updates {
        var thread = Thread {}
        message({
            it.to.isChannel assert true
            it.to.id assert controller.chatId
            it.from.id assert BOT_ID
        }) {
            when (it.message) {
                GAME_MESSAGE -> (it.replyMarkup as? TLReplyInlineMarkup)?.also { markup ->
                    (markup.rows[0].buttons[0] as? TLKeyboardButtonUrl)?.also { button ->
                        gameInvitationReceived(accountNumber to button.url.replace(INVITE_CODE_REGEX, ""))
                    }
                }
            }
            var lack = false
            if (accountNumber == 0 && (it.message?.contains(Regex("окончена")) == true
                        || (it.message == PLAYER_LACK).also { bool -> lack = bool }
                        || it.message?.contains(ALL_DIED) == true)) {
                thread.interrupt()
                gameFinished(if(lack) 1 else 0)
            }
        }
        message({
            it.to.isUser assert true
            it.from.id assert BOT_ID
        }) {
            val index = ROLE_MESSAGES.indexOf(it.message)
            val userController = controller.controllers[accountNumber]
            if(index != -1) {
                if(accountNumber == 0){
                    if(index == 2){
                        userController.leave()
                        thread = Thread(newGameRunnable).apply { start() }
                    }
                } else {
                    userController.leave()
                }
            }
            if (accountNumber == 0) {
                if(it.message == HEAL_MESSAGE)
                    (it.replyMarkup as TLReplyInlineMarkup).rows[0].buttons[0].click(it)
                if(it.message?.contains(GAME_END) == true) {
                    thread = Thread(newGameRunnable).apply { start() }
                }
            }
        }
    }
}
