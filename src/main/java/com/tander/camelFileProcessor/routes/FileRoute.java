package com.tander.camelFileProcessor.routes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;

import javax.jms.DeliveryMode;

@Getter
@Setter
@AllArgsConstructor
public class FileRoute extends RouteBuilder {
    private String destinationForXML;
    private String destinationForTXT;
    private String destinationForERROR;
    private String pathToFileDirectory;

    @Override
    public void configure() {
        from("file:" + pathToFileDirectory + "?noop=true&recursive=true")
                .routeId("file_to_brokerDB")
                .choice()
                .when(header("CamelFileName").endsWith(".xml"))
                .to(destinationForXML)
                .when(header("CamelFileName").endsWith(".txt"))
                .to(destinationForTXT)
                .process("DatabaseProcessor")
                .otherwise()
                //.throwException(new IllegalArgumentException("Invalid file extension"))
                .to(destinationForERROR)
                .end()
                .process("messageProcessor");
    }
}
