package com.oanda.v20;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.Instrument;
import com.oanda.v20.primitives.InstrumentName;

import static com.oanda.v20.instrument.CandlestickGranularity.H1;
import com.oanda.v20.trade.TradeCloseRequest;
import com.oanda.v20.trade.TradeSpecifier;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static com.oanda.v20.instrument.CandlestickGranularity.H1;

public class oandaController {

    public AccountSummary skibidi(){
        Context ctx = new Context("https://api-fxpractice.oanda.com", "0d5dc4b6290d7e4c79231934a2515051-1c59ba0673c3875fb4d43ae855b70d4b");
        try {
            AccountSummary summary = ctx.account.summary(new
                    AccountID("101-004-30186452-001")).getAccount();
            return summary;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public PricingGetResponse aktualisarak(String selectedCurrency) {
        StringBuilder pricesString = new StringBuilder();
        PricingGetResponse resp = null;
        try {
            Context ctx = new ContextBuilder("https://api-fxpractice.oanda.com")
                    .setToken("0d5dc4b6290d7e4c79231934a2515051-1c59ba0673c3875fb4d43ae855b70d4b")
                    .setApplication("PricePolling")
                    .build();

            AccountID accountId = new AccountID("101-004-30186452-001");
            List<String> instruments = Collections.singletonList(selectedCurrency);

            PricingGetRequest request = new PricingGetRequest(accountId, instruments);
            resp = ctx.pricing.get(request);

            for (ClientPrice price : resp.getPrices()) {
                pricesString.append(price.toString()).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resp;
    }

    public InstrumentCandlesResponse historikusArak(String instrument, String fromDate, String toDate) {
        try {
            Context ctx = new ContextBuilder("https://api-fxpractice.oanda.com")
                    .setToken("0d5dc4b6290d7e4c79231934a2515051-1c59ba0673c3875fb4d43ae855b70d4b")
                    .setApplication("PricePolling")
                    .build();
            InstrumentCandlesRequest request = new InstrumentCandlesRequest(new InstrumentName(instrument));
            request.setGranularity(H1);
            request.setFrom(fromDate);
            request.setTo(toDate);

            InstrumentCandlesResponse resp = ctx.instrument.candles(request);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String Nyitás(String instrumentCode, Integer units, boolean isBuy) {
        String message = "Hiba történt a pozíció nyitása során.:(";
        try {

            Context ctx = new ContextBuilder("https://api-fxpractice.oanda.com")
                    .setToken("0d5dc4b6290d7e4c79231934a2515051-1c59ba0673c3875fb4d43ae855b70d4b")
                    .setApplication("StepByStepOrder")
                    .build();
            AccountID accountId = new AccountID("101-004-30186452-001");

            OrderCreateRequest request = new OrderCreateRequest(accountId);
            MarketOrderRequest marketOrderRequest = new MarketOrderRequest();
            marketOrderRequest.setInstrument(new InstrumentName(instrumentCode));
            marketOrderRequest.setUnits(isBuy ? units : -units); //
            request.setOrder(marketOrderRequest);

            OrderCreateResponse response = ctx.order.create(request);
            String tradeId = String.valueOf(response.getOrderFillTransaction().getId());
            message = "Sikeresen nyitott pozíció! Trade ID: " + tradeId; //
        } catch (Exception e) {
            e.printStackTrace();
            message = "Hiba: " + e.getMessage();
        }
        return message;
    }
    public static String Zaras(String tradeId) {
        String resultMessage = "Zárás folyamatban...";
        try {
            Context ctx = new
                    ContextBuilder("https://api-fxpractice.oanda.com").setToken("0d5dc4b6290d7e4c79231934a2515051-1c59ba0673c3875fb4d43ae855b70d4b").setApplication("StepByStepOrder").build();

            AccountID accountId = new AccountID("101-004-30186452-001");

            ctx.trade.close(new TradeCloseRequest(accountId, new TradeSpecifier(tradeId)));
            resultMessage = "Trade ID: " + tradeId + " sikeresen lezárva.";
        } catch (Exception e) {
            e.printStackTrace();
            resultMessage = "Hiba: " + e.getMessage();
        }
        return resultMessage;
    }
}

