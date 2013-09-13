/**
 *  Thermostat Window Check
 *
 *  Author: brian@bevey.org
 *  Date: 9/13/13
 *
 *  If your heating or cooling system come on, it gives you notice if there are any windows or doors left open, preventing the system from working optimally.
 */

preferences {
  section("Things to check?") {
    input "sensors", "capability.contactSensor", multiple: true
  }

  section("Thermostats to monitor") {
    input "thermostats", "capability.thermostat", multiple: true
  }

  section("Notifications") {
    input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
    input "phone", "phone", title: "Send a Text Message?", required: false
  }
}

def installed() {
  subscribe(thermostats, "thermostatMode", thermoChange);
}

def updated() {
  unsubscribe()
  log.debug("Updated with settings: ${settings}")
  subscribe(thermostats, "thermostatMode", thermoChange);
}

def thermoChange(evt) {
  if (evt.value == "heat" ||
      evt.value == "cool") {
    def open = sensors.findAll { it?.latestValue("contact") == "open" }

    if (open) {
      send("Thermostat came on with the following things still open: ${open.join(', ')}")
    }

    else {
      log.info("Thermostat came on and nothing is open.");
    }
  }
}

private send(msg) {
  if (sendPushMessage != "No") {
    log.debug("Sending push message")
    sendPush(msg)
  }

  if (phone) {
    log.debug("Sending text message")
    sendSms(phone, msg)
  }

  log.debug(msg)
}