from werkzeug.security import generate_password_hash
from werkzeug.security import check_password_hash


#takes plain password and makes a hash out of it and returns the hash
def gen_hash(password):
  hash = generate_password_hash(password, 'pbkdf2:sha256:4096', 12)
  print("hash " + hash)
  return hash

#Takes auth formatted hash and plain text password
#converts the hash to werkzeug format and uses werkzeug password checking
def check_hash(auth_hash, password):
  auth = check_password_hash(make_wz_hash(auth_hash), password)
  print("Auth " + str(auth))
  return auth

#Takes werkzeug formatted hash and returns auth formatted hash
def make_auth_hash(wz_hash):
  auth_end = wz_hash[14:]
  auth_hash = "PBKDF2$sha256$" + auth_end
  print("auth_hash " + auth_hash)
  return auth_hash

#takes auth formatted hash and returns werkzeug formatted hash
def make_wz_hash(auth_hash):
  wz_end = auth_hash[14:]
  wz_hash = "pbkdf2:sha256:" + wz_end
  print("wz_hash " + wz_hash)
  return wz_hash

#print("Testing: password 'Testeri2'")
#print(str(check_password_hash('pbkdf2:sha256:4096$oH91zYO0nAcf$a0f0fca441b2c4e7e3e5ea62cb1b0e020876cb33be795e26bcd97903876a8fd9', 'Testeri2')))
#hashi1 = gen_hash('porkkana')
#auth_h1 = make_auth_hash(hashi1)
#hashi2 = make_wz_hash(auth_h1)
#check_hash(hashi2, "porkkana")