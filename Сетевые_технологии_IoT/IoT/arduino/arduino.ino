#include <ESP8266WiFi.h>
#include <PubSubClient.h>

const char* WIFI_SSID = "даша";
const char* WIFI_PASSWORD = "05060506";

const char* MQTT_HOST = "172.20.10.3";
const int MQTT_PORT = 1883;

const char* TOPIC_COMMANDS = "iot/speaker/commands";
const char* TOPIC_STATE = "iot/speaker/state";
const char* TOPIC_NOISE = "iot/speaker/telemetry/noise";
const char* TOPIC_MOTION = "iot/speaker/telemetry/motion";

const int LED_PIN = LED_BUILTIN;
const int BUZZER_PIN = D6;
const int SOUND_PIN = A0;

// HC-SR501 PIR motion sensor
// OUT -> D1 / GPIO5
// VCC -> 3V
// GND -> G
const int MOTION_PIN = D1;

// true = читать реальный датчик шума с A0
// false = использовать тестовые данные
const bool USE_REAL_NOISE_SENSOR = true;

// порог автотревоги по шуму в night mode
const int NOISE_ALARM_THRESHOLD = 50;

// интервал мигания/пиканья тревоги
const unsigned long BUZZER_TOGGLE_MS = 300;

// как часто проверять датчик движения
const unsigned long MOTION_CHECK_MS = 200;

struct SpeakerState {
  String mode = "normal";

  // noise  = тревога только от шума
  // motion = тревога только от движения
  // both   = тревога от шума или движения
  String triggerMode = "both";

  bool mute = false;
  bool alarm = false;
};

SpeakerState state;
WiFiClient espClient;
PubSubClient mqtt(espClient);

unsigned long lastStatePublishMs = 0;
unsigned long lastNoisePublishMs = 0;
unsigned long lastMotionCheckMs = 0;
unsigned long lastBuzzerToggleMs = 0;
unsigned long lastSensorLockLogMs = 0;

bool buzzerOn = false;
bool lastMotion = false;
int lastNoise = 42;

// none, noise, motion, manual
String lastAlarmSource = "none";

// После /alarm_off блокируем автотревогу,
// пока датчики не вернутся в спокойное состояние.
bool waitForSensorsClear = false;

void applyOutputs() {
  // LED на ESP8266 инверсный: LOW = включено, HIGH = выключено
  if (state.alarm) {
    digitalWrite(LED_PIN, LOW);
  } else {
    digitalWrite(LED_PIN, HIGH);
  }

  // Звук тут не держим постоянно — им управляет updateBuzzer()
  if (!state.alarm || state.mute) {
    buzzerOn = false;
    digitalWrite(BUZZER_PIN, LOW);
  }
}

void updateBuzzer() {
  if (!state.alarm || state.mute) {
    buzzerOn = false;
    digitalWrite(BUZZER_PIN, LOW);
    return;
  }

  unsigned long now = millis();
  if (now - lastBuzzerToggleMs >= BUZZER_TOGGLE_MS) {
    lastBuzzerToggleMs = now;
    buzzerOn = !buzzerOn;
    digitalWrite(BUZZER_PIN, buzzerOn ? HIGH : LOW);
  }
}

void blinkShort() {
  digitalWrite(LED_PIN, LOW);
  delay(80);
  digitalWrite(LED_PIN, HIGH);
}

void publishState() {
  String payload = "{";
  payload += "\"mode\":\"" + state.mode + "\",";
  payload += "\"triggerMode\":\"" + state.triggerMode + "\",";
  payload += "\"alarmSource\":\"" + lastAlarmSource + "\",";
  payload += "\"mute\":" + String(state.mute ? "true" : "false") + ",";
  payload += "\"alarm\":" + String(state.alarm ? "true" : "false") + ",";
  payload += "\"noise\":" + String(lastNoise) + ",";
  payload += "\"motion\":" + String(lastMotion ? "true" : "false") + ",";
  payload += "\"waitForSensorsClear\":" + String(waitForSensorsClear ? "true" : "false") + ",";
  payload += "\"ts\":" + String(millis() / 1000);
  payload += "}";

  Serial.print("Publish state: ");
  Serial.println(payload);

  mqtt.publish(TOPIC_STATE, payload.c_str());
}

