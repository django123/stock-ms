package liss.nvms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class LissAPP implements CommandLineRunner {
	
	public static void main (String[] args) {       
		//ConfigurableApplicationContext context = 
				SpringApplication.run(LissAPP.class, args);       
    }

	public void run(String... args) throws Exception {
		System.err.println("Application run ...");
		
	}
	
	 
}