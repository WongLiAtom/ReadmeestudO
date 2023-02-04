
package ch.pschatzmann.stocks.input.parser;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.StockRecord;

/**
 * Parses lines which contain the following structure:
 * 
 * Date,	Open,	High,	Low,	Close,	Volume,	Adj Close
 * 
 * @author pschatzmann
 *
 */

public class CsvParser implements IInputParser, Serializable {
	private static final Logger LOG = LoggerFactory.getLogger(CsvParser.class);
	private static final long serialVersionUID = 1L;
	private transient SimpleDateFormat df = null;
	private boolean corrected = true;
	private int posDate=0;
	private int posOpen=1;
	private int posHigh = 2;
	private int posLow = 3;
	private int posClosing = 4;
	private int posAdjClosing =5;
	private int posVolume = 6;
	private List<String> fields = new ArrayList();
	private int targetLength = 7;

	public CsvParser() {
	}

	public CsvParser(boolean corrected) {
		this();
		this.corrected = corrected;
	}
	

	
	@Override
	public void setup(String line) {
		fields.clear();
		for (String str : line.split(getSeparator())){
			fields.add(str.toLowerCase().trim());
		}
		posDate = fields.indexOf("date");
		posOpen=  fields.indexOf("open");
		posHigh = fields.indexOf("high");;
		posLow =  fields.indexOf("low");;
		posClosing =  fields.indexOf("close");;
		posAdjClosing =  fields.indexOf("adj close");
		if (posAdjClosing==-1) {
			posAdjClosing =  fields.indexOf("adjclose");			
		}
		posVolume =  fields.indexOf("volume");;
		
	}
	
	@Override
	public StockRecord parse(String line) throws ParseException {
		StockRecord sr = new StockRecord();
		String[] sa = line.split(getSeparator());
		if (sa.length==this.targetLength) {
			double adjustmentFactor = 1.0;
			if (corrected) {
				try {
					Double div = toDouble(sa[posClosing],1.0);
					if (div!=null) {
						adjustmentFactor = toDouble(sa[posAdjClosing],1.0) / div;
					}
				} catch(Exception ex){
				}
			}
			sr.setAdjustmentFactor(adjustmentFactor);;

			sr.setDate(this.getDate(sa[posDate]));
			sr.setOpen(toDouble(sa[posOpen],adjustmentFactor));
			sr.setHigh(toDouble(sa[posHigh],adjustmentFactor));
			sr.setLow(toDouble(sa[posLow],adjustmentFactor));
			sr.setClosing(toDouble(sa[posClosing],adjustmentFactor));
			String volume = sa[posVolume];
			if (!Context.isEmpty(volume)) {
				sr.setVolume(toDouble(volume,1.0));
			}
		}
		return sr;
	}
	
	protected Double toDouble(String v, double adjustmentFactor) {
		return Context.isEmpty(v) || v.equals("null") ? null : Double.valueOf(v)*adjustmentFactor;
	}
	
	public String getSeparator() {
		return ",";
	}

	public synchronized Date getDate(String date) throws ParseException {
		try {
			return new Date(this.getDateFormat().parse(date).getTime());
		} catch(ParseException ex) {
			LOG.error("Could not parse '"+date+"' with "+df.toPattern());
			throw ex;
		}
	}
	
	protected DateFormat getDateFormat() {
		if (df==null) {
			df = new SimpleDateFormat("yyyy-MM-dd");
		}
		return df;
	}

	public synchronized String getString(Date date) {
		return df.format(date);
	}

	@Override
	public boolean isValid(String line) {
		return !line.toLowerCase().startsWith("date") && !line.trim().isEmpty() && !line.startsWith("\"") && !line.toLowerCase().startsWith("symbol");
	}

	public void setDateFormat(SimpleDateFormat simpleDateFormat) {
		this.df = simpleDateFormat;		
	}
	
	public void setTargetLength(int len) {
		this.targetLength = len;
	}

}