int readNoiseLevel() {
  if (!USE_REAL_NOISE_SENSOR) {
    return random(20, 95);
  }

  int minVal = 1023;
  int maxVal = 0;

  unsigned long start = millis();
  while (millis() - start < 100) {
    int raw = analogRead(SOUND_PIN);

    if (raw < minVal) minVal = raw;
    if (raw > maxVal) maxVal = raw;
  }

  int amplitude = maxVal - minVal;
  int mapped = map(amplitude, 0, 20, 0, 100);

  if (mapped < 0) mapped = 0;
  if (mapped > 100) mapped = 100;

  Serial.print("MIN: ");
  Serial.print(minVal);
  Serial.print(" MAX: ");
  Serial.print(maxVal);
  Serial.print(" AMP: ");
  Serial.print(amplitude);
  Serial.print(" MAPPED: ");
  Serial.println(mapped);

  return mapped;
}

void triggerAlarmIfNeeded(String source, String reason) {
  if (state.mode != "night") {
    return;
  }

  if (state.alarm) {
    return;
  }

  if (waitForSensorsClear) {
    Serial.println("Alarm blocked: waiting for sensors to clear");
    return;
  }

  lastAlarmSource = source;

  Serial.print(reason);
  Serial.println(" -> alarm ON");

  state.alarm = true;
  applyOutputs();
  publishState();
}

void publishNoise() {
  lastNoise = readNoiseLevel();

  String payload = String(lastNoise);

  Serial.print("Publish noise: ");
  Serial.println(payload);

  mqtt.publish(TOPIC_NOISE, payload.c_str());

  bool noiseTriggerEnabled =
    state.triggerMode == "noise" || state.triggerMode == "both";

  if (noiseTriggerEnabled && lastNoise >= NOISE_ALARM_THRESHOLD) {
    triggerAlarmIfNeeded("noise", "Noise threshold exceeded in night mode");
  }
}

void checkMotion() {
  bool motionNow = digitalRead(MOTION_PIN) == HIGH;

  // motionStarted = true только при переходе false -> true.
  // Это защищает от повторного срабатывания, пока PIR держит HIGH.
  bool motionStarted = motionNow && !lastMotion;

  if (motionNow != lastMotion) {
    lastMotion = motionNow;

    Serial.print("Motion changed: ");
    Serial.println(lastMotion ? "true" : "false");

    mqtt.publish(TOPIC_MOTION, lastMotion ? "1" : "0");
    publishState();
  }

  bool motionTriggerEnabled =
    state.triggerMode == "motion" || state.triggerMode == "both";

  if (motionTriggerEnabled && motionStarted) {
    triggerAlarmIfNeeded("motion", "Motion detected in night mode");
  }
}

void updateSensorClearLock() {
  if (!waitForSensorsClear) {
    return;
  }

  // Важно: читаем реальный пин PIR прямо сейчас,
  // а не только lastMotion, чтобы не разблокироваться слишком рано.
  bool motionPinNow = digitalRead(MOTION_PIN) == HIGH;
  bool sensorsClear = !motionPinNow && lastNoise < NOISE_ALARM_THRESHOLD;

  if (sensorsClear) {
    waitForSensorsClear = false;
    lastMotion = false;

    Serial.println("Sensors are clear, auto alarm enabled again");
    publishState();
    return;
  }

  unsigned long now = millis();
  if (now - lastSensorLockLogMs >= 1000) {
    lastSensorLockLogMs = now;

    Serial.print("Waiting for sensors to clear. motionPin=");
    Serial.print(motionPinNow ? "true" : "false");
    Serial.print(" noise=");
    Serial.println(lastNoise);
  }
}

