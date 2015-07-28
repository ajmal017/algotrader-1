package com.unisoft.algotrader.provider.ib;

import ch.aonyx.broker.ib.api.SubscriptionRequest;
import ch.aonyx.broker.ib.api.UnsubscriptionRequest;
import ch.aonyx.broker.ib.api.contract.Contract;
import ch.aonyx.broker.ib.api.contract.SecurityType;
import ch.aonyx.broker.ib.api.data.bar.RealTimeBarDataType;
import ch.aonyx.broker.ib.api.data.bar.RealTimeBarSubscriptionRequest;
import ch.aonyx.broker.ib.api.data.bar.RealTimeBarUnsubscriptionRequest;
import ch.aonyx.broker.ib.api.data.historical.*;
import ch.aonyx.broker.ib.api.util.StringIdUtils;
import com.unisoft.algotrader.model.event.data.DataType;
import com.unisoft.algotrader.model.refdata.Instrument;
import com.unisoft.algotrader.provider.data.HistoricalSubscriptionKey;
import com.unisoft.algotrader.provider.data.SubscriptionKey;
import zmq.Sub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * Created by alex on 7/27/15.
 */
public class IBUtils {

    public static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    public static Contract getForexContract(final String symbol) {
        final Contract contract = new Contract();
        contract.setCurrencyCode("USD");
        contract.setExchange("IDEALPRO");
        contract.setSecurityType(SecurityType.FOREX);
        contract.setSymbol(symbol);
        return contract;
    }


    public static Contract getContract(SecurityType type, String symbol, String exchange, String currency){
        final Contract contract = new Contract();
        contract.setCurrencyCode(currency);
        contract.setExchange(exchange);
        contract.setSecurityType(type);
        contract.setSymbol(symbol);
        return contract;
    }

    public static Contract getContract(Instrument instrument){
        return getContract(
                convertSecurityType(instrument.getType()),
                instrument.getSymbol(IBProvider.PROVIDER_ID),
                instrument.getExchId(IBProvider.PROVIDER_ID),
                instrument.getCcyId());
    }

    public static RealTimeBarSubscriptionRequest createRealTimeBarSubscriptionRequest(SubscriptionKey subscriptionKey, Instrument instrument){
        Contract contract = IBUtils.getContract(instrument);
        return new RealTimeBarSubscriptionRequest(contract, 5, contract.getSecurityType() == SecurityType.FOREX ? RealTimeBarDataType.MID_POINT : RealTimeBarDataType.TRADES, false);
    }

    public static RealTimeBarUnsubscriptionRequest createRealTimeBarUnsubscriptionRequest(Instrument instrument){
        Contract contract = IBUtils.getContract(instrument);
        String subId = StringIdUtils.uniqueIdFromContract(contract);
        return new RealTimeBarUnsubscriptionRequest(subId);
    }

    public static HistoricalDataSubscriptionRequest createHistoricalDataSubscriptionRequest(HistoricalSubscriptionKey subscriptionKey, Instrument instrument){
        Contract contract = IBUtils.getContract(instrument);
        HistoricalDataSubscriptionRequest request = new HistoricalDataSubscriptionRequest(contract,
                IBUtils.convertDate(subscriptionKey.toDate),
                IBUtils.calculateTimeSpan(subscriptionKey.fromDate, subscriptionKey.toDate),
                IBUtils.convertFreq(subscriptionKey.barSize),
                HistoricalDataType.TRADES,
                false,
                DateFormat.YYYYMMDD__HH_MM_SS
        );
        return request;
    }

    public static HistoricalDataType convertHistoricalDataType(DataType dataType){
        if (dataType == DataType.Quote){
            return HistoricalDataType.BID_ASK;
        }
        if (dataType == DataType.Trade){
            return HistoricalDataType.TRADES;
        }
        return HistoricalDataType.MID_POINT;
    }

    public static SecurityType convertSecurityType(Instrument.InstType instType){
        switch (instType){
            case ETF:
                return SecurityType.STOCK;
            case Future:
                return SecurityType.FUTURE;
            case FX:
                return SecurityType.FOREX;
            case Index:
                return SecurityType.INDEX;
            case Option:
                return SecurityType.OPTION;
            case Stock:
                return SecurityType.STOCK;
            default:
                throw new IllegalArgumentException("unsupported instType:" + instType);
        }
    }

    public static final int SEC_1 = 1;
    public static final int SEC_5 = 5;
    public static final int SEC_15 = 15;
    public static final int SEC_30 = 30;
    public static final int MIN_1 = 60;
    public static final int MIN_2 = 60 * 2;
    public static final int MIN_3 = 60 * 3;
    public static final int MIN_5 = 60 * 5;
    public static final int MIN_15 = 60 * 15;
    public static final int MIN_30 = 60 * 30;
    public static final int HOUR_1 = 60 * 60;
    public static final int DAY_1 = 60 * 60 * 24;


    public static BarSize convertFreq(int freq){
        if(freq <= SEC_1)
            return BarSize.ONE_SECOND;
        else if (freq <= SEC_5)
            return BarSize.FIVE_SECONDS;
        else if (freq <= SEC_15)
            return BarSize.FIFTEEN_SECONDS;
        else if (freq <= SEC_30)
            return BarSize.THIRTY_SECONDS;
        else if (freq <= MIN_1)
            return BarSize.ONE_MINUTE;
        else if (freq <= MIN_2)
            return BarSize.TWO_MINUTES;
        else if (freq <= MIN_3)
            return BarSize.THIRTY_MINUTES;
        else if (freq <= MIN_5)
            return BarSize.FIFTEEN_MINUTES;
        else if (freq <= MIN_15)
            return BarSize.FIFTEEN_MINUTES;
        else if (freq <= MIN_30)
            return BarSize.THIRTY_MINUTES;
        else if (freq <= HOUR_1)
            return BarSize.ONE_HOUR;
        return BarSize.ONE_DAY;
    }

    public static TimeSpan calculateTimeSpan(long fromDate, long toDate){
        long diff = toDate - fromDate;

        Duration duration = Duration.of(diff, ChronoUnit.SECONDS);
        int diffDays = (int)duration.toDays();
        Period p = Period.ofDays(diffDays);
        int years = p.getYears();
        int months = p.getMonths();
        int days = p.getDays();

        if (years > 0){
            return new TimeSpan(years, TimeSpanUnit.YEAR);
        }
        if (months > 0){
            return new TimeSpan(months, TimeSpanUnit.MONTH);
        }
        if (months > 0){
            return new TimeSpan(days, TimeSpanUnit.DAY);
        }
        return new TimeSpan((int)diff, TimeSpanUnit.SECOND);
    }

    public static long convertDate(String date){
        try {
            return FORMAT.parse(date).getTime();
        }
        catch (ParseException e){
            throw new RuntimeException("fail to parse date:"+date, e);
        }
    }

    public static String convertDate(long date){
        return FORMAT.format(new Date(date));
    }
}
