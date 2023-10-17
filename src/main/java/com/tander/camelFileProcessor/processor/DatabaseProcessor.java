package com.tander.camelFileProcessor.processor;

import bitronix.tm.BitronixTransactionManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.transaction.*;
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

    BitronixTransactionManager transactionManager;

    @Override
    public void process(Exchange exchange){
        dataMessages.add(exchange.getIn().getBody(String.class));
        logger.info("Message added to package.");
        if (dataMessages.size() > batchSize){
            saveToDB();
        }
    }

    public void saveToDB(){
        logger.info("Save to db...");
        try {
            transactionManager.begin();
            try (Connection connection = dataSource.getConnection()) {
                try(PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertTXTMessage)) {
                    for (String dataToSave : dataMessages) {
                        preparedStatement.setString(1, dataToSave);
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                    transactionManager.commit();
                    dataMessages.clear();
                    logger.info("Save to db success.");
                }
            }
        }
        catch (SQLException e){
            try {
                transactionManager.rollback();
            }
            catch (SystemException se){
                logger.error("Error rolling back transaction: ", e);
            }
            logger.error("Error while save to DB: ", e);
        }
        catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException |
               HeuristicRollbackException e) {
            logger.error("Error while managing transaction: ", e);
        }
    }
}