import hashlib

def create_token(login):
    hash_object = hashlib.sha256(login.encode('utf-8'))
    hex_dig = hash_object.hexdigest()
    return hex_dig
