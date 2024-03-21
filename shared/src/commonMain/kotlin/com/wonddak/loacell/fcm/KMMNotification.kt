package com.wonddak.loacell.fcm


object KMMNotification {
    val manager: NotificationManager by lazy { NotificationManager() }

    private val nowToken: String?
        get() = manager.token

    //region token
    private var tokenReceived: (token: String) -> Unit = {}

    fun onNewToken(block: (token: String) -> Unit): KMMNotification {
        tokenReceived = block
        return this
    }

    internal fun notifyNewToken(token: String?) {
        token ?: return
        if (token.isEmpty() || token == nowToken) {
            return
        }
        tokenReceived(token)
    }

    //endregion

    //region message
    private var messageReceived: (notificationElement: NotificationElement) -> Unit = {}
    fun onNewMessage(block: (notificationElement: NotificationElement) -> Unit): KMMNotification {
        messageReceived = block
        return this
    }

    internal fun processReceived(notificationElement: NotificationElement) {
        messageReceived(notificationElement)
    }

    //endregion
    private var isInitialized = false

    fun init() {
        isInitialized = true
    }

}