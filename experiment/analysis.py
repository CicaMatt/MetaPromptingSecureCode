import math
from collections import Counter
import numpy as np
from scipy.stats import shapiro, wilcoxon, norm
import pandas as pd



def calculate_ratios_per_question_single(file_path, output_path):
    # Load the original dataset
    df = pd.read_csv(file_path)

    # List of LLMs
    llms = ['gpt', 'gemini', 'deepseek']

    # Empty DataFrame to contain only the new columns
    new_columns_df = pd.DataFrame()

    # Loop for each LLM (solo prompt 's' esiste)
    for llm in llms:
        # Column names for cwe_count and loc (solo 's')
        cwe_col = f"{llm}_cwe_count_s"
        loc_col = f"{llm}_loc_s"

        # Check if the columns exist in the DataFrame
        if all(col in df.columns for col in [cwe_col, loc_col]):
            # Convert columns to numeric, handle NaN
            df[cwe_col] = pd.to_numeric(df[cwe_col], errors='coerce').fillna(0)
            df[loc_col] = pd.to_numeric(df[loc_col], errors='coerce').fillna(0)

            # Calculate new ratios, handle division by zero
            cwe_loc_ratio = df[cwe_col] / df[loc_col].replace({0: pd.NA})

            # Add new column to the DataFrame of new columns
            new_columns_df[f"{llm}_cwe_loc"] = cwe_loc_ratio

    # Save the DataFrame with only the new columns to a new CSV file
    new_columns_df.to_csv(output_path, index=False)



def calculate_ratios_per_question_multicolumn(file_path, output_path):
    # Load the original dataset
    df = pd.read_csv(file_path)

    # List of LLMs and prompts
    # llms = ['gpt', 'gemini', 'codellama']
    llms = ['gpt', 'gemini', 'deepseek']
    prompts = ['b', 's']

    # Empty DataFrame to contain only the new columns
    new_columns_df = pd.DataFrame()

    # Loop for each LLM and each prompt level
    for llm in llms:
        for prompt in prompts:
            # Column names for cwe_count, cq, and loc
            cwe_col = f"{llm}_cwe_count_{prompt}"
            loc_col = f"{llm}_loc_{prompt}"

            # Check if the columns exist in the DataFrame
            if all(col in df.columns for col in [cwe_col, loc_col]):
                # Convert columns to numeric, handle NaN
                df[cwe_col] = pd.to_numeric(df[cwe_col], errors='coerce').fillna(0)
                df[loc_col] = pd.to_numeric(df[loc_col], errors='coerce').fillna(0)

                # Calculate new ratios, handle division by zero
                cwe_loc_ratio = df[cwe_col] / df[loc_col].replace({0: pd.NA})

                # Add new columns to the DataFrame of new columns
                new_columns_df[f"{llm}_cwe_loc_{prompt}"] = cwe_loc_ratio

    # Save the DataFrame with only the new columns to a new CSV file
    new_columns_df.to_csv(output_path, index=False)


def calculate_mean(input_file, output_file):
    # Read the CSV file into a DataFrame
    df = pd.read_csv(input_file)

    # Calculate the mean of each column
    means = df.mean()

    # Create a DataFrame with the means
    df_mean = pd.DataFrame(means, columns=['Mean'])

    # Save the DataFrame of means to a new CSV file
    df_mean.to_csv(output_file)


def calculate_improvement(val_b, val_s):
    return ((val_b - val_s) / val_b) * 100 if val_b != 0 else 0


def print_improvements(file_path):
    df = pd.read_csv(file_path).set_index("Unnamed: 0")["Mean"]

    # Identificare e confrontare le coppie
    gpt_improvement = calculate_improvement(df.get("gpt_cwe_loc_b", 0), df.get("gpt_cwe_loc_s", 0))
    print(f"GPT Improvement: {gpt_improvement:.2f}%")

    gemini_improvement = calculate_improvement(df.get("gemini_cwe_loc_b", 0), df.get("gemini_cwe_loc_s", 0))
    print(f"Gemini Improvement: {gemini_improvement:.2f}%")

    deepseek_improvement = calculate_improvement(df.get("deepseek_cwe_loc_b", 0), df.get("deepseek_cwe_loc_s", 0))
    print(f"Deepseek Improvement: {deepseek_improvement:.2f}%")

    # codellama_improvement = calculate_improvement(df.get("codellama_cwe_loc_b", 0), df.get("codellama_cwe_loc_s", 0))
    # print(f"CodeLlama Improvement: {codellama_improvement:.2f}%")


