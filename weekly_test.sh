#!/bin/sh

# Main directory for daily tests
DIR="../../../daily_tests/"

# Day counter
DAY=1
DAYNEXT=2

# Loops through inputs for each day
for f in "$DIR"inputs/*; do	
	echo "Day # $DAY"

	# Runs application and backend with inputs depending on the Day
	# Day 1 required initial inputs
	if [ "$DAY" -eq "1" ]
	then
		echo "$DIR"day1
		
		mkdir "$DIR"day1
		
		echo ""
		echo "Running Application"
		
		#										initial users					 initial units					      input		         transaction
		java main.java.org.otbnb.Application "$DIR"current_user_accounts.txt "$DIR"available_rental_units.txt < "$f" "$DIR"day1/day1transaction.txt 
		
		mkdir "$DIR"day"$DAYNEXT"
		
		echo ""
		echo "Running Backend"

		#								   initial users					 initial units						transaction				 next day users							   next day rentals
		java main.java.org.otbnb.BackEnd "$DIR"current_user_accounts.txt "$DIR"available_rental_units.txt "$DIR"day1/day1transaction.txt "$DIR"day"$DAYNEXT"/day"$DAYNEXT"users.txt "$DIR"day"$DAYNEXT"/day"$DAYNEXT"rentals.txt
	else
		
		echo ""
		echo "Running Application"
		#										previous users		  previous units				input				  transaction
		java main.java.org.otbnb.Application "$DIR"day"$DAY"/day"$DAY"users.txt "$DIR"day"$DAY"/day"$DAY"rentals.txt < "$f" "$DIR"day"$DAY"/day"$DAY"transaction.txt
		
		mkdir "$DIR"day"$DAYNEXT"
		
		echo ""
		echo "Running Backend"
		#								   previous users		 previous units			 transaction				 next day users							  next day rentals
		java main.java.org.otbnb.BackEnd "$DIR"day"$DAY"/day"$DAY"users.txt "$DIR"day"$DAY"/day"$DAY"rentals.txt "$DIR"day"$DAY"/day"$DAY"transaction.txt "$DIR"day"$DAYNEXT"/day"$DAYNEXT"users.txt "$DIR"day"$DAYNEXT"/day"$DAYNEXT"rentals.txt
	fi
	
	echo "----------------------------------------------------------"
	echo ""
	
	# Counts days
	let DAY=DAY+1
	let DAYNEXT=DAYNEXT+1
done
