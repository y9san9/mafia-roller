package com.y9san9.mafiaboost.model

import com.y9san9.mafiaboost.mafia.MafiaController
import java.lang.Thread.sleep


sealed class Event {
    data class InvitationReceived(val user: Int, val code: String) : Event()
    data class GameFinished(val lack: Boolean) : Event()
    object AllJoined : Event()
}

class Model(private val controller: MafiaController){

    fun auth(handler: MafiaController.AuthDSL.() -> Unit) = controller.auth(handler)

    fun startGame(eventsHandler: (Event) -> Unit = {}){
        controller.gameInvitationReceived = { (user, code) ->
            eventsHandler(Event.InvitationReceived(user, code))
            controller.joinGame(user, code)
        }
        controller.allJoined = {
            eventsHandler(Event.AllJoined)
            controller.forceStart()
        }
        controller.gameFinished = { code ->
            eventsHandler(Event.GameFinished(code == 1))
            if(code == 1)
                sleep(5000)
            controller.startGame()
        }
        controller.startGame()
    }

}
