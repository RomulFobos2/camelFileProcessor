package com.tander.camelFileProcessor.processor;

import com.tander.camelFileProcessor.service.MailService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
@AllArgsConstructor
public class MessageProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessor.class);

    private MailService mailService;
    private int countFilesForSend;

    private static int current_ProcessedFile;
    private static int count_XMLFile;
    private static int count_TXTFile;
    private static int count_UNKNOWFile;
    private static long startTime;

    static {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Prepare info about batch");
        current_ProcessedFile++;
        String fileName = exchange.getIn().getHeader("CamelFileName", String.class);
        String fileExtension = "";
        if (fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        switch (fileExtension){
            case "xml" : {
                count_XMLFile++;
                break;
            }
            case "txt" : {
                count_TXTFile++;
                break;
            }
            default : {
                count_UNKNOWFile++;
            }
        }
        if (current_ProcessedFile >= countFilesForSend){
            long work_time = (startTime - System.currentTimeMillis())/1000;
            String textLetter = mailService.createLetter(current_ProcessedFile, count_XMLFile, count_TXTFile, count_UNKNOWFile, work_time);
            mailService.send(textLetter);
        }
    }
}
