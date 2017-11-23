from sqlalchemy import create_engine


engine = create_engine('postgresql+psycopg2://painlessserver:StillSomePainLeft@localhost/mosquittoauth', pool_size=20,
                       pool_recycle=600)


# Authenticates the given user by calling a procedure to match the username and password hash. Returns True/False accordingly.
def get_password(username):
  connection = engine.raw_connection()
  cursor = connection.cursor()
  cursor.execute('SELECT usp_get_password(%s)', (username,))
  pw_hash = cursor.fetchone()[0]
  print("Got dem hashes: " + str(pw_hash))
  cursor.close()
  connection.commit()
  connection.close()
  return pw_hash


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
def add_user(username, password_hash):
  try:
    connection = engine.raw_connection()
    print("Adding user to database with username: " + username + " and password: " + password_hash)
    cursor = connection.cursor()
    cursor.execute('SELECT usp_insert_user(%s,%s)', [username, password_hash])
    cursor.close()
    connection.commit()
    connection.close()
  except Exception:
    print("Exception occured while adding an user to database. :(")
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

