package com.oanda.v20;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;

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
}