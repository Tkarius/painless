# Driver for Painless -server backend.
# Listens and publishes on Painless system channels on MQTT-broker.

import ssl
import paho.mqtt.client as mqtt
import DBConnector
import utilities


#Testing user auth again:
DBConnector.add_user("Oranges", utilities.gen_hash("CloseUp", utilities.salt_hash(), auth=True))

def auth_user(username, password):
  stored_pw_hash = DBConnector.get_password(username)
  if (stored_pw_hash == None):
    return False
  else:
    user_auth = utilities.check_hash(stored_pw_hash, password)
    return user_auth


def on_connect(mqttClient, userdata, flags, rc):
  print("Server connected with result code: " + str(rc))
  mqttClient.subscribe("painless/sys/#", 2)
  mqttClient.subscribe("painless/sys/auth/#", 2)

def on_message(mqttClient, userdata, msg):
  #We should better define the channel structure in order to optimize channel checks
  parsed_topic= msg.topic.split('/')
  if (parsed_topic[2] == 'auth' and parsed_topic[3] == 'request'):
    #Processes auth requests which land on painless/auth/request/_ClientID
    client = parsed_topic[len(parsed_topic)-1]
    print("[" + msg.topic + "] " + str(msg.payload))
    msg_arr = str(msg.payload).split('@')
    username = msg_arr[0][2:]
    password = msg_arr[1][:-1]
    print("auth request arrived. Let's auth this thing! Username/Password: " + username + password)
    if(auth_user(username, password)):
      print("Success!")
      server.publish('painless/sys/auth/response/'+client, payload='Success', qos=2)
    else:
      print("Dat fails")
      server.publish('painless/sys/auth/response/'+client, payload='DENIED', qos=2)


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
