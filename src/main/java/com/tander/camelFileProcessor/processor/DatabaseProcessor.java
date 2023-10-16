package com.tander.camelFileProcessor.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
public class DatabaseProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseProcessor.class);
    private static ArrayList<String> dataMessages = new ArrayList<>();

    private DataSource dataSource;
    private int batchSize;
    String sqlInsertTXTMessage;

    @Override
    public void process(Exchange exchange){
        dataMessages.add(exchange.getIn().getBody(String.class));
        logger.info("Message added to package.");
        if (dataMessages.size() > batchSize){
            save();
        }
    }

    public void save(){
        logger.info("Save to db...");
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try(PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertTXTMessage)) {
                for (String dataToSave : dataMessages) {
                    preparedStatement.setString(1, dataToSave);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                connection.commit();
                dataMessages.clear();
                logger.info("Save to db success.");
            }
            catch (SQLException e){
                connection.rollback();
                logger.error("Error while save to DB: ", e);
            }
        }
        catch (SQLException e){
            logger.error("Error while create connect to DB: ", e);
        }
    }
}