import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

data class SimState(
    var mode: String = "normal",
    var mute: Boolean = false,
    var alarm: Boolean = false
)

fun main() {
    val client = MqttClient(
        System.getenv("MQTT_BROKER_URL") ?: "tcp://localhost:1883",
        "echonode-sim",
        MemoryPersistence()
    )
    client.connect()

    val gson = Gson()
    val state = SimState()
    var lastNoise: Int = 0

    fun publishState() {
        val payload = gson.toJson(
            mapOf(
                "mode" to state.mode,
                "mute" to state.mute,
                "alarm" to state.alarm,
                "noise" to lastNoise,
                "ts" to (System.currentTimeMillis() / 1000)
            )
        )
        client.publish("iot/speaker/state", MqttMessage(payload.toByteArray()))
    }

    client.subscribe("iot/speaker/commands") { _, msg ->
        val map = gson.fromJson(msg.toString(), Map::class.java)
        when (map["cmd"]) {
            "mode" -> state.mode = map["value"] as String
            "mute" -> state.mute = map["value"] as Boolean
            "alarm" -> state.alarm = map["value"] as Boolean
            "status_request" -> {}
        }
        publishState()
    }

    fixedRateTimer(period = 2000) {
        lastNoise = Random.nextInt(20, 95)
        client.publish("iot/speaker/telemetry/noise", MqttMessage("$lastNoise".toByteArray()))

        if (state.mode == "night" && lastNoise > 80 && !state.alarm) {
            state.alarm = true
            publishState()
        }
    }

    while (true) Thread.sleep(10_000)
}
