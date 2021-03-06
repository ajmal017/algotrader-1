package com.unisoft.algotrader.provider.ib.api.event;

import com.unisoft.algotrader.provider.ib.api.model.fa.FinancialAdvisorDataType;

/**
 * Created by alex on 8/26/15.
 */
public class FinancialAdvisorConfigurationEvent extends IBEvent<FinancialAdvisorConfigurationEvent>  {

    public final FinancialAdvisorDataType dataTypeValue;
    public final String xml;
    public FinancialAdvisorConfigurationEvent(final FinancialAdvisorDataType dataTypeValue, final String xml){
        this.dataTypeValue = dataTypeValue;
        this.xml = xml;
    }

    @Override
    public void on(IBEventHandler handler) {
        handler.onFinancialAdvisorConfigurationEvent(this);
    }

    @Override
    public String toString() {
        return "FinancialAdvisorConfigurationEvent{" +
                "dataTypeValue=" + dataTypeValue +
                ", xml='" + xml + '\'' +
                "} " + super.toString();
    }
}