package com.y9san9.mafiaboost.mafia

import API_HASH
import API_ID
import APP_VERSION
import BOT_ID
import CHAT_ID
import LANG_CODE
import MODEL
import SYSTEM_VERSION
import com.github.badoualy.telegram.api.Kotlogram
import com.github.badoualy.telegram.api.TelegramApp
import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.*
import com.y9san9.mafiaboost.utils.apihelper.auth
import com.y9san9.mafiaboost.utils.apihelper.getUser
import com.y9san9.mafiaboost.utils.apihelper.loadChannel
import java.lang.Thread.sleep
import kotlin.random.Random


const val START_GAME_COMMAND = "/game"
const val FORCE_START_GAME_COMMAND = "/start"


class MafiaController {
    var playersJoined = 0

    var gameInvitationReceived: (Pair<Int, String>) -> Unit = {}
    var allJoined: () -> Unit = {}
    var gameFinished: () -> Unit = {}

    private val app = TelegramApp(API_ID, API_HASH, MODEL, SYSTEM_VERSION, APP_VERSION, LANG_CODE)

    private val client1 = Kotlogram.getDefaultClient(app, storage("0"), updateCallback = handler(0))
    private val client2 = Kotlogram.getDefaultClient(app, storage("1"), updateCallback = handler(1))
    private val client3 = Kotlogram.getDefaultClient(app, storage("2"), updateCallback = handler(2))
    private val client4 = Kotlogram.getDefaultClient(app, storage("3"), updateCallback = handler(3))

    private val clients = arrayOf(client1, client2, client3, client4)

    private val controller1 = UserController(client1)
    private val controller2 = UserController(client2)
    private val controller3 = UserController(client3)
    private val controller4 = UserController(client4)

    private val controllers = arrayOf(controller1, controller2, controller3, controller4)

    fun auth(phone1: String, phone2: String, phone3: String, phone4: String, codeHandler: (Int) -> String){
        client1.auth(phone1){ codeHandler(0) }
        client2.auth(phone2){ codeHandler(1) }
        client3.auth(phone3){ codeHandler(2) }
        client4.auth(phone4){ codeHandler(3) }
    }

    fun startGame() {
        controllers.forEach { it.joined = false }
        playersJoined = 0
        client1.messagesSendMessage(controller1.channelPeer, START_GAME_COMMAND, Random.nextLong())
    }
    fun forceStart() = client1.messagesSendMessage(controller1.channelPeer, FORCE_START_GAME_COMMAND, Random.nextLong())

    fun joinGame(user: Int, code: String){
        if(controllers[user].join(code)) {
            playersJoined++
            if (playersJoined == 4) {
                sleep(300)
                allJoined()
            }
        }
    }

    fun leaveOthers() = controllers.sliceArray(1..3).forEach { it.leave() }
}

class UserController(private val client: TelegramClient){
    val channelPeer by lazy { (client.loadChannel(CHAT_ID) as TLChannel).toInputPeer()!! }
    private val botPeer by lazy { client.getUser(BOT_ID)!!.toInputPeer() }

    var joined = false

    fun join(code: String) : Boolean {
        if(!joined) {
            joined = true
            val text = "/start $code"
            client.messagesSendMessage(botPeer, text, Random.nextLong())
            return true
        }
        return false
    }

    fun leave() = client.messagesSendMessage(botPeer, "/leave", Random.nextLong())
}
