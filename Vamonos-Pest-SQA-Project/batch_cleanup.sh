#!/bin/sh

for d in ../../../tests/*/*/; do
	dos2unix.exe "$d"exp_output.txt

    echo "$(basename "$d") Complete"
done