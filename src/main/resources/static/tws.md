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


from fuzzywuzzy import fuzz

# Add this to your existing CATEGORIES dictionary
CATEGORIES = {
"Node.js": ["nodejs", "node.js", "npm", "yarn", "express", "react", "angular"],
# ... other categories ...
}

def fuzzy_match(text, keyword, threshold=80):
return fuzz.partial_ratio(text, keyword) >= threshold

def classify_email(email_content):
email_content = email_content.lower()
classifications = []

    for category, keywords in CATEGORIES.items():
        for keyword in keywords:
            if fuzzy_match(email_content, keyword, threshold=85):
                classifications.append(category)
                break  # Move to next category once a match is found

    if not classifications:
        classifications.append("Other")

    return list(set(classifications))  # Remove duplicates


Benefits in our context:

It can catch misspellings: e.g., "nodejs" would match "node.js", "nodejs", or even slight misspellings like "nodjs".
It's more forgiving of variations: e.g., "react.js" would match "react" or "reactjs".
It can handle slight formatting differences: e.g., "visual studio" would match "visualstudio" or "visual-studio".


Integration:
This fuzzy matching function would replace or supplement the exact string matching we were doing before in the classify_email function.

By using fuzzywuzzy in this way, we're not changing the overall structure of our classification system. We're simply making the keyword matching step more flexible and robust. This can help catch relevant emails that might have been missed with strict exact matching, potentially improving the accuracy of our classification.
Would you like me to explain any other aspect of how this fits into our email classification system, or discuss any other potential improvements?