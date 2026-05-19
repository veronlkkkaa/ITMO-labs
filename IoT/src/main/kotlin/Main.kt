import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.BotCommand
import com.github.kotlintelegrambot.entities.ChatId
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.concurrent.CopyOnWriteArraySet

object SharedState {
    @Volatile
    var lastStateText: String = "Состояние пока не получено"

    @Volatile
    var lastStateTs: Long = 0

    @Volatile
    var previousAlarm: Boolean = false

    @Volatile
    var suppressNextAlarmNotification: Boolean = false
}

fun main() {
    val token = System.getenv("TELEGRAM_BOT_TOKEN")
        ?.trim()
        ?.removeSurrounding("\"")
        ?: error("TELEGRAM_BOT_TOKEN не задан")

    val mqttUrl = System.getenv("MQTT_BROKER_URL")
        ?.trim()
        ?.removeSurrounding("\"")
        ?: "tcp://localhost:1883"

    val gson = Gson()
    val knownChats = CopyOnWriteArraySet<Long>()

    val mqtt = MqttClient(
        mqttUrl,
        "echonode-telegram-bot",
        MemoryPersistence()
    )

    mqtt.connect()
    println("MQTT connected: $mqttUrl")

    fun publishCommand(cmd: String, value: Any? = null) {
        val payload = if (value == null) {
            gson.toJson(mapOf("cmd" to cmd))
        } else {
            gson.toJson(mapOf("cmd" to cmd, "value" to value))
        }

        println("Publish MQTT command: $payload")
        mqtt.publish("iot/speaker/commands", MqttMessage(payload.toByteArray()))
    }

    val telegramBot = bot {
        this.token = token

        dispatch {
            command("start") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    """
                    Бот запущен

                    Этот бот управляет умной акустической системой через MQTT.
                    Устройство измеряет уровень шума, отслеживает движение и может включать тревогу в ночном режиме.

                    Команды можно выбрать через стандартное меню Telegram:
                    нажми кнопку / или Menu рядом с полем ввода.

                    Основные команды:

                    /status
                    Показать текущее состояние устройства.

                    /night
                    Включить ночной режим.
                    В этом режиме тревога может включаться автоматически.

                    /normal
                    Включить обычный режим.
                    Шум и движение продолжат измеряться, но тревога сама включаться не будет.

                    /trigger_noise
                    В ночном режиме тревога будет срабатывать только на звук.

                    /trigger_motion
                    В ночном режиме тревога будет срабатывать только на движение.

                    /trigger_both
                    В ночном режиме тревога будет срабатывать на звук или движение.

                    /alarm_on
                    Включить тревогу вручную.

                    /alarm_off
                    Выключить тревогу.

                    /mute
                    Отключить звук зуммера.
                    Сама тревога при этом не выключается.

                    /unmute
                    Включить звук зуммера обратно.

                    /ping
                    Проверить, что бот работает.
                    """.trimIndent()
                )
            }

            command("ping") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "pong ✅ Бот работает."
                )
            }

            command("mute") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                publishCommand("mute", true)

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Mute включён. Звук зуммера отключён, но сама тревога не сброшена."
                )
            }

            command("unmute") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                publishCommand("mute", false)

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Mute выключен. Если alarm сейчас активен, зуммер снова будет пищать."
                )
            }

            command("alarm_on") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                SharedState.suppressNextAlarmNotification = true
                publishCommand("alarm", true)

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Alarm включён вручную. Индикатор должен загореться, зуммер будет пищать, если mute = false."
                )
            }

            command("alarm_off") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                publishCommand("alarm", false)

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Alarm выключен. Тревога сброшена, индикатор и зуммер должны отключиться."
                )
            }

            command("night") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                publishCommand("mode", "night")

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Режим night включён. Теперь тревога может сработать автоматически по выбранному режиму: звук, движение или всё вместе."
                )
            }

            command("normal") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                publishCommand("mode", "normal")

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Режим normal включён. Шум и движение продолжат измеряться, но тревога сама включаться не будет."
                )
            }

            command("trigger_noise") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                publishCommand("trigger_mode", "noise")

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Режим срабатывания: только звук. В night mode тревога будет включаться только при превышении порога шума."
                )
            }

            command("trigger_motion") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                publishCommand("trigger_mode", "motion")

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Режим срабатывания: только движение. В night mode тревога будет включаться только при обнаружении движения."
                )
            }

            command("trigger_both") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                publishCommand("trigger_mode", "both")

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    "Режим срабатывания: звук или движение. В night mode тревога будет включаться при шуме или при движении."
                )
            }

            command("status") {
                val chatId = message.chat.id
                knownChats.add(chatId)

                val before = SharedState.lastStateTs
                publishCommand("status_request")

                Thread.sleep(500)

                val text = if (SharedState.lastStateTs != 0L && SharedState.lastStateTs >= before) {
                    SharedState.lastStateText
                } else {
                    "Статус пока не получен от устройства. Проверь, что NodeMCU подключена к Wi-Fi и MQTT."
                }

                bot.sendMessage(
                    ChatId.fromId(chatId),
                    text
                )
            }
        }
    }

    telegramBot.setMyCommands(
        listOf(
            BotCommand("start", "Запустить бота и показать описание команд"),
            BotCommand("status", "Показать состояние устройства"),
            BotCommand("night", "Включить ночной режим"),
            BotCommand("normal", "Включить обычный режим"),
            BotCommand("trigger_noise", "Срабатывать только на звук"),
            BotCommand("trigger_motion", "Срабатывать только на движение"),
            BotCommand("trigger_both", "Срабатывать на звук или движение"),
            BotCommand("alarm_on", "Включить тревогу вручную"),
            BotCommand("alarm_off", "Выключить тревогу"),
            BotCommand("mute", "Отключить звук зуммера"),
            BotCommand("unmute", "Включить звук зуммера"),
            BotCommand("ping", "Проверить работу бота")
        )
    )

    println("Telegram command menu configured")

    mqtt.subscribe("iot/speaker/state") { _, msg ->
        val json = gson.fromJson(String(msg.payload), JsonObject::class.java)

        val mode = json.get("mode")?.asString ?: "unknown"
        val triggerMode = json.get("triggerMode")?.asString ?: "both"
        val alarmSource = json.get("alarmSource")?.asString ?: "unknown"
        val mute = json.get("mute")?.asBoolean ?: false
        val alarm = json.get("alarm")?.asBoolean ?: false
        val noise = if (json.has("noise")) json.get("noise").asInt else -1
        val motion = if (json.has("motion")) json.get("motion").asBoolean else false
        val waitForSensorsClear =
            if (json.has("waitForSensorsClear")) json.get("waitForSensorsClear").asBoolean else false
        val ts = if (json.has("ts")) json.get("ts").asLong else 0L

        SharedState.lastStateText = buildString {
            appendLine("Текущее состояние устройства:")
            appendLine()

            appendLine("mode: $mode")
            appendLine(
                when (mode) {
                    "normal" -> "Обычный режим: шум и движение измеряются, но тревога сама не включается."
                    "night" -> "Ночной режим: тревога может включаться автоматически."
                    else -> "Неизвестный режим работы."
                }
            )
            appendLine()

            appendLine("triggerMode: $triggerMode")
            appendLine(
                when (triggerMode) {
                    "noise" -> "Тревога в night mode срабатывает только на звук."
                    "motion" -> "Тревога в night mode срабатывает только на движение."
                    "both" -> "Тревога в night mode срабатывает на звук или движение."
                    else -> "Неизвестный режим срабатывания."
                }
            )
            appendLine()

            appendLine("alarmSource: $alarmSource")
            appendLine(
                when (alarmSource) {
                    "noise" -> "Последняя тревога была вызвана шумом."
                    "motion" -> "Последняя тревога была вызвана движением."
                    "manual" -> "Тревога была включена вручную."
                    "none" -> "Источник тревоги отсутствует."
                    else -> "Источник тревоги неизвестен."
                }
            )
            appendLine()

            appendLine("mute: $mute")
            appendLine(
                if (mute) {
                    "Звук зуммера отключён. Тревога может быть активна, но пищать устройство не будет."
                } else {
                    "Звук зуммера разрешён. Если alarm = true, зуммер должен пищать."
                }
            )
            appendLine()

            appendLine("alarm: $alarm")
            appendLine(
                if (alarm) {
                    "Тревога активна. Чтобы сбросить её, отправь /alarm_off."
                } else {
                    "Тревога сейчас не активна."
                }
            )
            appendLine()

            appendLine("waitForSensorsClear: $waitForSensorsClear")
            appendLine(
                if (waitForSensorsClear) {
                    "После сброса тревоги устройство ждёт, пока датчики успокоятся."
                } else {
                    "Автоматическое срабатывание разрешено."
                }
            )
            appendLine()

            appendLine("noise: $noise")
            appendLine(
                when {
                    noise < 0 -> "Уровень шума пока неизвестен."
                    noise < 30 -> "Уровень шума низкий."
                    noise < 80 -> "Уровень шума средний."
                    else -> "Уровень шума высокий."
                }
            )
            appendLine()

            appendLine("motion: $motion")
            append(
                if (motion) {
                    "Обнаружено движение рядом с устройством."
                } else {
                    "Движение сейчас не обнаружено."
                }
            )
        }

        SharedState.lastStateTs = ts

        println("STATE UPDATED:")
        println(SharedState.lastStateText)

        val alarmJustTurnedOn = alarm && !SharedState.previousAlarm
        val alarmTriggeredInNightMode = mode == "night" && alarmJustTurnedOn

        if (alarmTriggeredInNightMode) {
            if (SharedState.suppressNextAlarmNotification) {
                SharedState.suppressNextAlarmNotification = false
            } else {
                val reason = when (alarmSource) {
                    "noise" -> "Зафиксирован высокий уровень шума."
                    "motion" -> "Обнаружено движение рядом с устройством."
                    "manual" -> "Тревога включена вручную."
                    else -> "Устройство зафиксировало подозрительное событие."
                }

                val notification = """
                    🚨 Тревога сработала в ночном режиме!

                    Причина: $reason

                    mode: $mode
                    triggerMode: $triggerMode
                    alarmSource: $alarmSource
                    noise: $noise
                    motion: $motion
                    mute: $mute
                    alarm: $alarm
                    waitForSensorsClear: $waitForSensorsClear

                    Чтобы выключить тревогу, отправь /alarm_off.
                    Чтобы отключить только звук зуммера, отправь /mute.
                    Чтобы посмотреть состояние, отправь /status.
                """.trimIndent()

                knownChats.forEach { chatId ->
                    telegramBot.sendMessage(
                        ChatId.fromId(chatId),
                        notification
                    )
                }
            }
        }

        SharedState.previousAlarm = alarm
    }

    println("Telegram bot started")
    telegramBot.startPolling()
}