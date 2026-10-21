package com.jagex.game.runetek5.plugin

import org.w3c.notifications.GRANTED
import org.w3c.notifications.DEFAULT
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions
import org.w3c.notifications.NotificationPermission

/*
 * Not part of the original client: the browser side of showDesktopNotification, through the
 * Notification API. The first call, with the permission not yet given, asks for it and shows
 * nothing; once the player has allowed it, every call shows.
 */
@Suppress("TooGenericExceptionCaught", "SwallowedException") // a browser without the API, or in an insecure context, means "no notification"
actual fun showDesktopNotification(title: String, text: String): Boolean {
	try {
		if (!js("typeof Notification !== 'undefined'").unsafeCast<Boolean>()) {
			return false
		}
		val permission: NotificationPermission = Notification.permission
		if (permission == NotificationPermission.GRANTED) {
			Notification(title, NotificationOptions(body = text))
			return true
		}
		if (permission == NotificationPermission.DEFAULT) {
			Notification.requestPermission { }
		}
		return false
	} catch (error: Throwable) {
		return false
	}
}
