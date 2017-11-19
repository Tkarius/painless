from sqlalchemy import create_engine
from PBKDF2_M_auth_python3 import hashing_passwords

engine = create_engine('postgresql+psycopg2://painlessserver:StillSomePainLeft@localhost/mosquittoauth', pool_size=20,
                       pool_recycle=600)


# Authenticates the given user by callign a procedure to match the username and password hash. Returns True/False accordingly.
def check_auth(username, password):
  connection = engine.raw_connection()
  cursor = connection.cursor()
  print(username)
  cursor.execute('SELECT usp_get_password(%s)', (username,))
  pw_hash = cursor.fetchone()[0]
  cursor.close()
  connection.commit()
  connection.close()
  if (pw_hash == ''):
    return False
  print("Hasing password check result: " + str(hashing_passwords.check_hash(password, pw_hash)))
  if (hashing_passwords.check_hash(password, pw_hash)):
    return True
  else:
    return False


# Checks if the given user is the channel owner of the given channel. Returns True/False accordingly
def is_owner(username, channel):
  connection = engine.raw_connection()
  cursor = connection.cursor()
  cursor.execute('SELECT usp_check_if_owner(%s, %s)', [username, channel])
  channel_owner = cursor.fetchone()[0]
  cursor.close()
  connection.commit()
  connection.close()
  return channel_owner


# Handles adding the user to database. Returns true if no error is encountered.
def add_user(username, password):
  try:
    password_hash = hashing_passwords.make_hash(password)
    connection = engine.raw_connection()
    cursor = connection.cursor()
    cursor.execute('SELECT usp_insert_user(%s,%s)', [username, password_hash])
    cursor.close()
    connection.commit()
    connection.close()
  except Exception:
    # we should identify all possible exceptions and then tighten the scope of this try/except
    return False

def add_channel_rights(username, channel, level, owner):
  try:
    connection = engine.raw_connection()
    cursor = connection.cursor()
    cursor.execute('SELECT usp_insert_channel_right(%s,%s, %s, %s)', [username, channel, level, owner])
    cursor.close()
    connection.commit()
    connection.close()
  except Exception:
    #we should identify all possible exceptions and then tighten the scope of this try/except
    return False

# check_auth("Testeri", "testeri")
#add_user("Törppö", "Einari")
