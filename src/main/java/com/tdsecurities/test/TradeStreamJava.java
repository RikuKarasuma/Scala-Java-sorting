package com.tdsecurities.test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

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

    /**
     * Separates out the two types of Trades into groups. Sorts the Swap trades
     * lexicographically. Then reorganizes the Swap trade pairs so that SELL comes
     * first. Finally the sorted Swap trades are merged with Cash trades and returned
     * for processing.
     *
     * @param tradesStream - Iterator of Cash and Swap trades.
     * @return Sorted ArrayList of Trades with Swap Trades merged on top of unchanged Cash
     *      trades.
     */
    static List<String> orderTrades(Iterator<String> tradesStream)
    {
        // Used to seperate out our two types of trade.
        final ArrayList<String> swapTrades = new ArrayList<String>(),
                        cashTrades = new ArrayList<String>();

        // Seperate out the two types of trade.
        while(tradesStream.hasNext()) {
            final String trade = tradesStream.next();
            if (isSwapTrade(trade))
                swapTrades.add(trade);
            else
                cashTrades.add(trade);
        }

        // Sort our Swap trade lexicographically
        final Object[] lexicoSorted = swapTrades.stream()
                .sorted().toArray();

        // Merged group for both lists to be reconciled.
        final ArrayList<String> mergedGroups = new ArrayList<>(swapTrades.size()/2);

        // Our lexicographical sort makes BUY come first. So re-organize and
        // place our SELL Swap trade to our list first.
        for (int i = 0; i < swapTrades.size(); i += 2)
        {
            // Placing it first here.
            mergedGroups.add((String)lexicoSorted[i + 1]);
            // Buying placed after.
            mergedGroups.add((String)lexicoSorted[i]);
        }

        // Merge in the cash trades.
        mergedGroups.addAll(cashTrades);

        // Return merged groups.
        return mergedGroups;
    }

    static boolean isSwapTrade(String trade)
    {
        return trade.endsWith("BUY") || trade.endsWith("SELL");
    }
}
