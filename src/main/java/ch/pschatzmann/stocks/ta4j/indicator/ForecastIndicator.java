package ch.pschatzmann.stocks.ta4j.indicator;

import java.util.Date;

import org.ta4j.core.Indicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.forecasting.IForecast;
import ch.pschatzmann.stocks.integration.HistoricValues;

/**
 * We use IForecast as Indicator
 * 
 * @author pschatzmann
 *
 */

public class ForecastIndicator implements Indicator<Num>, IIndicator<N