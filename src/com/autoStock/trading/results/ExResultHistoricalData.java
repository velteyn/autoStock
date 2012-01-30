package com.autoStock.trading.results;

import java.util.ArrayList;

import com.autoStock.trading.types.TypeHistoricalData;

/**
 * @author Kevin Kowalewski
 *
 */

public class ExResultHistoricalData {
	
	public class ExResultSetHistoricalData {
		public TypeHistoricalData typeHistoricalData;
		public ArrayList<ExResultRowHistoricalData> listOfExResultRowHistoricalData = new ArrayList<ExResultRowHistoricalData>();
		
		public ExResultSetHistoricalData(TypeHistoricalData typeHistoricalData){
			this.typeHistoricalData = typeHistoricalData;
		}
	}
	
	public class ExResultRowHistoricalData {
		public String date;
		public float price;
		
		public ExResultRowHistoricalData(String date, float price){
			this.date = date;
			this.price = price;
		}
	}
}