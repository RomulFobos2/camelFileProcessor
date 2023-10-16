package com.tander.camelFileProcessor;

import com.tander.camelFileProcessor.processor.DatabaseProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class CamelFileProcessorApplication {

	public static void main(String[] args) throws InterruptedException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		//DatabaseProcessor databaseProcessor = context.getBean(DatabaseProcessor.class);
		Thread.sleep(15000);
		context.close();
	}
}
