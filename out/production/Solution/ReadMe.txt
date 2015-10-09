How To Run:
1. Extract Project.java and the jsoup module.
2. Open Terminal.
3. Type the following commands to run(Change version of jsoup as necessary):
	javac -classpath ./jsoup-1.8.3.jar Project.java 
	java -classpath .:jsoup-1.8.3.jar Project
4. The output text file will be in the same directory.

Notea:
1. To query another API, change the url in line 16.
2. Change the output file name as required in line 35.
3. This program queries the API and extracts the raw text of the the first 50 posts in that API.
