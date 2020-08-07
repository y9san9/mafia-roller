package com.y9san9.mafiaboost.mafia

import APP_VERSION
import BOT_ID
import LANG_CODE
import MODEL
import SYSTEM_VERSION
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.TelegramApp


const val START_GAME_COMMAND = "/game"
const val FORCE_START_GAME_COMMAND = "/start"


class MafiaController(apiId: Int, apiHash: String, val chatId: Int) {
    private var playersJoined = 0

    var gameInvitationReceived: (Pair<Int, String>) -> Unit = {}
    var allJoined: () -> Unit = {}

    /**
     * when code == 1 then you need wait some seconds
     */
    var gameFinished: (code: Int) -> Unit = {}

    private val app = TelegramApp(apiId, apiHash, MODEL, SYSTEM_VERSION, APP_VERSION, LANG_CODE)

    private val client1 = KotlogramClient(app, "0")
    private val client2 = KotlogramClient(app, "1")
    private val client3 = KotlogramClient(app, "2")
    private val client4 = KotlogramClient(app, "3")

    private val clients = arrayOf(client1, client2, client3, client4)

    private val controller1 = UserController(client1, chatId)
    private val controller2 = UserController(client2, chatId)
    private val controller3 = UserController(client3, chatId)
    private val controller4 = UserController(client4, chatId)

    val controllers = arrayOf(controller1, controller2, controller3, controller4)

    init {
        clients.forEachIndexed { index, client ->
            client.handler(this, index)
        }
    }

    fun auth(handler: AuthDSL.() -> Unit) = clients.forEachIndexed { pos, client ->
        if(!client.isAuthorized) {
            val auth = AuthDSL(pos + 1).apply(handler)
            client.auth(auth.phone, auth.password, auth.code)
        } else println("Account ${pos + 1} authorized")
    }

    inner class AuthDSL(val num: Int) {
        var phone: String = ""
        var password: () -> String = { "" }
        var code: () -> String = { "" }
    }

    fun startGame() {
        controllers.forEach { it.joined = false }
        playersJoined = 0
        client1.sendMessage(controller1.channelPeer, START_GAME_COMMAND)
    }
    fun forceStart() = client1.sendMessage(controller1.channelPeer, FORCE_START_GAME_COMMAND)

    fun joinGame(user: Int, code: String){
        if(controllers[user].join(code)) {
            playersJoined++
            if (playersJoined == 4) {
                allJoined()
            }
        }
    }
}

class UserController(private val client: KotlogramClient, chatId: Int){
    val channelPeer by lazy { client.getChannel(chatId)!!.input }
    private val botPeer by lazy { client.getUser(BOT_ID)!!.input }

    var joined = false

    fun join(code: String) : Boolean {
        if(!joined) {
            joined = true
            val text = "/start $code"
            client.sendMessage(botPeer, text)
            return true
        }
        return false
    }

    fun leave() = client.sendMessage(botPeer, "/leave")
}
