import os
import sys
import requests
import time
from google import genai
from google.genai import errors

def main():
    gemini_api_key = os.getenv("GEMINI_API_KEY")
    github_token = os.getenv("GITHUB_TOKEN")
    pr_diff = os.getenv("PR_DIFF")
    repo = os.getenv("GITHUB_REPOSITORY")
    pr_number = os.getenv("PR_NUMBER")

    if not all([gemini_api_key, github_token, pr_diff, repo, pr_number]):
        print("Missing required environment variables.")
        sys.exit(1)

    client = genai.Client(api_key=gemini_api_key)
    
    # 無料枠で最も利用可能な可能性が高い gemini-1.5-flash を使用
    model_id = "gemini-1.5-flash"

    prompt = f"""
    あなたは Android/Kotlin 開発のエキスパートとして、プルリクエストのコードレビューを行ってください。
    以下の diff を解析し、建設的なフィードバックを日本語で提供してください。

    重点項目:
    1. バグやロジックミスの指摘。
    2. パフォーマンス（無駄な再レンダリング、メモリリーク等）の改善案。
    3. Kotlin/Android のベストプラクティス（Coroutines, Hilt, Compose 等）の遵守。
    4. コードの可読性と保守性。

    レビューは簡潔に、具体的な修正案を含めて日本語で回答してください。
    
    ---
    Diff:
    {pr_diff}
    """

    review_comment = None
    # 429 エラー時のための簡単なリトライ
    for attempt in range(2):
        try:
            response = client.models.generate_content(
                model=model_id,
                contents=prompt
            )
            review_comment = response.text
            break
        except errors.ClientError as e:
            if "429" in str(e) and attempt == 0:
                print("Rate limit exceeded. Waiting 30 seconds before retrying...")
                time.sleep(30)
                continue
            print(f"Error calling Gemini API with {model_id}: {e}")
            sys.exit(1)
        except Exception as e:
            print(f"Unexpected error: {e}")
            sys.exit(1)

    if not review_comment:
        print("Failed to generate review comment.")
        sys.exit(1)

    # Post comment to GitHub
    url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"
    headers = {
        "Authorization": f"token {github_token}",
        "Accept": "application/vnd.github.v3+json"
    }
    data = {"body": f"### 🤖 Gemini Code Review\n\n{review_comment}"}
    
    res = requests.post(url, headers=headers, json=data)
    if res.status_code == 201:
        print("Review comment posted successfully.")
    else:
        print(f"Failed to post comment: {res.status_code} {res.text}")

if __name__ == "__main__":
    main()
