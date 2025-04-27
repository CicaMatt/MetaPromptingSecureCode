import os
import google.generativeai as genai

def send_prompt_gemini(prompt):
    genai.configure(api_key=os.environ.get('GOOGLE_API_KEY'))

    try:
        model = genai.GenerativeModel("gemini-1.5-pro")
        response = model.generate_content(prompt)
        return response.text

    except Exception as e:
        return f"Error during request: {e}"

prompt = r'''
'''

# print(send_prompt_gemini(prompt))
