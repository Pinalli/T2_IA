import java.io.FileNotFoundException;
import java.lang.StackTraceElement;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.FileWriter;
import java.util.Scanner;
import java.lang.String;
import java.util.Arrays;
import java.io.File;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.Duration;

public class Logger {
	private static PrintWriter logDataWriter;
	public static boolean runningFile;
	private static Path logDateTime;
	private static Logger instance;

	private static DateTimeFormatter time;
	private static LocalDateTime beginProg;
	public static LocalDateTime now;

	private Logger() {
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH'H'.mm'M'.ss'S'");
		time = DateTimeFormatter.ofPattern("HH:mm:ss:SSS' - '"); 
        
        String filePath = Paths.get("").toAbsolutePath().toString();
        now = LocalDateTime.now();
		beginProg = now;

        filePath += "\\SavedLogs\\" + "LoggerRuntime_" + dt.format(now) + ".txt";   
        
        logDateTime =  Paths.get(filePath);
		String welcome = "";

		try{
			File logFile = new File(logDateTime.toString());

			if(!logFile.exists()){
            	logFile.createNewFile();

				welcome = "Logger file has been created on " +
						  logDateTime.subpath((logDateTime.getNameCount()-3), (logDateTime.getNameCount()-1)) + 
					 	  ",\nat file " +
	  					  logDateTime.getFileName() + ".";
			
		
				logDataWriter = new PrintWriter(new FileWriter(logFile, true));
		        logDataWriter.println(welcome);

				StringBuilder logEnviroment = new StringBuilder();
				
				logEnviroment
	   				.append("\nProcessor: ").append(System.getenv("PROCESSOR_IDENTIFIER")).append(". ")
					.append("\nComputer: ")	.append(System.getenv("COMPUTERNAME"))		  .append(", ")
					.append("User: ")		.append(System.getenv("USERNAME"))			  .append(".")
					.append("\nSystem: ")	.append(System.getenv("OS"))				  .append(", ")
											.append(System.getenv("NUMBER_OF_PROCESSORS")).append(" Core(s),")
					.append(" v. ")			.append(System.getenv("PROCESSOR_REVISION"))  .append(".\n");

				//logDataWriter.append(logEnviroment);
		        logDataWriter.close();
        	}

        }catch(IOException x){
             System.err.format("Erro de E/S: %s%n", x);
        }
        runningFile = false;	
	}
	
	public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }


	public void exception(Thread t, Throwable e) {
		StackTraceElement[] stack = e.getStackTrace();
		String exception = "THROWN EXCEPTION\n" + e.getClass().getCanonicalName() + "\n";
		String message = e.getLocalizedMessage();

		if(message == null)
			message = "";
		else
			message += "\n";

		exception += "Thread "+ t.getId() +", " + t.getState() + "\n";
		exception += message + stack[0].toString() + "\n" + stack[stack.length-1].toString();

		publishLog(exception);
	}

	public void publishLog(int node) {
		publishLog(Integer.toString(node));
	}

	public void publishLog(int[] chromosome, int generation, int gene, int hits) {
		publishLog(Arrays.toString(chromosome) + " -> Generation: " + generation + " -gene: " + gene + " hits: " + hits + "\n");
	}
	
	public void publishLog(String data) {
		if(data == null)
			return;

		try{
			File logFile = new File(logDateTime.toString());

			if(!logFile.exists()) {
            	logFile.createNewFile();
			}
		
			logDataWriter = new PrintWriter(new FileWriter(logFile, true));
        
        }catch(IOException x){
             System.err.format("Erro de E/S: %s%n", x);
        }

		data = data.replaceAll("\n", "\n               ");
		logDataWriter.append("\n" + time.format(LocalDateTime.now()) + data);
        logDataWriter.close();
	}

	public void close() {
		StringBuilder str = new StringBuilder();
			now = LocalDateTime.now();
			Duration duration = Duration.between(beginProg, now);

			str.append(" - Runned on ")
			   .append(duration.toHours()%60)
			   .append(" hours, ")
			   .append(duration.toMinutes()%60)
			   .append(" Minutes, ")
			   .append(duration.toSeconds()%60)
			   .append(" Seconds, ")
			   .append(duration.toMillis()%1000)
			   .append(" Milli.\n")
			   .append("\n");
			   
			runningFile = false;
		publishLog(str.toString());
	}

	public void initFile(String filename) {
		beginProg = LocalDateTime.now();
		runningFile = true;
		publishLog("Running on file " + filename);
	}
}