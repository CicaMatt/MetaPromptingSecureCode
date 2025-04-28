from analysis import calculate_ratios_per_question_multicolumn, calculate_mean, print_improvements, \
    count_all_cwe_ids, check_normality_per_column, most_frequent_cwe, wilcoxon_test_cross_template, \
    count_selected_cwe_ids, \
    calculate_ratios_per_question_single, wilcoxon_test_cross_model


experiment_folder = "/data/experiment_results"

baseline = experiment_folder + "/Baseline.csv"

cwe_259 = experiment_folder + "/CWE-259.csv"
baseline_259 = experiment_folder + "/Baseline - CWE-259.csv"
processed_259 = experiment_folder + "/CWE-259 - Rate.csv"
processed_baseline_259 = experiment_folder + "/Baseline - CWE-259 - Rate.csv"
mean_259 = experiment_folder + "/CWE-259 - Mean.csv"
mean_baseline_259 = experiment_folder + "/Baseline - CWE-259 - Mean.csv"

cwe_295 = experiment_folder + "/CWE-295.csv"
baseline_295 = experiment_folder + "/Baseline - CWE-295.csv"
processed_295 = experiment_folder + "/CWE-295 - Rate.csv"
processed_baseline_295 = experiment_folder + "/Baseline - CWE-295 - Rate.csv"
mean_295 = experiment_folder + "/CWE-295 - Mean.csv"
mean_baseline_295 = experiment_folder + "/Baseline - CWE-295 - Mean.csv"

cwe_327 = experiment_folder + "/CWE-327.csv"
baseline_327 = experiment_folder + "/Baseline - CWE-327.csv"
processed_327 = experiment_folder + "/CWE-327 - Rate.csv"
processed_baseline_327 = experiment_folder + "/Baseline - CWE-327 - Rate.csv"
mean_327 = experiment_folder + "/CWE-327 - Mean.csv"
mean_baseline_327 = experiment_folder + "/Baseline - CWE-327 - Mean.csv"

cwe_397 = experiment_folder + "/CWE-397.csv"
baseline_397 = experiment_folder + "/Baseline - CWE-397.csv"
processed_397 = experiment_folder + "/CWE-397 - Rate.csv"
processed_baseline_397 = experiment_folder + "/Baseline - CWE-397 - Rate.csv"
mean_397 = experiment_folder + "/CWE-397 - Mean.csv"
mean_baseline_397 = experiment_folder + "/Baseline - CWE-397 - Mean.csv"

cwe_477 = experiment_folder + "/CWE-477.csv"
baseline_477 = experiment_folder + "/Baseline - CWE-477.csv"
processed_477 = experiment_folder + "/CWE-477 - Rate.csv"
processed_baseline_477 = experiment_folder + "/Baseline - CWE-477 - Rate.csv"
mean_477 = experiment_folder + "/CWE-477 - Mean.csv"
mean_baseline_477 = experiment_folder + "/Baseline - CWE-477 - Mean.csv"

cwe_798 = experiment_folder + "/CWE-798.csv"
baseline_798 = experiment_folder + "/Baseline - CWE-798.csv"
processed_798 = experiment_folder + "/CWE-798 - Rate.csv"
processed_baseline_798 = experiment_folder + "/Baseline - CWE-798 - Rate.csv"
mean_798 = experiment_folder + "/CWE-798 - Mean.csv"
mean_baseline_798 = experiment_folder + "/Baseline - CWE-798 - Mean.csv"

cwe_all = experiment_folder + "/CWE-ALL.csv"
baseline_all = experiment_folder + "/Baseline - CWE-ALL.csv"
processed_all = experiment_folder + "/CWE-ALL - Rate.csv"
processed_all_multi = experiment_folder + "/Baseline - CWE-ALL - Rate.csv"
mean_all = experiment_folder + "/CWE-ALL - Mean.csv"
mean_all_multi = experiment_folder + "/Baseline - CWE-ALL - Mean.csv"



class QuantitativeAnalysis:
    def __init__(self):
        print("---------------------------------------------------------------")
        print("--------------------Quantitative Analysis-----------------------")
        print("---------------------------------------------------------------\n\n")

        calculate_ratios_per_question_multicolumn(baseline_259, processed_baseline_259)
        calculate_ratios_per_question_multicolumn(baseline_295, processed_baseline_295)
        calculate_ratios_per_question_multicolumn(baseline_327, processed_baseline_327)
        calculate_ratios_per_question_multicolumn(baseline_397, processed_baseline_397)
        calculate_ratios_per_question_multicolumn(baseline_477, processed_baseline_477)
        calculate_ratios_per_question_multicolumn(baseline_798, processed_baseline_798)
        calculate_ratios_per_question_multicolumn(baseline_all, processed_all_multi)

        calculate_mean(processed_baseline_259, mean_baseline_259)
        calculate_mean(processed_baseline_295, mean_baseline_295)
        calculate_mean(processed_baseline_327, mean_baseline_327)
        calculate_mean(processed_baseline_397, mean_baseline_397)
        calculate_mean(processed_baseline_477, mean_baseline_477)
        calculate_mean(processed_baseline_798, mean_baseline_798)
        calculate_mean(processed_all_multi, mean_all_multi)


        print("Baseline - CWE-259 | Improvement:")
        print_improvements(mean_baseline_259)
        print("\nBaseline - CWE-295 | Improvement:")
        print_improvements(mean_baseline_295)
        print("\nBaseline - CWE-327 | Improvement:")
        print_improvements(mean_baseline_327)
        print("\nBaseline - CWE-397 | Improvement:")
        print_improvements(mean_baseline_397)
        print("\nBaseline - CWE-477 | Improvement:")
        print_improvements(mean_baseline_477)
        print("\nBaseline - CWE-798 | Improvement:")
        print_improvements(mean_baseline_798)
        print("\nBaseline - CWE-ALL | Improvement:")
        print_improvements(mean_all_multi)
        print("---------------------------------------------------------------\n\n")


