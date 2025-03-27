#!/bin/sh

NOW=$(date +"%m_%d_%Y_%H_%M_%S")

DTDIFFS="$NOW/Report_Daily_Transaction_diff_$NOW.log"
DTREP="$NOW/Report_Daily_Transaction_$NOW.log"
OUTPUTDIFFS="$NOW/Report_Output_diff_$NOW.log"
OUTPUTREP="$NOW/Report_Output_$NOW.log"
OUTPUTPASS=0
DTPASS=0
TOTAL=0

# Create directory to put the reports in
mkdir -p ../../../output
mkdir -p ../../../output/"$NOW"

echo "Daily Transaction Test Differences" > ../../../output/"$DTDIFFS"
echo "Daily Transaction Test Report" > ../../../output/"$DTREP"

echo "Output Test Differences" > ../../../output/"$OUTPUTDIFFS"
echo "Output Test Report" > ../../../output/"$OUTPUTREP"

# For each leaf directory in the tests subdirectory
for dir in ../../../tests/*/*/; do
	# Remove any existing daily_transaction_file.txt
	rm -f "$dir"daily_transaction_file.txt
	
	# Use custom current_user_accounts.txt and available_rental_units.txt file (if present)
	FILE1="$dir"current_user_accounts.txt
	FILE2="$dir"available_rental_units.txt
	
	# In the case where a custom file is not present, use standard input files located in tests directory
	if [ ! -f "$FILE1" ]; then
		FILE1="$dir"../../current_user_accounts.txt
	fi
	
	if [ ! -f "$FILE2" ]; then
		FILE2="$dir"../../available_rental_units.txt
	fi
	
	# Runs application with arguments and saves output
	java main.java.org.otbnb.Application "$FILE1" "$FILE2" < "$dir"input.txt "$dir"daily_transaction_file.txt > "$dir"output.txt
	
	# Compares Daily Transaction File with expected results and prints results to daily transaction diffs and report files
	DIFFDT=$(diff -q "$dir"daily_transaction_file.txt "$dir"exp_daily_transaction_file.txt)
	
	echo "$(basename "$dir")" >> ../../../output/"$DTDIFFS"
	diff "$dir"daily_transaction_file.txt "$dir"exp_daily_transaction_file.txt >> ../../../output/"$DTDIFFS"
	echo "---------------" >> ../../../output/"$DTDIFFS"
	
	if ["$DIFFDT" == ""]
	then
		echo "PASSED | $(basename "$dir")" >> ../../../output/"$DTREP"
		let DTPASS=DTPASS+1
	else
		echo "FAILED  | $(basename "$dir")" >> ../../../output/"$DTREP"
	fi
	
	# Compares Output File with expected results and prints results to output diffs and report files
	DIFFOUT=$(diff -q --suppress-common-lines "$dir"output.txt "$dir"exp_output.txt)

	echo "$(basename "$dir")" >> ../../../output/"$OUTPUTDIFFS"
	diff "$dir"output.txt "$dir"exp_output.txt >> ../../../output/"$OUTPUTDIFFS"
	echo "---------------" >> ../../../output/"$OUTPUTDIFFS"
	
	if ["$DIFFOUT" == ""]
	then
		echo "PASSED | $(basename "$dir")" >> ../../../output/"$OUTPUTREP"
		let OUTPUTPASS=OUTPUTPASS+1
	else
		echo "FAILED  | $(basename "$dir")" >> ../../../output/"$OUTPUTREP"
	fi
	
	echo "$(basename "$dir") Test Complete"
	let TOTAL=TOTAL+1
done

# Print summary statistics to report logs
echo "Total Tests: $TOTAL" >> ../../../output/"$DTREP"
echo "Tests Passed: $DTPASS" >> ../../../output/"$DTREP"

echo "Total Tests: $TOTAL" >> ../../../output/"$OUTPUTREP"
echo "Tests Passed: $OUTPUTPASS" >> ../../../output/"$OUTPUTREP"