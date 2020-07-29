package com.y9san9.mafiaboost.booster

import com.y9san9.mafiaboost.mafia.MafiaController
import com.y9san9.mafiaboost.model.Model
import com.y9san9.mafiaboost.utils.ApiStorage
import com.y9san9.mafiaboost.utils.refresh
import java.io.File
import java.util.*


val scriptDir = (System.getProperty("user.dir") ?: "") + "/properties"

object MafiaBooster {
    private val scanner = Scanner(System.`in`)
    /**
     * Run to start bot
     */
    fun start(){
        var apiId = 0
        var apiHash = ""

        val dataFile = ApiStorage(File(scriptDir, "apiData"))
        dataFile.get()?.let { (id, hash) ->
            apiId = id
            apiHash = hash
        } ?: let {
            println("Bot requires telegram api auth (https://my.telegram.org)")
            print("Enter api id: ")
            apiId = scanner.nextInt()
            print("Enter api hash: ")
            apiHash = scanner.next()
            dataFile.put(apiId, apiHash)
        }

        val chatFile = File(scriptDir, "chat.txt").apply { refresh() }
        var chatId: Int

        chatFile.readText().let {
            if(it.isEmpty()){
                print("Enter chat id you want to roll in: ")
                chatId = scanner.nextInt()
                chatFile.writeText(chatId.toString())
            } else {
                chatId = it.toInt()
            }
        }

        val model = Model(MafiaController(apiId, apiHash, chatId))
        model.auth {
            print("Enter $num phone: ")
            phone = scanner.next()
            password = {
                print("Enter password: ")
                scanner.next()
            }
            code = {
                print("Enter code: ")
                scanner.next()
            }
        }

        model.startGame()
    }
}