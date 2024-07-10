from cryptography.fernet import Fernet
import argparse

def generate_key():
"""Generates a secure key."""
return Fernet.generate_key()

def encrypt_data(data, key):
"""Encrypts data using the provided key."""
fernet = Fernet(key)
encrypted = fernet.encrypt(data.encode())
return encrypted

def get_args():
parser = argparse.ArgumentParser(description="Encrypt environment variable values")
parser.add_argument('data', nargs='+', help='Key-value pairs in the format key=value')
return parser.parse_args()

def main():
args = get_args()
key = generate_key()  # Generate a new key for encryption

    print("Use the following commands to set your encrypted environment variables:")
    for pair in args.data:
        if '=' in pair:
            env_key, value = pair.split('=', 1)
            encrypted_value = encrypt_data(value, key).decode()
            print(f'export {env_key}="{encrypted_value}"')
        else:
            print(f"Error: Data should be in the format key=value for {pair}")
    
    print("Securely store this key for decryption:")
    print(key.decode())

if __name__ == "__main__":
main()
