import os
import sys
# インポートはこれだけにする
from google import genai

def main():
    api_key = os.environ.get("GEMINI_API_KEY")
    if not api_key:
        print("GEMINI_API_KEY is missing")
        sys.exit(1)

    # クライアント作成
    client = genai.Client(api_key=api_key)

    try:
        # モデル名に 'models/' は付けない
        model_name = "gemini-1.5-flash"

        response = client.models.generate_content(
            model=model_name,
            contents="Hello! This is a test from GitHub Actions. Reply with 'Success' if you can hear me."
        )

        print(f"--- Response from {model_name} ---")
        print(response.text)

    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()