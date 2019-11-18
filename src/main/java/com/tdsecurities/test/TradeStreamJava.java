package com.tdsecurities.test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;
import java.util.List;

/**
 * The system is event based so trades arrive at this consumer out of order.
 * To fix this issue, tradesStream must be sorted with the following rules:
 *
 * * SWAP trades are represented by two strings (i.e. 123CB SELL or 9898AA BUY), orderNumber and buySell respectively.
 *   - order numbers must be sorted lexicographically (i.e 11 before 11C)
 *   - Sells (SELL) must be processed before Buys (BUY) by stacking SELL/BUY pairs with the same orderNumber
 *
 * * CASH trades are represented by two numbers (i.e. 984756 34566), code and serial respectively.
 *   CASH trades must be processed in order of appearance, after all SWAPs are processed
 *
 * Please take a look at the test class for some examples
 */
class TradeStreamJava {

    static List<String> orderTrades(Iterator<String> tradesStream) {
        throw new NotImplementedException();
    }
}