def count_all_cwe_ids(file_path):
    # Read the CSV file
    df = pd.read_csv(file_path)

    # Filter columns that contain "cwe_id"
    cwe_columns = [col for col in df.columns if 'cwe_id' in col]

    # For each column, count occurrences of cwe_id
    for col in cwe_columns:
        # Join all rows of the column into a list of identifiers
        cwe_list = []
        for value in df[col].dropna():
            # Split space-separated cwe_id and add to the list, excluding "0"
            cwe_list.extend([cwe for cwe in value.split() if cwe != '0'])

        # Count occurrences of cwe_id
        cwe_counter = Counter(cwe_list)

        # Sort by occurrences in descending order
        sorted_cwe = sorted(cwe_counter.items(), key=lambda x: x[1], reverse=True)
        ordered_cwe = sorted([int(cwe) for cwe, _ in cwe_counter.items()])
        cwe_types_number = len(cwe_counter.items())

        # Print the result for the current column
        print(f"Column: {col}")
        for cwe, count in sorted_cwe:
            print(f"{cwe}: {count}")

        # Calculate and print the total CWE found in the current column
        total_cwe = sum(cwe_counter.values())
        print(f"Total CWE per column {col}: {total_cwe}")
        print("CWE ordered by ID: " + str(ordered_cwe))
        print("Total CWE types: " + str(cwe_types_number))
        print("-" * 40)



def count_selected_cwe_ids(file_path):
    # Define the CWEs of interest
    target_cwes = {'259', '295', '327', '397', '477', '798'}

    # Read the CSV file
    df = pd.read_csv(file_path)

    # Filter columns that contain "cwe_id"
    cwe_columns = [col for col in df.columns if 'cwe_id' in col]

    for col in cwe_columns:
        cwe_list = []
        for value in df[col].dropna():
            # Split space-separated CWE IDs and ignore "0"
            cwe_list.extend([cwe for cwe in value.split() if cwe != '0'])

        # Count all CWE occurrences
        cwe_counter = Counter(cwe_list)

        # Initialize counters for selected CWEs and others
        selected_counts = {cwe: cwe_counter.get(cwe, 0) for cwe in target_cwes}
        others_count = sum(count for cwe, count in cwe_counter.items() if cwe not in target_cwes)

        # Print results
        print(f"Column: {col}")
        for cwe in sorted(target_cwes):
            print(f"{cwe}: {selected_counts[cwe]}")
        print(f"Others: {others_count}")

        total_cwe = sum(cwe_counter.values())
        print(f"Total CWE per column {col}: {total_cwe}")
        print("Total CWE types (including Others):", len(cwe_counter))
        print("-" * 40)


def most_frequent_cwe(file_path, considered_columns):
    # Read the CSV file
    df = pd.read_csv(file_path)

    # Filtering by language
    #df = df[df['language'].str.contains('PHP', na=False, case=False)]

    # Filter columns by considered columns
    cwe_columns = [col for col in df.columns if considered_columns in col]
    # List to contain all relevant columns
    all_cwe_list = []

    # For each column, add cwe_id to the general list excluding "0"
    for col in cwe_columns:
        for value in df[col].dropna():
            # Split space-separated cwe_id and add to the general list, excluding "0"
            all_cwe_list.extend([cwe for cwe in value.split() if cwe != '0'])

    # Count occurrences of each cwe_id
    cwe_counter = Counter(all_cwe_list)

    # Sort by occurrences in descending order
    sorted_cwe = sorted(cwe_counter.items(), key=lambda x: x[1], reverse=True)

    # Calculate the general frequency as a ratio of occurrences to total
    total_occurrences = sum(cwe_counter.values())

    print("Ordered list of the most frequent CWE:")
    for cwe, count in sorted_cwe:
        frequency = (count / total_occurrences) * 100  # Calculate the frequency percentage
        print(f"{cwe}: {count} occurrences, {frequency:.2f}% general frequency")
    print("-" * 40)

    top_5_cwe = [f"CWE-{cwe}" for cwe, count in sorted_cwe[:5]]
    print("Top 5 CWE:", ", ".join(top_5_cwe))


