
package ch.pschatzmann.stocks;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;

import ch.pschatzmann.dates.DateRange;
import ch.pschatzmann.stocks.accounting.HistoricValue;
import ch.pschatzmann.stocks.accounting.IHistoricValue;
import ch.pschatzmann.stocks.input.IReader;
import ch.pschatzmann.stocks.integration.StockTimeSeries;
import ch.pschatzmann.stocks.utils.CSVWriter;

/**
 * Price history of an individual stock
 * 
 * @author pschatzmann
 *
 */

public class StockData implements Serializable, IStockTarget, IStockData, IResettable {
	public enum DateMatching {
		Exact, Prior, Next
	};

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StockData.class);
	private List<IStockRecord> history = null;
	private IStockID id;
	private transient Comparator<IStockRecord> dateComparator;
	private transient DateFormat df;
	private transient BarSeries timeSeries;
	private IReader reader = null;

	public StockData() {
	}

	public StockData(IStockID id) {
		this();
		this.id = id;
		this.reader = Context.getDefaultReader();
	}

	public StockData(IStockID id, IReader reader) {
		this();
		this.id = id;
		this.reader = reader;
	}

	public StockData(StockData source) {
		this.id = source.id;
		this.reader = source.reader;
		this.history = source.history;
		this.timeSeries = source.timeSeries;
	}

	public StockData(IStockID id, List<IStockRecord>history) {
		this.id = id;
		this.history = history;
	}

	
	@Override
	public String getTicker() {
		return id == null ? null : id.getTicker();
	}

	@Override
	public String getExchange() {
		return id == null ? null : id.getExchange();
	}

	@Override
	public IStockID getStockID() {
		return this.id;
	}

	public void setStockID(IStockID stockID) {
		this.id = stockID;
	}

	public void setStockID(String stockID) {
		this.id = StockID.parse(stockID);
	}

	@Override
	public synchronized List<IStockRecord> getHistory() {
		if (history == null) {
			LOG.debug("getHistory " + this);
			IReader r = this.getReader();

			String key = this.getStockID() + "/" + getSimpleClassName(r);
			getHistoryFromCache(key);

			if (history == null) {
				history = new ArrayList();
				if (r != null) {
					int count = r.read(this);
					LOG.info("Reading stock data for {} -> {} records", this.getStockID(), count);
					putHistoryToCache(key);
				}
			}
		}
		return history;
	}

	public static String getSimpleClassName(IReader r) {
		return r == null ? "" : r.getClass().getSimpleName();
	}

	private void putHistoryToCache(String key) {
		if (Context.isCacheActive()) {
			Context.getCache().put(key, history);
		}
	}

	private void getHistoryFromCache(String key) {
		if (Context.isCacheActive()) {
			try {
				history = Context.getCache().get(key);
				if (history!=null) {