/**
 * 
 */
package com.autoStock.exchange.request;

import java.util.Date;

import com.autoStock.Co;
import com.autoStock.exchange.ExchangeController;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestHistoricalDataListener;
import com.autoStock.exchange.request.listener.RequestMarketDataListener;
import com.autoStock.exchange.results.ExResultHistoricalData;
import com.autoStock.exchange.results.ExResultMarketData;
import com.autoStock.exchange.results.ExResultHistoricalData.ExResultRowHistoricalData;
import com.autoStock.exchange.results.ExResultHistoricalData.ExResultSetHistoricalData;
import com.autoStock.exchange.results.ExResultMarketData.ExResultRowMarketData;
import com.autoStock.exchange.results.ExResultMarketData.ExResultSetMarketData;
import com.autoStock.exchange.results.ResultQuoteSlice;
import com.autoStock.tools.QuoteSliceTools;
import com.autoStock.trading.platform.ib.definitions.MarketData.TickTypes;
import com.autoStock.trading.types.TypeHistoricalData;
import com.autoStock.trading.types.TypeMarketData;

/**
 * @author Kevin Kowalewski
 * 
 */
public class RequestMarketData {
	public RequestHolder requestHolder;
	public RequestMarketDataListener requestMarketDataListener;
	public ExResultSetMarketData exResultSetMarketData;
	public TypeMarketData typeMarketData;
	private Thread threadForSliceCollector;
	private int sliceMilliseconds;
	private long receivedTimestamp = 0;
	private Date sliceDate;

	public RequestMarketData(RequestHolder requestHolder, RequestMarketDataListener requestMarketDataListener, TypeMarketData typeMarketData, int sliceMilliseconds) {
		this.requestHolder = requestHolder;
		this.requestHolder.caller = this;
		this.requestMarketDataListener = requestMarketDataListener;
		this.typeMarketData = typeMarketData;
		this.exResultSetMarketData = new ExResultMarketData(). new ExResultSetMarketData(typeMarketData);
		this.sliceMilliseconds = sliceMilliseconds;
		
		ExchangeController.getIbExchangeInstance().getMarketData(typeMarketData, requestHolder);
	}
	
	public synchronized void addResult(ExResultRowMarketData exResultRowMarketData){
		Co.println("addResult");
		if (exResultRowMarketData.tickType == TickTypes.type_string){
			if (sliceMilliseconds != 0 && receivedTimestamp == 0){
				receivedTimestamp = Long.valueOf(exResultRowMarketData.tickStringValue);
				runThreadForSliceCollector(sliceMilliseconds);
			}
		}else if (receivedTimestamp != 0){
			synchronized(RequestMarketData.this){
				exResultSetMarketData.listOfExResultRowMarketData.add(exResultRowMarketData);
			}
		} 
	}
	
	public void runThreadForSliceCollector(final int sliceMilliseconds){
		Date date = new Date(receivedTimestamp*1000);
		//Co.println("*********************************************: " + date.toGMTString());
		
		this.threadForSliceCollector = new Thread(new Runnable(){
			@Override
			public void run() {
				while (true){
					try {Thread.sleep(sliceMilliseconds);}catch(InterruptedException e){return;}
					synchronized(RequestMarketData.this){
						ResultQuoteSlice resultQuoteSlice = new QuoteSliceTools().getQuoteSlice(exResultSetMarketData.listOfExResultRowMarketData);
						exResultSetMarketData.listOfExResultRowMarketData.clear();
						
						Co.println("Generated new QuoteSlice");
						Co.println("O,H,L,C" + resultQuoteSlice.priceOpen + "," + resultQuoteSlice.priceHigh + "," + resultQuoteSlice.priceLow + "," + resultQuoteSlice.priceClose + "," + resultQuoteSlice.sizeVolume);
					}
				}
			}
		});
		
		this.threadForSliceCollector.start();
	}
}
