package com.oanda.v20;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.InstrumentName;
import static com.oanda.v20.instrument.CandlestickGranularity.H1;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    public String aktualisarak() {
        StringBuilder pricesString = new StringBuilder();
        try {
            Context ctx = new ContextBuilder("https://api-fxpractice.oanda.com")
                    .setToken("0d5dc4b6290d7e4c79231934a2515051-1c59ba0673c3875fb4d43ae855b70d4b")
                    .setApplication("PricePolling")
                    .build();

            AccountID accountId = new AccountID("101-004-30186452-001");
            List<String> instruments = new ArrayList<>(Arrays.asList("EUR_USD", "USD_JPY", "GBP_USD", "USD_CHF"));

            PricingGetRequest request = new PricingGetRequest(accountId, instruments);
            PricingGetResponse resp = ctx.pricing.get(request);

            for (ClientPrice price : resp.getPrices()) {
                pricesString.append(price.toString()).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        return pricesString.toString();
    }
    public static List<Candlestick> getCandlestickData(String instrument, CandlestickGranularity granularity, LocalDate startDate, LocalDate endDate) {
        List<Candlestick> candleDataList = new ArrayList<>();
        try {
            Context ctx = new ContextBuilder("https://api-fxpractice.oanda.com")
                    .setToken("0d5dc4b6290d7e4c79231934a2515051-1c59ba0673c3875fb4d43ae855b70d4b")
                    .setApplication("HistorikusAdatok")
                    .build();

            InstrumentCandlesRequest request = new InstrumentCandlesRequest(new InstrumentName(instrument));
            request.setGranularity(granularity);
            request.setCount(10L);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            request.setFrom(dateFormat.format(java.sql.Date.valueOf(startDate)));
            request.setTo(dateFormat.format(java.sql.Date.valueOf(endDate)));

            InstrumentCandlesResponse resp = ctx.instrument.candles(request);
            candleDataList.addAll(resp.getCandles());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return candleDataList;
    }
    public static String Nyitás(String instrumentCode, Integer units, boolean isBuy) {
        String message = "Hiba történt a pozíció nyitása során.";
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
}