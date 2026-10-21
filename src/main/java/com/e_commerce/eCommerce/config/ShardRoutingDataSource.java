package com.e_commerce.eCommerce.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ShardRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log =
            LoggerFactory.getLogger(ShardRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {

        Integer shardId = 1;

        if (shardId == null) {
            log.info("Shard ID is not set");
            throw new IllegalStateException("Shard ID is not set");
        }

        boolean readOnly =
                TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (shardId == 1) {
            log.info("Shard ID: {} | Routing to DB1 Primary", shardId);
            return DbRoute.SHARD1_PRIMARY;
        }

        if (shardId == 2) {
            if (readOnly) {
                log.info("Shard ID: {} | Routing to DB2 Replica", shardId);
                return DbRoute.SHARD2_REPLICA;
            }

            log.info("Shard ID: {} | Routing to DB2 Primary", shardId);
            return DbRoute.SHARD2_PRIMARY;
        }

        log.error("Invalid shard ID: {}", shardId);
        throw new IllegalStateException("Invalid shard ID: " + shardId);
    }
}