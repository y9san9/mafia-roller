package com.y9san9.mafiaboost.model

import com.y9san9.mafiaboost.mafia.MafiaController
import java.lang.Thread.sleep


class Model(private val controller: MafiaController){

    fun auth(handler: MafiaController.AuthDSL.() -> Unit) = controller.auth(handler)

    fun startGame(){
        controller.gameInvitationReceived = { (user, code) ->
            controller.joinGame(user, code)
        }
        controller.allJoined = {
            controller.forceStart()
        }
        controller.gameFinished = {
            controller.startGame()
        }
        controller.startGame()
    }

}
