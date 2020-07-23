package com.y9san9.mafiaboost.booster

import com.y9san9.mafiaboost.mafia.MafiaController
import com.y9san9.mafiaboost.model.Model
import java.util.*

object MafiaBooster {
    private val model = Model(MafiaController())

    private val scanner = Scanner(System.`in`)
    /**
     * Run to change accounts or authorize (only one time)
     */
    fun authorize(){
        print("Enter first phone number: ")
        val phone1 = scanner.nextLine()
        print("Enter second phone number: ")
        val phone2 = scanner.nextLine()
        print("Enter third phone number: ")
        val phone3 = scanner.nextLine()
        print("Enter fourth phone number: ")
        val phone4 = scanner.nextLine()

        model.auth(phone1, phone2, phone3, phone4){
            print("Enter ${listOf("first", "second", "third", "fourth")[it]} code: ")
            scanner.nextLine()
        }
    }

    /**
     * Run to start bot
     */
    fun start() = model.startGame()
}