class QualitativeAnalysis:
    def __init__(self):
        print("---------------------------------------------------------------")
        print("--------------------Qualitative Analysis-----------------------")
        print("---------------------------------------------------------------\n\n")

        print("Baseline | Stats:")
        # count_all_cwe_ids(baseline)
        count_selected_cwe_ids(baseline)
        print("\n\nCWE-259 | Stats:")
        #count_all_cwe_ids(cwe_259)
        count_selected_cwe_ids(cwe_259)
        print("\n\nCWE-295 | Stats:")
        # count_all_cwe_ids(cwe_295)
        count_selected_cwe_ids(cwe_295)
        print("\n\nCWE-327 | Stats:")
        #count_all_cwe_ids(cwe_327)
        count_selected_cwe_ids(cwe_327)
        print("\n\nCWE-397 | Stats:")
        # count_all_cwe_ids(cwe_397)
        count_selected_cwe_ids(cwe_397)
        print("\n\nCWE-477 | Stats:")
        # count_all_cwe_ids(cwe_477)
        count_selected_cwe_ids(cwe_477)
        print("\n\nCWE-798 | Stats:")
        # count_all_cwe_ids(cwe_798)
        count_selected_cwe_ids(cwe_798)
        print("\n\nCWE-ALL | Stats:")
        # count_all_cwe_ids(cwe_all)
        count_selected_cwe_ids(cwe_all)
        print("---------------------------------------------------------------\n\n")




class StatisticalTestsCrossTemplate:
    def __init__(self):
        print("---------------------------------------------------------------")
        print("---------------Statistical Tests Cross Template----------------")
        print("---------------------------------------------------------------\n\n")

        # check_normality_per_column(processed_baseline_259)
        # check_normality_per_column(processed_baseline_295)
        # check_normality_per_column(processed_baseline_327)
        # check_normality_per_column(processed_baseline_397)
        # check_normality_per_column(processed_baseline_477)
        # check_normality_per_column(processed_baseline_798)
        # check_normality_per_column(processed_all)

        print("Baseline - CWE-259 | Statistical Test:")
        wilcoxon_test_cross_template(processed_baseline_259)
        print("\nBaseline - CWE-295 | Statistical Test:")
        wilcoxon_test_cross_template(processed_baseline_295)
        print("\nBaseline - CWE-327 | Statistical Test:")
        wilcoxon_test_cross_template(processed_baseline_327)
        print("\nBaseline - CWE-397 | Statistical Test:")
        wilcoxon_test_cross_template(processed_baseline_397)
        print("\nBaseline - CWE-477 | Statistical Test:")
        wilcoxon_test_cross_template(processed_baseline_477)
        print("\nBaseline - CWE-798 | Statistical Test:")
        wilcoxon_test_cross_template(processed_baseline_798)
        print("\nBaseline - CWE-ALL | Statistical Test:")
        wilcoxon_test_cross_template(processed_all_multi)
        print("---------------------------------------------------------------\n\n")


class StatisticalTestsCrossModel:
    def __init__(self):
        print("---------------------------------------------------------------")
        print("---------------Statistical Tests Cross Model----------------")
        print("---------------------------------------------------------------\n\n")
        calculate_ratios_per_question_single(cwe_259, processed_259)
        calculate_ratios_per_question_single(cwe_295, processed_295)
        calculate_ratios_per_question_single(cwe_327, processed_327)
        calculate_ratios_per_question_single(cwe_397, processed_397)
        calculate_ratios_per_question_single(cwe_477, processed_477)
        calculate_ratios_per_question_single(cwe_798, processed_798)
        calculate_ratios_per_question_single(cwe_all, processed_all)

        # check_normality_per_column(processed_259)
        # check_normality_per_column(processed_295)
        # check_normality_per_column(processed_327)
        # check_normality_per_column(processed_397)
        # check_normality_per_column(processed_477)
        # check_normality_per_column(processed_798)
        # check_normality_per_column(processed_all)

        print("CWE-259 | Statistical Test:")
        wilcoxon_test_cross_model(processed_259)
        print("\nCWE-295 | Statistical Test:")
        wilcoxon_test_cross_model(processed_295)
        print("\nCWE-327 | Statistical Test:")
        wilcoxon_test_cross_model(processed_327)
        print("\nCWE-397 | Statistical Test:")
        wilcoxon_test_cross_model(processed_397)
        print("\nCWE-477 | Statistical Test:")
        wilcoxon_test_cross_model(processed_477)
        print("\nCWE-798 | Statistical Test:")
        wilcoxon_test_cross_model(processed_798)
        print("\nCWE-ALL | Statistical Test:")
        wilcoxon_test_cross_model(processed_all)
        print("---------------------------------------------------------------\n\n")


### Calculate the #CWE/LOC rate for each entry, calculate the mean of those rates,
### and calculate the improvement percentages of each template over the baseline
QuantitativeAnalysis()

### Report all the types of CWE detected ordered by frequency, and the total occurrences of CWE
QualitativeAnalysis()

### Normality test and statistical tests for cross template analysis
StatisticalTestsCrossTemplate()

### Normality test and statistical tests for cross model analysis
StatisticalTestsCrossModel()
