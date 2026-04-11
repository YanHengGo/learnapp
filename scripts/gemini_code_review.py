import os
import sys
import requests
from google import genai

def main():
    gemini_api_key = os.getenv("GEMINI_API_KEY")
    github_token = os.getenv("GITHUB_TOKEN")
    pr_diff = os.getenv("PR_DIFF")
    repo = os.getenv("GITHUB_REPOSITORY")
    pr_number = os.getenv("PR_NUMBER")

    if not all([gemini_api_key, github_token, pr_diff, repo, pr_number]):
        print("Missing required environment variables.")
        if not gemini_api_key: print("Missing GEMINI_API_KEY")
        if not github_token: print("Missing GITHUB_TOKEN")
        if not pr_diff: print("Missing PR_DIFF")
        if not repo: print("Missing GITHUB_REPOSITORY")
        if not pr_number: print("Missing PR_NUMBER")
        sys.exit(1)

    # Configure Gemini with the NEW SDK (google-genai)
    client = genai.Client(api_key=gemini_api_key)
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

    try:
        # The new SDK uses client.models.generate_content
        response = client.models.generate_content(
            model=model_id,
            contents=prompt
        )
        review_comment = response.text
    except Exception as e:
        print(f"Error calling Gemini API: {e}")
        # Optionally print more info for debugging
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
