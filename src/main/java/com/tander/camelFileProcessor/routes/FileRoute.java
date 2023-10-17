package com.tander.camelFileProcessor.routes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
@AllArgsConstructor
public class FileRoute extends RouteBuilder {
    private static final Logger logger = LoggerFactory.getLogger(RouteBuilder.class);

    private String destinationForXML;
    private String destinationForTXT;
    private String destinationForERROR;
    private String pathToFileDirectory;

    @Override
    public void configure() {
        from("file:" + pathToFileDirectory + "?noop=true&recursive=true")
                .routeId("file_to_brokerDB")
                .doTry()
                    .choice()
                        .when(header("CamelFileName").endsWith(".xml"))
                            .to(destinationForXML)
                        .when(header("CamelFileName").endsWith(".txt"))
                            .to(destinationForTXT)
                            .process("DatabaseProcessor")
                        .otherwise()
                            .to(destinationForERROR)
                            .throwException(new IllegalArgumentException("Invalid file extension"))
                    .endChoice()
                .endDoTry()
                .doCatch(IllegalArgumentException.class)
                    .process(exchange -> {
                        logger.error("Invalid file extension.");
                    })
                .doFinally()
                    .process("messageProcessor")
                .end();
    }
}