void handleCommand(String json) {
  Serial.print("Command payload: ");
  Serial.println(json);

  if (json.indexOf("\"cmd\":\"mode\"") >= 0) {
    if (json.indexOf("\"value\":\"night\"") >= 0) {
      state.mode = "night";
    } else if (json.indexOf("\"value\":\"normal\"") >= 0) {
      state.mode = "normal";
    }

    applyOutputs();
    publishState();
    return;
  }

  if (json.indexOf("\"cmd\":\"trigger_mode\"") >= 0) {
    if (json.indexOf("\"value\":\"noise\"") >= 0) {
      state.triggerMode = "noise";
    } else if (json.indexOf("\"value\":\"motion\"") >= 0) {
      state.triggerMode = "motion";
    } else if (json.indexOf("\"value\":\"both\"") >= 0) {
      state.triggerMode = "both";
    }

    applyOutputs();
    publishState();
    return;
  }

  if (json.indexOf("\"cmd\":\"mute\"") >= 0) {
    state.mute = json.indexOf("\"value\":true") >= 0;
    applyOutputs();
    publishState();
    return;
  }

  if (json.indexOf("\"cmd\":\"alarm\"") >= 0) {
    state.alarm = json.indexOf("\"value\":true") >= 0;

    if (state.alarm) {
      lastAlarmSource = "manual";
      waitForSensorsClear = false;
    } else {
      lastAlarmSource = "none";

      // Сразу читаем реальное состояние PIR в момент выключения тревоги.
      lastMotion = digitalRead(MOTION_PIN) == HIGH;

      // После /alarm_off не даём тревоге включиться сразу заново,
      // пока PIR реально не станет LOW и шум не станет ниже порога.
      waitForSensorsClear = true;

      Serial.print("Alarm OFF. Waiting for sensors clear. motion=");
      Serial.print(lastMotion ? "true" : "false");
      Serial.print(" noise=");
      Serial.println(lastNoise);
    }

    applyOutputs();
    publishState();
    return;
  }

  if (json.indexOf("\"cmd\":\"status_request\"") >= 0) {
    publishState();
    return;
  }
}

void mqttCallback(char* topic, byte* payload, unsigned int length) {
  String msg;

  for (unsigned int i = 0; i < length; i++) {
    msg += (char)payload[i];
  }

  Serial.print("MQTT message on topic ");
  Serial.print(topic);
  Serial.print(": ");
  Serial.println(msg);

  if (String(topic) == TOPIC_COMMANDS) {
    handleCommand(msg);
  }
}

void connectWiFi() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.print("Connecting to Wi-Fi");

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    blinkShort();
    delay(300);
  }

  Serial.println();
  Serial.println("Wi-Fi connected");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void connectMqtt() {
  mqtt.setServer(MQTT_HOST, MQTT_PORT);
  mqtt.setCallback(mqttCallback);

  while (!mqtt.connected()) {
    Serial.print("Connecting to MQTT... ");

    String clientId = "echonode-device-";
    clientId += String(ESP.getChipId(), HEX);

    if (mqtt.connect(clientId.c_str())) {
      Serial.println("connected");
      mqtt.subscribe(TOPIC_COMMANDS);
      Serial.print("Subscribed to ");
      Serial.println(TOPIC_COMMANDS);
      publishState();
    } else {
      Serial.print("failed, rc=");
      Serial.print(mqtt.state());
      Serial.println(", retry in 2 sec");
      delay(2000);
    }
  }
}

void setup() {
  pinMode(LED_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  pinMode(MOTION_PIN, INPUT);

  digitalWrite(LED_PIN, HIGH);
  digitalWrite(BUZZER_PIN, LOW);

  Serial.begin(115200);
  delay(1000);

  Serial.println();
  Serial.println("NodeMCU boot");

  connectWiFi();
  connectMqtt();
  applyOutputs();
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("Wi-Fi lost, reconnecting...");
    connectWiFi();
  }

  if (!mqtt.connected()) {
    connectMqtt();
  }

  mqtt.loop();
  updateBuzzer();

  unsigned long now = millis();

  if (now - lastStatePublishMs >= 15000) {
    lastStatePublishMs = now;
    publishState();
  }

  if (now - lastNoisePublishMs >= 5000) {
    lastNoisePublishMs = now;
    publishNoise();
  }

  if (now - lastMotionCheckMs >= MOTION_CHECK_MS) {
    lastMotionCheckMs = now;
    checkMotion();
  }

  updateSensorClearLock();
}