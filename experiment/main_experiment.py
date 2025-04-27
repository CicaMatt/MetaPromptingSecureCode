from experiment import create_languages_files, create_questions_files, create_question_folders, \
    match_question_to_template, process_prompts, response_to_code, check_snippets_adherence, create_question_folders, \
    copy_contents_to_all_question_folders, empty_and_rename_files_by_language, empty_leaf_txt_files


# Path to the questions csv
csv_path = "/data/questions.csv"

# Path to the experiment folder
experiment_folder = "/experiment_data"


class ExperimentRun:
    def __init__(self):
        question_files = experiment_folder + "/Problem Descriptions"
        languages_files = experiment_folder + "/Problem Languages"
        secure_prompt_patterns_path = experiment_folder + "/Secure prompt templates (PC)"
        to_validate_path = experiment_folder + "/To Validate"
        full_answers_path = experiment_folder + "/Full Answers"
        validation_path = experiment_folder + "/Validation"


        ### Create questions and languages folder starting from the original questions csv
        create_questions_files(question_files, csv_path)
        create_languages_files(languages_files, csv_path)

        ### Create the structure needed to run the experiment
        create_question_folders(languages_files, to_validate_path)
        create_question_folders(languages_files, full_answers_path)
        create_question_folders(languages_files, validation_path)

        ### Populate the folder to validate with the templates, and prepare the answers files and the snippet files
        copy_contents_to_all_question_folders(secure_prompt_patterns_path, to_validate_path)
        copy_contents_to_all_question_folders(secure_prompt_patterns_path, full_answers_path)
        copy_contents_to_all_question_folders(secure_prompt_patterns_path, validation_path)
        empty_leaf_txt_files(full_answers_path)
        empty_and_rename_files_by_language(validation_path)

        ### Fill the template's placeholders with the necessary fields based on the questions body
        match_question_to_template(to_validate_path, question_files, languages_files)

        ### Run the prompts over the LLMs
        process_prompts(to_validate_path, full_answers_path)

        ### Extract the code from the LLM's responses
        response_to_code(full_answers_path, validation_path)

        ### Verify that the snippets adheres to the expected language
        check_snippets_adherence(validation_path)

ExperimentRun()