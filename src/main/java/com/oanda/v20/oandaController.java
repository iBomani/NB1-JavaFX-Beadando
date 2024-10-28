package com.oanda.v20;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class oandaController {

    public AccountSummary skibidi(){
        Context ctx = new Context("https://api-fxpractice.oanda.com", "c98e58a6077aab45fe95dd3a884c56ee-e2cd6c83e80c34050cde1d86b4ed7a2f");
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

}