import os
import re

import emoji
from openai import OpenAI

def remove_emojis(text):
    text = emoji.replace_emoji(text, replace='')
    return text

def send_prompt_deepseek(prompt):
    client = OpenAI(api_key=os.environ['DEEPSEEK_API_KEY'], base_url="https://api.deepseek.com")
    try:
        response = client.chat.completions.create(
            messages=[
                {
                    "role": "user",
                    "content": prompt,
                }
            ],
            model="deepseek-chat",
        )
        return remove_emojis(response.choices[0].message.content)

    except Exception as e:
        return f"Error during request: {e}"


prompt = r'''
'''

# print(send_prompt_deepseek(prompt))