from werkzeug.security import pbkdf2_bin
from base64 import b64encode, b64decode
import string
import random

ITERATIONS = 4000
KEYLEN = 24
HASHFUNCTION = 'sha256'
SALT_LENGTH = 8

def salt_hash():
  salt = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(SALT_LENGTH))
  return salt

#takes plain password and makes a hash out of it and returns the hash
def gen_hash(password, _salt, iterations = ITERATIONS, keylen = KEYLEN, hashfunction = HASHFUNCTION, auth= False):
  print("hashfunction: " + hashfunction)
  print("iterations: " + str(iterations))
  print("salt: " + _salt)
  hash = str(b64encode(pbkdf2_bin(password, _salt, iterations, keylen, hashfunction)), 'utf-8')
  print("hash " + str(hash))
  if (auth == False):
    return hash
  else:
    return make_auth_hash(hash, _salt)

#Takes auth formatted hash and plain text password
#converts the hash to werkzeug format and uses werkzeug password checking
def check_hash(auth_hash, _password):
  hashparts = auth_hash.split('$')
  _hashfunction = hashparts[1]
  _iterations = hashparts[2]
  _salt = hashparts[3]
  _dbhash = hashparts[4]
  print("password: " + _password)
  print("hashfunction: " + _hashfunction)
  print("iterations: " + _iterations)
  print("salt: " + _salt)
  print("dbhash: " + _dbhash)
  new_hash = gen_hash(_password, _salt, iterations = int(_iterations), hashfunction = _hashfunction)
  auth = (new_hash == _dbhash)
  print("Auth " + str(auth))
  return auth

#Takes werkzeug formatted hash and returns auth formatted hash
def make_auth_hash(wz_hash ,salt):
  #auth_end = wz_hash[14:]
  auth_hash = "PBKDF2$sha256$" + str(ITERATIONS) + "$" + salt + "$" + wz_hash
  print("auth_hash " + auth_hash)
  return auth_hash

#takes auth formatted hash and returns werkzeug formatted hash
def make_wz_hash(auth_hash):
  wz_end = auth_hash[14:]
  wz_hash = "pbkdf2:sha256:" + wz_end
  print("wz_hash " + wz_hash)
  return wz_hash

#print("Testing: password 'Testeri2'")
#hashi1 = gen_hash('porkkana', 'kokeiluSuolaLOL')
#check_hash(hashi1, "porkkana")