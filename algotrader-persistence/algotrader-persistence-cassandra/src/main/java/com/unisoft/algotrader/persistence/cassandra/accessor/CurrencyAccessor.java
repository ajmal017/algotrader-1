package com.unisoft.algotrader.persistence.cassandra.accessor;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;
import com.unisoft.algotrader.model.refdata.Currency;

/**
 * Created by alex on 7/7/15.
 */
@Accessor
public interface CurrencyAccessor {
    @Query("SELECT * FROM refdata.currencies WHERE ccy_id = :ccy_id")
    Currency get(@Param("ccy_id") String ccy_id);

    @Query("SELECT * FROM refdata.currencies")
    Result<Currency> getAll();

    @Query("SELECT * FROM refdata.currencies")
    ListenableFuture<Result<Currency>> getAllAsync();
}
