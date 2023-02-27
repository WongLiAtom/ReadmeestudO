package ch.pschatzmann.stocks.ta4j.indicator;

import java.io.Serializable;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Calculates the absolute difference to the prior period or to the base
 * indicator
 * 
 * @author pschatzmann
 *
 */
public class AbsoluteChangeIndicator extends CachedIndicator<Num> implements IIndicator<Num> {
	private static final long serialVersionUID = 1L;
	private Indicator<Num> prices;
	private Indicator<Num> prices1;
	private Num na = null;

	public AbsoluteChangeIndicator(Indicator<Num> prices) {
		this(prices, prices.numOf(Double.NaN));
	}

	public AbsoluteChangeIndicator(Indicator<Num> originalValues, Indicator<Num> newValues) {
		super(newValues.getBarSeries());
		this.prices = originalValues;
		this.prices1 = newValues;
		this.na = prices.numOf(Double.NaN);
	}

	public AbsoluteChangeIndicator(Indicator<Num> prices, Num defaultNA) {
		super(prices.getBarSeries());
		this.prices = prices;
		this.na = defaultNA;
	}

	@Override
	protected Num calculate(int index) {
		try {
			if (prices1 == null) {
				if (index >= 1 && index <= prices.getBarSeries().g