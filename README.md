# ParallelProject3

## Temperature Reading Report
In the program all 8 threads have the power to "record temperatures" and add them to a list </br>
The list takes all recorded temperatures in which it assumes that every input is one minute apart </br>
This is not the case in the program because that would simply take too long. </br>
Instead, the program generates 60 randomized temperatures to simulate one temperature per minute for an hour. </br>
With this choice the program is able to run very quickly, taking an average of 3 seconds on my computer. </br>
The program uses a Reentrant Lock in order to guarantee the safety of the list of recorded temperatures </br>
This Reentrant Lock allows only one thread to access and make changes to the list at a time </br>
This removes the possibility of corrupted or inconsistent data.

## Installation Process
1. Download the three java files in the repo
2. Navigate to a directory including all three files
3. type `javac Main.java` and press enter
4. type `java Main` and press enter
5. Details will be found in the "giftThankYous.txt" and "tempReport.txt" files