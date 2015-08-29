package com.unisoft.algotrader.provider.ib.api.deserializer;

import com.unisoft.algotrader.provider.ib.api.IBSession;
import com.unisoft.algotrader.provider.ib.api.IncomingMessageId;

import java.io.InputStream;

import static com.unisoft.algotrader.provider.ib.api.InputStreamUtils.readInt;
import static com.unisoft.algotrader.provider.ib.api.InputStreamUtils.readString;

/**
 * Created by alex on 8/13/15.
 */
public class AccountSummaryEventDeserializer extends Deserializer {


    public AccountSummaryEventDeserializer(){
        super(IncomingMessageId.ACCOUNT_SUMMARY);
    }

    @Override
    public void consumeVersionLess(final int version, final InputStream inputStream,
                                   final IBSession ibSession) {
        final int reqId = readInt(inputStream);
        final String account = readString(inputStream);
        final String tag = readString(inputStream);
        final String value = readString(inputStream);
        final String currency = readString(inputStream);

        ibSession.onAccountSummary(reqId, account, tag, value, currency);
    }
}