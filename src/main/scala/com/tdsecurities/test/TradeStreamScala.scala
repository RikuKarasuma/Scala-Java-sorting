package com.tdsecurities.test

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
object TradeStreamScala {

  /**
   * Extracts Swap trades from trades stream. Sorts them lexicographically first,
   * then sorts each Swap trade by verifying which contains contains "SELL" string.
   * Finally the Swap trades are merged with the unchanged Cash trades.
   *
   * @param tradesStream - Trade stream containing Cash and/or Swap trades.
   * @return Sorted Seq of Trades with Swap Trades merged on top of unchanged Cash
   *         trades.
   */
  def orderTrades(tradesStream: String*): Seq[String] =
  {
    // Literal function for determining Swap trades in grouping.
    val isSwapTrade = (trade:String) => trade.endsWith("SELL") || trade.endsWith("BUY")
    // Group by swap and cash trades.
    val swapAndCashTrades = tradesStream groupBy isSwapTrade

    val selectSwapTrades = true
    // Select Swap trades from grouped Map.
    val swapTrades = swapAndCashTrades(selectSwapTrades)
    // Select cash trades from grouped Map.
    val cashTrades = swapAndCashTrades(!selectSwapTrades)

    // Group by the Swap orderNumber.
    val groupedTrades = swapTrades
      .groupBy(_.split(" ")(0))
      // To list to get our sorting functionality.
      .toList
      // Sort groups by the Swap orderNumber
      .sortBy(_._1)

    // Sort the list within the tuple subsets by whether or not the Swap trade
    // ends with SELL string.
    val sortedSubsets = groupedTrades
      .map(_._2.sortWith( (swapOne, swapTwo) => {swapOne.endsWith("SELL")}))
      // Reduce lists into single list.
      .reduce(_ ++ _)

    // Merge Swap trades on top of Cash trades.
    sortedSubsets ++ cashTrades
  }
}
