package com.y9san9.mafiaboost.utils.apihelper

import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.api.UpdateCallback
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.api.updates.TLDifference
import com.github.badoualy.telegram.tl.core.TLVector


class UpdatesHandler(handler: UpdatesDSL.() -> Unit) : UpdateCallback {
    private val dsl = UpdatesDSL().apply(handler)

    override fun onShortSentMessage(client: TelegramClient, message: TLUpdateShortSentMessage)
            = handleMessagesDifference(client, message.pts, message.ptsCount, message.date)
    override fun onShortChatMessage(client: TelegramClient, message: TLUpdateShortChatMessage)
            = handleMessagesDifference(client, message.pts, message.ptsCount, message.date)
    override fun onShortMessage(client: TelegramClient, message: TLUpdateShortMessage)
            = handleMessagesDifference(client, message.pts, message.ptsCount, message.date)
    override fun onUpdateShort(client: TelegramClient, update: TLUpdateShort) {}

    private fun handleMessagesDifference(client: TelegramClient, pts: Int, ptsCount: Int, date: Int){
        val difference = client.updatesGetDifference(pts - ptsCount, null, date, 0)
        if(difference is TLDifference){
            handleUpdates(
                client,
                difference.users,
                difference.chats,
                *(difference.newMessages.map { TLUpdateNewMessage(it, pts, ptsCount) }
                        + difference.otherUpdates).toTypedArray()
            )
        }
    }

    override fun onUpdates(client: TelegramClient, updates: TLUpdates)
            = handleUpdates(client, updates.users, updates.chats, *updates.updates.toTypedArray())
    override fun onUpdatesCombined(client: TelegramClient, updates: TLUpdatesCombined)
            = handleUpdates(client, updates.users, updates.chats, *updates.updates.toTypedArray())

    private fun handleUpdates(
            client: TelegramClient,
            users: TLVector<TLAbsUser>,
            chats: TLVector<TLAbsChat>,
            vararg updates: TLAbsUpdate
    ){
        cachedUsers.putAll(users.map { client to it.id to it.asUser })
        cachedChannels.putAll(chats.mapNotNull { client to it.id to (it as? TLChannel ?: return@mapNotNull null) })
        dsl(client, *updates)
    }

    override fun onUpdateTooLong(client: TelegramClient) {}


    inner class UpdatesDSL {
        operator fun invoke(client: TelegramClient, vararg updates: TLAbsUpdate){
            val updatesMutable = updates.toMutableList()

            updatesMutable.removeIf {
                messagesHandlers.forEach { handler ->
                    handler.handler(client, (when (it) {
                        is TLUpdateNewMessage -> it.message
                        is TLUpdateNewChannelMessage -> it.message
                        else -> null
                    } as? TLMessage ?: return@removeIf false).also { message ->
                        handler.toChat?.apply { message.isChannel == this || return@forEach }
                        handler.toChatId?.apply { (message.isChannel && (message.toIntId == this)) || return@forEach }
                        handler.fromUser?.apply { message.fromId == this || return@forEach }
                        handler.toUserId?.apply { (message.is1v1 && (message.toIntId == this)) || return@forEach }
                    })
                }
                return@removeIf false
            }

            updatesMutable.forEach {
                client.otherHandler(it)
            }
        }

        private var messagesHandlers = mutableListOf<MessagesHandler>()

        fun message(
                out: Boolean? = null,
                toChat: Boolean? = null,
                toChatId: Int? = null,
                toUserId: Int? = null,
                fromUser: Int? = null,
                handler: TelegramClient.(TLMessage) -> Unit
        ) = messagesHandlers.add(MessagesHandler(out, toChat, toChatId, toUserId, fromUser, handler))

        private var otherHandler: TelegramClient.(TLAbsUpdate) -> Unit = {}
        fun other(handler: TelegramClient.(TLAbsUpdate) -> Unit){ otherHandler = handler }
    }

    class MessagesHandler(
        var out: Boolean? = null,
        var toChat: Boolean? = null,
        var toChatId: Int? = null,
        var toUserId: Int? = null,
        var fromUser: Int? = null,
        var handler: TelegramClient.(TLMessage) -> Unit = {}
    )

}

fun updatesHandler(handler: UpdatesHandler.UpdatesDSL.() -> Unit) = UpdatesHandler(handler)


private val cachedUsers = mutableMapOf<Pair<TelegramClient, Int>, TLUser>()
private val cachedChannels = mutableMapOf<Pair<TelegramClient, Int>, TLChannel>()

fun TelegramClient.getUser(id: Int) = cachedUsers[this to id]
fun TelegramClient.getChannel(id: Int) = cachedChannels[this to id]
