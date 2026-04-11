import os
import sys
import requests
from google import genai

def main():
    # 必要な環境変数の取得
    api_key = os.environ.get("GEMINI_API_KEY")
    github_token = os.environ.get("GITHUB_TOKEN")
    pr_diff = os.environ.get("PR_DIFF")
    repo = os.environ.get("GITHUB_REPOSITORY")
    pr_number = os.environ.get("PR_NUMBER")

    if not all([api_key, github_token, pr_diff, repo, pr_number]):
        print("Error: Missing required environment variables.")
        sys.exit(1)

    # クライアント初期化 (google-genai)
    client = genai.Client(api_key=api_key)

    try:
        # モデル名に 'models/' は含めず、シンプルに指定
        model_id = "gemini-1.5-flash" 
        
        prompt = f"""
        あなたは Android/Kotlin 開発のエキスパートとして、プルリクエストのコードレビューを行ってください。
        以下の diff を解析し、建設的なフィードバックを日本語で提供してください。
        
        ---
        Diff:
        {pr_diff}
        """

        # コンテンツ生成
        response = client.models.generate_content(
            model=model_id,
            contents=prompt
        )
        review_comment = response.text

        # GitHub PR へのコメント投稿
        url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"
        headers = {
            "Authorization": f"token {github_token}",
            "Accept": "application/vnd.github.v3+json"
        }
        data = {"body": f"### 🤖 Gemini Code Review\n\n{review_comment}"}
        
        res = requests.post(url, headers=headers, json=data)
        if res.status_code == 201:
            print(f"Successfully posted review from {model_id}.")
        else:
            print(f"Failed to post comment: {res.status_code} {res.text}")

    except Exception as e:
        print(f"Error calling Gemini API: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
