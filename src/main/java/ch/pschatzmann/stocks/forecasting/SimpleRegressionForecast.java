package ch.pschatzmann.stocks.forecasting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import ch.pschatzmann.dates.CalendarUtils;
import ch.pschatzmann.stocks.accounting.HistoricValue;
import ch.pschatzmann.stocks.accounting.IHistoricValue;
import ch.pschatzmann.stocks.integration.HistoricValues;
import ch.pschatzmann.stocks.ta4j.indicator.IndicatorFromData;
import ch.pschatzmann.stocks.ta4j.indicator.IndicatorUtils;
import ch.pschatzmann.stocks.utils.Calculations;

/**
 * We use a simple regression on the current stock data
 * 
 * @author pschatzmann
 *
 */
public class SimpleRegressionForecast extends BaseForecast  {
	private static final long serialVersionUID = 1L;
	private SimpleRegression regression = new SimpleRegression();
		
	public SimpleRegressionForecast(HistoricValues values) {
		super(values);
		setName(values.getName()+"-SimpleRegressionForecast");
		int x = 0;
		for (IHistoricValue value : this.getValues().list()) {
			regression.addData(x++, value.getValue());
		}
	}

	@Override
	public HistoricValues for