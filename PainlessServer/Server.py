# Driver for Painless -server backend.
# Listens and publishes on Painless system channels on MQTT-broker.

import ssl
import paho.mqtt.client as mqtt
import DBConnector

#DBConnector tests
#Currently some issues with the library when making/checking hashes

DBConnector.add_user('DBconTest', 'BestestPassword')
print("Should be False: " + str(DBConnector.check_auth('DBconTest', 'WrongPasswordOuNou')))
print("Should be True: " + str(DBConnector.check_auth('DBconTest', 'BestestPassword')))

def on_connect(mqttClient, userdata, flags, rc):
  print("Server connected with result code: " + str(rc))
  mqttClient.subscribe("painless/sys/#", 2)
  mqttClient.subscribe("testi/#", 2)

def on_message(mqttClient, userdata, msg):
  print("[" + msg.topic + "] " + str(msg.payload))

def on_subscribe(mqttClient, userdata, mid, granted_qos):
  print("Subscribe successful with following QoS: " + str(granted_qos))

def on_publish(mqttClient, userdata, mid):
  print("Publish successful. Somewhere, somehow :s")


server = mqtt.Client(client_id="Painless-Server-1")
server.on_connect = on_connect
server.on_message = on_message
server.on_subscribe = on_subscribe
server.on_publish = on_publish

server.tls_set(ca_certs="./tls/ca.crt", certfile="./tls/painlessServer.client.crt",
               keyfile="./tls/painlessServer.client.key", cert_reqs=ssl.CERT_REQUIRED, ciphers=None)
#convenience setting for test environment. Do not use in real environment.
server.tls_insecure_set(True)
server.username_pw_set("PainLess", "PainLessServerPassu-001")

server.connect("localhost", 8883, 60)
server.loop_forever()