def check_normality_per_column(file_path):
    try:
        # Load the CSV file into a DataFrame
        dataframe = pd.read_csv(file_path)

        # Filter columns that contain 'cwe_count', 'cq', or 'loc' in the name
        considered_columns = [col for col in dataframe.columns if
                               any(keyword in col for keyword in ['cwe_count', 'cwe_loc', 'loc'])]

        result = {}

        # For each column, perform the Shapiro-Wilk test
        for column in considered_columns:
            try:
                # Convert the column to numeric values, replacing non-convertible values with NaN
                dataframe[column] = pd.to_numeric(dataframe[column], errors='coerce')

                # Perform the test only if there are at least 3 numeric values
                if dataframe[column].count() >= 3:
                    stat, p_value = shapiro(dataframe[column].dropna())
                    # Check if the p-value is greater than 0.05
                    result[column] = 'Normal' if p_value > 0.05 else 'Non normal'
                else:
                    result[column] = 'Unable to process'
            except Exception as e:
                result[column] = f"Processing error: {e}"

        print(result)
    except FileNotFoundError:
        return "Error: file not found."
    except pd.errors.EmptyDataError:
        return "Error: empty file or unvalid data."
    except Exception as e:
        return f"Error: {e}"


def cohen_label(r):
    if r < 0.1:
        return "trascurabile"
    elif r < 0.3:
        return "piccolo"
    elif r < 0.5:
        return "medio"
    else:
        return "grande"

def wilcoxon_test_cross_template(file_path, field="cwe_loc", alpha=0.05):
    df = pd.read_csv(file_path)

    llms = ['gpt', 'gemini', 'deepseek', 'codellama']
    prompts = ['b', 's']
    results = {}

    for llm in llms:
        columns = {prompt: f"{llm}_{field}_{prompt}" for prompt in prompts}

        if all(col in df.columns for col in columns.values()):
            results[llm] = {}
            try:
                for col in columns.values():
                    df[col] = pd.to_numeric(df[col], errors='coerce')

                valid_data = df[list(columns.values())].dropna()

                x = valid_data[columns['b']]
                y = valid_data[columns['s']]
                N = len(x)

                w_s_b = wilcoxon(x, y)

                # Calcolo effect size
                z = (w_s_b.statistic - (N * (N + 1) / 4)) / np.sqrt(N * (N + 1) * (2 * N + 1) / 24)
                r = abs(z) / np.sqrt(N)
                r_label = cohen_label(r)

                results[llm]['s_b'] = {
                    'statistic': float(w_s_b.statistic),
                    'p_value': float(w_s_b.pvalue),
                    'statistical_difference': w_s_b.pvalue < alpha,
                    'effect_size_r': round(r, 4),
                    'effect_size_label': r_label
                }

            except Exception as e:
                results[llm]['error'] = str(e)

    # Stampa leggibile
    for llm, data in results.items():
        if 's_b' in data:
            res = data['s_b']
            print(f"\nLLM: {llm}")
            print(f" - Wilcoxon statistic: {res['statistic']}")
            print(f" - p-value: {res['p_value']:.6f}")
            print(f" - Statistically significant: {'✅' if res['statistical_difference'] else '❌'}")
            print(f" - Effect size r: {res['effect_size_r']} ({res['effect_size_label']})")
        else:
            print(f"\nLLM: {llm} - Errore: {data.get('error', 'Colonne mancanti')}")

    return results


def wilcoxon_test_cross_model(csv_path):
    """
    Esegue test di Wilcoxon tra GPT, Gemini e DeepSeek su CWE/LOC.
    Stampa risultati formattati con effetto e significatività.
    """
    # Caricamento dati
    df = pd.read_csv(csv_path)
    gpt = df['gpt_cwe_loc']
    gemini = df['gemini_cwe_loc']
    deepseek = df['deepseek_cwe_loc']

    # Lista ordinata dei confronti (GPT-Gemini prima)
    comparisons = [
        ("gpt", "gemini", gpt, gemini),
        ("gpt", "deepseek", gpt, deepseek),
        ("gemini", "deepseek", gemini, deepseek)
    ]

    # Effect size con classificazione
    def effect_size(p, n):
        if p == 0:
            return float('inf'), "grande"
        z = abs(norm.ppf(p / 2))
        r = z / math.sqrt(n)
        if r < 0.3:
            label = "piccolo"
        elif r < 0.5:
            label = "medio"
        else:
            label = "grande"
        return r, label

    # Esecuzione dei test
    for model1, model2, data1, data2 in comparisons:
        stat, p = wilcoxon(data1, data2)
        r, label = effect_size(p, len(data1))
        significant = "✅" if p < 0.05 else "❌"
        print(f"\nLLM: {model1} vs {model2}")
        print(f" - Wilcoxon statistic: {stat}")
        print(f" - p-value: {p:.6f}")
        print(f" - Statistically significant: {significant}")
        print(f" - Effect size r: {r:.4f} ({label})")


