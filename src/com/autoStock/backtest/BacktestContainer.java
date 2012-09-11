package com.autoStock.backtest;

import java.util.ArrayList;

import com.autoStock.algorithm.AlgorithmTest;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.algorithm.core.AlgorithmManagerTable;
import com.autoStock.algorithm.reciever.ReceiverOfQuoteSlice;
import com.autoStock.backtest.BacktestDefinitions.BacktestType;
import com.autoStock.generated.basicDefinitions.TableDefinitions.DbStockHistoricalPrice;
import com.autoStock.tables.TableController;
import com.autoStock.tables.TableDefinitions.AsciiTables;
import com.autoStock.trading.types.HistoricalData;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class BacktestContainer implements ReceiverOfQuoteSlice {
	public Symbol symbol;
	private Exchange exchange;
	private HistoricalData historicalData;
	public AlgorithmTest algorithm;
	private ArrayList<DbStockHistoricalPrice> listOfDbHistoricalPrices = new ArrayList<DbStockHistoricalPrice>();
	private ListenerOfBacktestCompleted listener; 
	private Backtest backtest;
	private AlgorithmMode algorithmMode;
	private AlgorithmManagerTable algorithmManagerTable = new AlgorithmManagerTable();
	
	private int tempCountReceived = 0;
	
	public BacktestContainer(Symbol symbol, Exchange exchange, ListenerOfBacktestCompleted listener, AlgorithmMode algorithmMode){
		this.symbol = symbol;
		this.exchange = exchange;
		this.listener = listener;
		this.algorithmMode = algorithmMode;
	}
	
	public void setBacktestData(ArrayList<DbStockHistoricalPrice> listOfDbStockHistoricalPrice, HistoricalData historicalData){
		this.listOfDbHistoricalPrices = listOfDbStockHistoricalPrice;
		this.historicalData = historicalData;
		algorithm = new AlgorithmTest(false, exchange, symbol, algorithmMode);
	}
	
	public synchronized void runBacktest(){
		backtest = new Backtest(historicalData, listOfDbHistoricalPrices, symbol);
		backtest.performBacktest(this);
	}
	
	@Override
	public synchronized void receiveQuoteSlice(QuoteSlice quoteSlice) {
		
//		tempCountReceived++;
//		
//		if (tempCountReceived >= 30){
//			algorithmManagerTable.addRow(algorithm, algorithm.listOfQuoteSlice);
//			new TableController().displayTable(AsciiTables.algorithm_manager, algorithmManagerTable.getListOfDisplayRows());
//			algorithmManagerTable.clear();
//			tempCountReceived = 0;
//		}
		
		algorithm.receiveQuoteSlice(quoteSlice);
	}
	
	@Override
	public synchronized void endOfFeed(Symbol symbol) {
		algorithm.endOfFeed(symbol);
		listener.backtestCompleted(symbol);
	}
}
