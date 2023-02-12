package ch.pschatzmann.stocks.integration;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.ta4j.core.Bar;
import org.ta4j.core.num.Num;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.IStockRecord;

/**
 * Class which implements org.ta4j.core Tick Interface
 * 
 * @author pschatzmann
 *
 */

public class StockBar implements Bar, Serializable {
	private static final long serialVersionUID = 1L;
	private int trades;
	private Num volume;
	private Num closePrice;
	private Num maxPrice;
	private Num minPrice;
	private Num openPrice;
	private Num amount;
	private Duration timePeriod;
	private ZonedDateTime beginTime;
	private ZonedDateTime endTime;

	public StockBar(ZonedDateTime time, Num open, Num high, Num low, Num closing, Num volume) {
		this.beginTime = time;
		this.endTime = time;
		this.openPrice = open;
		this.maxPrice = high;
		this.minPrice = low;
		this.closePrice = closing;
		this.volume = volume;
	}

	public StockBar(Date time, double open, double high, double lo