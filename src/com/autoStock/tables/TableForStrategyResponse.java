/**
 * 
 */
package com.autoStock.tables;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.autoStock.backtest.BacktestContainer;
import com.autoStock.signal.SignalMoment;
import com.autoStock.strategy.StrategyResponse;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.MiscTools;

/**
 * @author Kevin
 *
 */
public class TableForStrategyResponse extends BaseTable {
	private BacktestContainer backtestContainer;
	
	public TableForStrategyResponse(BacktestContainer backtestContainer){
		this.backtestContainer = backtestContainer;
	}

	@Override
	public ArrayList<ArrayList<String>> getDisplayRows() {
		for (StrategyResponse strategyResponse : backtestContainer.listOfStrategyResponse) {
			ArrayList<String> listOfString = new ArrayList<String>();
			listOfString.add(DateTools.getPrettyDate(strategyResponse.quoteSlice.dateTime));
			listOfString.add(backtestContainer.symbol.symbolName);
			listOfString.add(new DecimalFormat("#.00").format(strategyResponse.quoteSlice.priceClose));
			listOfString.add(strategyResponse.strategyActionCause.name().replaceAll("proceed_changed", "entry").replaceAll("changed", "").replaceAll("proceed_", "").replaceAll("_condition_", " -> ").replaceAll("_", " "));
			listOfString.add(strategyResponse.positionGovernorResponse.status.name().replaceAll("changed_", "").replaceAll("_", " "));

			String stringForSignal = new String();

			for (SignalMoment signalMoment : strategyResponse.signal.getListOfSignalMoment()) {
				stringForSignal += signalMoment.signalMetricType.name().replace("metric_", "") + ":" + new DecimalFormat("0.00").format(signalMoment.strength);
				if (signalMoment.debug.length() > 0){stringForSignal += " - " + signalMoment.debug;}
			}

			listOfString.add(stringForSignal);

			listOfString.add(TableTools.getTransactionDetails(strategyResponse));
			listOfString.add(TableTools.getProfitLossDetails(strategyResponse));
			listOfString.add(MiscTools.getCommifiedValue(strategyResponse.basicAccountCopy.getBalance()));
			
//			if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_exit || strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_short_exit) {
//				listOfString.add("$ " + new DecimalFormat("#.00").format(strategyResponse.positionGovernorResponse.position.getPositionProfitLossAfterComission(true)));
//			} else if (strategyResponse.positionGovernorResponse.status == PositionGovernorResponseStatus.changed_long_reentry) {
//				listOfString.add("");
//			} else {
//				listOfString.add("");
//			}

			listOfDisplayRows.add(listOfString);
		}
		
		return listOfDisplayRows;
	}
}
