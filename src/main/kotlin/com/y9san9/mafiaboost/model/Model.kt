package com.y9san9.mafiaboost.model

import com.y9san9.mafiaboost.mafia.MafiaController
import java.lang.Thread.sleep


class Model(private val controller: MafiaController){

    fun auth(phone1: String, phone2: String, phone3: String, phone4: String, codeHandler: (Int) -> String)
            = controller.auth(phone1, phone2, phone3, phone4, codeHandler)

    fun startGame(){
        controller.gameInvitationReceived = { (user, code) ->
            controller.joinGame(user, code)
        }
        controller.allJoined = {
            controller.forceStart()
            sleep(1000)
            controller.leaveOthers()
        }
        controller.gameFinished = {
            sleep(1000)
            controller.startGame()
        }
        controller.startGame()
    }

}
