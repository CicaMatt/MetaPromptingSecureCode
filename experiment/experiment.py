import csv
import os
import re
import shutil

from prompting.deepseek import send_prompt_deepseek
from prompting.gemini import send_prompt_gemini
from prompting.gpt import send_prompt_gpt


def create_questions_files(output_folder, csv_file):
    # Create the output folder if it doesn't exist
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # Open and read the CSV file
    with open(csv_file, mode='r', newline='', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            so_id = row.get('so_id')
            question = row.get('question')
            if so_id is None or question is None:
                print(f"Skipping row with missing 'so_id' or 'question': {row}")
                continue

            # Define the full path for the new .txt file
            txt_file_path = os.path.join(output_folder, f"{so_id}.txt")

            # Write the question content into the file
            with open(txt_file_path, 'w', encoding='utf-8') as txt_file:
                txt_file.write(question)
            print(f"Created {txt_file_path}")

def create_languages_files(output_folder, csv_file):
    # Create the output folder if it doesn't exist
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # Open and read the CSV file
    with open(csv_file, mode='r', newline='', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            so_id = row.get('so_id')
            language = row.get('language')
            question = row.get('question')
            if so_id is None or question is None:
                print(f"Skipping row with missing 'so_id' or 'question': {row}")
                continue

            # Define the full path for the new .txt file
            txt_file_path = os.path.join(output_folder, f"{so_id}.txt")

            # Write the question content into the file
            with open(txt_file_path, 'w', encoding='utf-8') as txt_file:
                txt_file.write(language)
            print(f"Created {txt_file_path}")


def extract_language_from_file(file_path):
    with open(file_path, 'r') as f:
        return f.read().strip()

def create_question_folders(source_dir, target_dir):
    if not os.path.exists(source_dir):
        print(f"Source directory '{source_dir}' does not exist.")
        return

    if not os.path.exists(target_dir):
        os.makedirs(target_dir)

    for file_name in os.listdir(source_dir):
        file_path = os.path.join(source_dir, file_name)
        if os.path.isfile(file_path):
            question_id = os.path.splitext(file_name)[0]
            language = extract_language_from_file(file_path)

            language_dir = os.path.join(target_dir, language)
            if not os.path.exists(language_dir):
                os.makedirs(language_dir)

            question_dir = os.path.join(language_dir, question_id)
            if not os.path.exists(question_dir):
                os.makedirs(question_dir)
                print(f"Created directory: {question_dir}")
            else:
                print(f"Directory already exists: {question_dir}")



def copy_contents_to_all_question_folders(source_dir, target_dir):
    if not os.path.exists(source_dir):
        print(f"Source directory '{source_dir}' does not exist.")
        return

    if not os.path.exists(target_dir):
        print(f"Target directory '{target_dir}' does not exist.")
        return

    for language_folder in os.listdir(target_dir):
        language_path = os.path.join(target_dir, language_folder)
        if os.path.isdir(language_path):
            for question_folder in os.listdir(language_path):
                question_path = os.path.join(language_path, question_folder)
                if os.path.isdir(question_path) and question_folder.isdigit():
                    # Now copy all contents of source_dir into this question folder
                    for item in os.listdir(source_dir):
                        s = os.path.join(source_dir, item)
                        d = os.path.join(question_path, item)
                        if os.path.isdir(s):
                            shutil.copytree(s, d, dirs_exist_ok=True)
                        else:
                            shutil.copy2(s, d)
                    print(f"Copied contents into: {question_path}")


def empty_and_rename_files_by_language(target_dir):
    if not os.path.exists(target_dir):
        print(f"Target directory '{target_dir}' does not exist.")
        return

    # Map language folders to their extensions
    language_extensions = {
        "Python": ".py",
        "Java": ".java",
        "PHP": ".php"
    }

    for language_folder in os.listdir(target_dir):
        language_path = os.path.join(target_dir, language_folder)
        if os.path.isdir(language_path) and language_folder in language_extensions:
            extension = language_extensions[language_folder]

            for question_folder in os.listdir(language_path):
                question_path = os.path.join(language_path, question_folder)
                if os.path.isdir(question_path):
                    for root, _, files in os.walk(question_path):
                        for file in files:
                            file_path = os.path.join(root, file)

                            # Empty the file
                            with open(file_path, 'w') as f:
                                pass

                            # Change extension
                            base_name = os.path.splitext(file)[0]
                            new_file_path = os.path.join(root, base_name + extension)

                            # Rename file if needed
                            if file_path != new_file_path:
                                os.rename(file_path, new_file_path)
                                print(f"Emptied and renamed: {file_path} -> {new_file_path}")
                            else:
                                print(f"Emptied: {file_path}")


def empty_leaf_txt_files(target_dir):
    if not os.path.exists(target_dir):
        print(f"Target directory '{target_dir}' does not exist.")
        return

    for root, _, files in os.walk(target_dir):
        for file in files:
            if file.lower().endswith('.txt'):
                file_path = os.path.join(root, file)

                # Empty the .txt file
                with open(file_path, 'w') as f:
                    pass

                print(f"Emptied: {file_path}")


def replace_question_placeholder(text, question_text, placeholder ="[Problem Statement]"):
    quoted_question_text = f'"{question_text}"'
    return text.replace(placeholder, quoted_question_text)

def replace_language_placeholder(text, language_text, placeholder ="[Solution Language]"):
    return text.replace(placeholder, language_text)


def match_question_to_template(secure_templates_dir, problems_dir, languages_dir):
    # Iterate over each programming language folder (e.g., Java, Python, PHP)
    for language in os.listdir(secure_templates_dir):
        language_path = os.path.join(secure_templates_dir, language)
        if not os.path.isdir(language_path):
            continue  # Skip files; we expect directories here

        # For each question directory inside the language folder:
        for question_id in os.listdir(language_path):
            question_id_path = os.path.join(language_path, question_id)
            if not os.path.isdir(question_id_path):
                continue

            # Construct the path to the corresponding problem file.
            # Here we assume the problem file is named "<question_id>.txt".
            problem_file = os.path.join(problems_dir, f"{question_id}.txt")
            if not os.path.exists(problem_file):
                print(f"Problem file for question ID '{question_id}' not found. Skipping.")
                continue

            # Read the content of the problem file.
            with open(problem_file, "r", encoding="utf-8", errors="replace") as pf:
                problem_text = pf.read()

            language_file = os.path.join(languages_dir, f"{question_id}.txt")
            if not os.path.exists(language_file):
                print(f"Language file for question ID '{question_id}' not found. Skipping.")
                continue

            # Read the content of the language file.
            with open(language_file, "r", encoding="utf-8", errors="replace") as pf:
                language_text = pf.read()

            # Now iterate over the CWE directories (subdirectories within the question id folder)
            for cwe_dir in os.listdir(question_id_path):
                cwe_dir_path = os.path.join(question_id_path, cwe_dir)
                if not os.path.isdir(cwe_dir_path):
                    continue

                # Iterate over the prompt files (GPT, Gemini, Codellama)
                for prompt_filename in os.listdir(cwe_dir_path):
                    if prompt_filename.startswith('.'):
                        continue  # Skip hidden files like .DS_Store
                    file_path = os.path.join(cwe_dir_path, prompt_filename)
                    if not os.path.isfile(file_path):
                        continue

                    # Read the current content of the file, handling decoding errors.
                    with open(file_path, "r", encoding="utf-8", errors="replace") as file:
                        content = file.read()

                    # Replace the placeholder with the problem text.
                    new_content_1 = replace_question_placeholder(content, problem_text)
                    new_content_2 = replace_language_placeholder(new_content_1, language_text)

                    # Write the new content back to the file.
                    with open(file_path, "w", encoding="utf-8", errors="replace") as file:
                        file.write(new_content_2)

                    print(f"Updated placeholders in file: {file_path}")

# Time to execute: 45 minutes per 100 prompts
def process_prompts(to_validate_dir, validation_dir):
    processed_prompts = 0

    for language in os.listdir(to_validate_dir):
        language_path = os.path.join(to_validate_dir, language)
        if not os.path.isdir(language_path):
            continue  # Salta eventuali file che non sono directory

        for question_id in os.listdir(language_path):
            question_id_path = os.path.join(language_path, question_id)
            if not os.path.isdir(question_id_path):
                continue

            for cwe_dir in os.listdir(question_id_path):
                cwe_dir_path = os.path.join(question_id_path, cwe_dir)
                if not os.path.isdir(cwe_dir_path):
                    continue

                for prompt_filename in os.listdir(cwe_dir_path):
                    if prompt_filename.startswith('.'):
                        continue

                    file_path = os.path.join(cwe_dir_path, prompt_filename)
                    if not os.path.isfile(file_path):
                        continue

                    with open(file_path, "r", encoding="utf-8", errors="replace") as file:
                        prompt_content = file.read()


                    llm = os.path.splitext(prompt_filename)[0]
                    print(f"Prompting: {language} - {question_id} - {cwe_dir} to {llm}")
                    # print(f"Question:\n'''{prompt_content}'''\n")
                    if llm == "GPT":
                        response = send_prompt_gpt(prompt_content)
                    elif llm == "Gemini":
                        response = send_prompt_gemini(prompt_content)
                    # elif llm == "Codellama":
                    #    response = send_prompt_codellama(prompt_content)
                    elif llm == "Deepseek":
                        response = send_prompt_deepseek(prompt_content)
                    else:
                        print(f"Skipping {file_path} due to unrecognized LLM")
                        continue

                    # Use same directory structure
                    output_dir = os.path.join(validation_dir, language, question_id, cwe_dir)
                    os.makedirs(output_dir, exist_ok=True)
                    output_file_path = os.path.join(output_dir, prompt_filename)

                    with open(output_file_path, "w", encoding="utf-8") as out_file:
                        out_file.write(response)

                    print(f"Answer produced and saved in: {output_file_path}")
                    processed_prompts += 1
                    print(f"Processed prompts: {processed_prompts}")
                    print(f"-----------------------------------------------------------------------------------------\n\n\n")


def extract_code_from_llm_response(file_path):
    """
    Extracts the source code from an LLM response stored in a file.

    The function looks for code blocks delimited by either:
      - Triple backticks (```) or triple single quotes ('''), optionally with a language marker.
      - [PYTHON] and [/PYTHON]

    In the first alternative, only code blocks with a language marker of python, php, or java are extracted.

    If one or more code blocks are found, the contents inside them are concatenated and returned.
    Otherwise, the entire file content is assumed to be code and is returned unchanged.

    Parameters:
        file_path (str): Path to the file containing the LLM response.

    Returns:
        str: The extracted source code.
    """
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except IOError as e:
        raise RuntimeError(f"Could not read file {file_path}: {e}")

    # Allowed languages for triple-quote code blocks.
    allowed_languages = {"python", "php", "java"}

    # The regex now includes a named group "lang" to capture the language marker.
    pattern = re.compile(
        r"""
        (?:  # first alternative: triple backticks or triple single quotes
            (?P<backtick>```|''')
            \s*(?P<lang>\w+)?        # optional language identifier (e.g., python, php, java, etc.)
            \s*\n
            (?P<code1>.*?)
            \n?(?P=backtick)
        )
        |
        (?:  # second alternative: [PYTHON] ... [/PYTHON]
            \[PYTHON\]\s*\n?
            (?P<code2>.*?)
            \n?\[/PYTHON\]
        )
        """,
        re.DOTALL | re.VERBOSE
    )

    code_blocks = []
    for match in pattern.finditer(content):
        # Se abbiamo un blocco dalla prima alternativa, controlla il marcatore di linguaggio.
        if match.group('code1') is not None:
            lang = match.group('lang')
            if lang is not None and lang.lower() in allowed_languages:
                code_blocks.append(match.group('code1').rstrip())
        # Se il blocco proviene dalla seconda alternativa ([PYTHON] ... [/PYTHON]), lo estraiamo direttamente.
        elif match.group('code2') is not None:
            code_blocks.append(match.group('code2').rstrip())

    if code_blocks:
        return "\n".join(code_blocks).strip()
    else:
        # Se non vengono trovati blocchi di codice, si assume che l'intero contenuto sia codice.
        print(f"{file_path}: No code blocks found")
        return content.strip()


def response_to_code(full_answers_path, only_code_path):
    for language in os.listdir(full_answers_path):
        language_path = os.path.join(full_answers_path, language)
        if not os.path.isdir(language_path):
            continue

        for question_id in os.listdir(language_path):
            question_id_path = os.path.join(language_path, question_id)
            if not os.path.isdir(question_id_path):
                continue

            for cwe_dir in os.listdir(question_id_path):
                cwe_dir_path = os.path.join(question_id_path, cwe_dir)
                if not os.path.isdir(cwe_dir_path):
                    continue

                for prompt_filename in os.listdir(cwe_dir_path):
                    if prompt_filename.startswith('.'):
                        continue
                    # if prompt_filename.startswith('GPT') or prompt_filename.startswith('Gemini'):
                    #    continue

                    file_path = os.path.join(cwe_dir_path, prompt_filename)
                    if not os.path.isfile(file_path):
                        continue

                    # extracted_code = extract_code_from_llm_response(file_path)
                    extracted_code = extract_code_from_llm_response(file_path)
                    llm = os.path.splitext(prompt_filename)[0]
                    destination = ""
                    if language == "Python":
                        destination = llm + ".py"
                    elif language == "Java":
                        destination = llm + ".java"
                    elif language == "PHP":
                        destination = llm + ".php"


                    output_dir = os.path.join(only_code_path, language, question_id, cwe_dir)
                    os.makedirs(output_dir, exist_ok=True)
                    output_file_path = os.path.join(output_dir, destination)

                    with open(output_file_path, "w", encoding="utf-8") as out_file:
                        out_file.write(extracted_code)


def is_code_matching_language(file_path):
    """
    Analyzes a code snippet file and prints a message only if the code does not
    appear to match the language implied by the file extension (.py, .java, or .php).

    Parameters:
        file_path (str): Path to the code snippet file.
    """
    ext = os.path.splitext(file_path)[1].lower()

    # Determine the expected language based on the file extension.
    if ext == '.py':
        expected_language = 'python'
    elif ext == '.java':
        expected_language = 'java'
    elif ext == '.php':
        expected_language = 'php'
    else:
        print(f"Unsupported file extension: {ext}")
        return

    # Read the file content.
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            code = f.read()
    except Exception as e:
        print(f"Error reading file '{file_path}': {e}")
        return

    # Define and apply language-specific heuristics.
    if expected_language == 'python':
        python_patterns = [
            r'^\s*def\s+\w+\(.*\):',  # function definitions
            r'^\s*class\s+\w+\(?.*\)?:',  # class definitions
            r'^\s*import\s+\w+',  # import statements
            r'if\s+__name__\s*==\s*[\'"]__main__[\'"]\s*:'  # main guard
        ]
        # Check for any of the Python patterns, including a shebang line.
        match_found = any(re.search(pattern, code, re.MULTILINE) for pattern in python_patterns) \
                      or re.search(r'#!.*python', code, re.IGNORECASE)
        if not match_found:
            print(f"Mismatch: '{file_path}' does not appear to be valid Python code.")

    elif expected_language == 'java':
        java_patterns = [
            r'\bpublic\s+class\s+\w+\s*{',  # public class declaration
            r'\bpublic\s+static\s+void\s+main\s*\(',  # main method signature
            r'\bpackage\s+[\w\.]+;',  # package declaration
            r'\bimport\s+java\.'  # java-specific import
        ]
        match_found = any(re.search(pattern, code) for pattern in java_patterns)
        if not match_found:
            print(f"Mismatch: '{file_path}' does not appear to be valid Java code.")

    elif expected_language == 'php':
        # Check for the PHP opening tag.
        if not re.search(r'<\?php', code, re.IGNORECASE):
            print(f"Mismatch: '{file_path}' does not appear to be valid PHP code.")


def check_snippets_adherence(path):
    for language in os.listdir(path):
        language_path = os.path.join(path, language)
        if not os.path.isdir(language_path):
            continue

        for question_id in os.listdir(language_path):
            question_id_path = os.path.join(language_path, question_id)
            if not os.path.isdir(question_id_path):
                continue

            for cwe_dir in os.listdir(question_id_path):
                cwe_dir_path = os.path.join(question_id_path, cwe_dir)
                if not os.path.isdir(cwe_dir_path):
                    continue

                for prompt_filename in os.listdir(cwe_dir_path):
                    if prompt_filename.startswith('.'):
                        continue

                    file_path = os.path.join(cwe_dir_path, prompt_filename)
                    if not os.path.isfile(file_path):
                        continue

                    is_code_matching_language(file_path)
                    # is_code_matching_language_parser(file_path)
