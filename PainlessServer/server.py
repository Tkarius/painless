import ssl
import paho.mqtt.client as mqtt

def on_connect(mqttClient, userdata, flags, rc):
  print("Server connected with result code: " + rc)
  mqttClient.subscribe("sys/#", 2)
  mqttClient.subscribe("testi/#", 2)

def on_message(mqttClient, userdata, msg):
  print("[" + msg.topic + "] " + str(msg.payload))

def on_subscribe(mqttClient, userdata, mid, granted_qos):
  print("Subscribe successful with following QoS: " + str(granted_qos))
  #we could parse the channels here from granted_qos and publish something
  #like 'PainLess' super-admin present or.. something.

def on_publish(mqttClient, userdata, mid):
  print("Publish successful. Somewhere, somehow :s")


server = mqtt.Client(client_id="Painless-Server-1")
server.on_connect = on_connect
server.on_message = on_message
server.on_subscribe = on_subscribe
server.on_publish = on_publish

server.tls_set(ca_certs="./TLS", certfile="", keyfile="", cert_reqs=ssl.CERT_REQUIRED , ciphers=None)
server.username_pw_set("PainLess","PainLessServerPassu-001")

server.connect("localhost", 1883, 60)
server.loop_forever()

