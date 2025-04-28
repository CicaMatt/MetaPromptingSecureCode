<div align="center">
  
  <h2 align="center">From Prompt to Secure Code: A Meta Prompting Approach for Secure Code Generation through LLMs</h2>

  <p align="center">
    A benchmark study on the impact of Meta-Prompting on the security and quality of artificially generated code.
  </p>
</div>

## About The Project
This project explores a meta-prompting approach to improve the security of LLM-generated code. By creating structured prompt templates based on real-world vulnerabilities (CWEs), the study demonstrates how prompt engineering can significantly reduce security flaws across different models and programming languages.


## Repository Contents
### _data_ 
Contains all the data needed to run the experiment and evaluate its results. The _questions.csv_ file contains the collected questions for the study. The _experiment_result_ folder collects all the csv related to the baseline, the fine-grained, and the general-purpose template. 

### _experiment_data_
Contains all the needed subfolder needed for the experiment. Specifically, the folder is organized as follows:
- Problem Description, which contains the question bodies of the extracted questions
- Problem Languages, which contains the programming languages of the extracted questions
- Secure prompt templates (Original), which contains the plain secure templates generated through the meta-prompting approach
- Secure prompt templates (PC), which contains the secure templates where the question and language placeholder have been adjusted to match the defined placeholder needed for the experiment pipeline
- To Validate, which contains the prompts obtained by populating the templates with the related question body and programming language
- Full Answers, which contains the whole answer produced by the LLMs by inquiring them with the produced secure prompt
- Validation, which contains the code snippets extracted by the full answers produced by the LLMs

### _experiment_
Contains all the necessary scripts needed to perform the experiment:
- _experiment.py_: contains all the function needed to build the experiment folder and conduct the approach, starting from the question folders and secure templates to the final code snippets
- _main_experiment.py_: collects all the previous functions in an organized way
- _analysis_: contains all the functions needed to collect metrics and analyze the results of the security analysis on the code snippets
- _main_analysis_: collects all the previous functions in an organized way, allowing to perform quantitative and qualitative analysis, along with statistical tests

### _prompting_
Contains all the necessary scripts needed to prompt ChatGPT, Gemini and DeepSeek. In order for those script to work properly, all the API keys needs to be stored in the OS environment variables.

### _env_
Contains the .yaml file needed to replicate the environment.



## Getting Started

### Prerequisites
Python 3.12

### Installation

1. Clone the Repository:
   ```sh
   git clone https://github.com/CicaMatt/PromptData.git
   ```
2. Install Dependencies:
   ```sh
   conda env create -f metaprompting.yaml
   ```
   
## Contact Us!
mcicalese@unisa.it
<br>
fpalomba@unisa.it
<br>
ggiordano@unisa.it

