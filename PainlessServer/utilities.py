from werkzeug.security import pbkdf2_bin
from base64 import b64encode
import string
import random

ITERATIONS = 4000
KEYLEN = 24
HASHFUNCTION = 'sha256'
SALT_LENGTH = 8

#generates random salt which length is determined by SALT_LENGTH variable
def salt_hash():
  salt = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(SALT_LENGTH))
  return salt

#takes plain password and makes a hash out of it and returns the hash
def gen_hash(password, _salt, iterations = ITERATIONS, keylen = KEYLEN, hashfunction = HASHFUNCTION, auth= False):
  hash = str(b64encode(pbkdf2_bin(password, _salt, iterations, keylen, hashfunction)), 'utf-8')
  print("hash " + str(hash))
  if (auth == False):
    return hash
  else:
    return make_auth_hash(hash, _salt)

#Takes auth formatted hash and plain text password
#splits the hash to parts and re-hashes the password using this information.
# then compares the two hashes and returns auth
def check_hash(auth_hash, _password):
  hashparts = auth_hash.split('$')
  _hashfunction = hashparts[1]
  _iterations = hashparts[2]
  _salt = hashparts[3]
  _dbhash = hashparts[4]
  new_hash = gen_hash(_password, _salt, iterations = int(_iterations), hashfunction = _hashfunction)
  auth = (new_hash == _dbhash)
  print("Auth " + str(auth))
  return auth

#Takes werkzeug formatted hash and returns auth formatted hash
def make_auth_hash(wz_hash ,salt):
  auth_hash = "PBKDF2$sha256$" + str(ITERATIONS) + "$" + salt + "$" + wz_hash
  print("auth_hash " + auth_hash)
  return auth_hash
