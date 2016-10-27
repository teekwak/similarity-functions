#!/bin/bash

# email when completed

echo
echo "===== Running email.sh ====="
echo

while [ 1 ];
do
	count=0

	# find number of jar instances (1 means not running)
	IFS=$'\n'
	for line in $(ps ax | grep similarityfunctions)
	do
		(( count++ ))
	done

	# if there is only one command (which would be the ps command) send me an email
	if [[ $count -lt 2 ]]; 
		then
			echo "Hello! We are confirming from $(hostname) that the querying process is complete!" | mail -s "Querying is complete!" teekwak@gmail.com
			break
	fi

	sleep 300s;
done

echo 
echo "===== Process completed. Exiting gracefully... ====="
echo
