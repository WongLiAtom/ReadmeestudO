package ch.pschatzmann.stocks.input.parser;

import java.io.Serializable;
import java.text.ParseException;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.StockID;
import ch.pschatzmann.stocks.StockRecord;

/**
 * Parses lines which contain the following structure:
 * 
 * Date,	Open,	High,	Low,	Close,	Volume,	Adj Close
 * 
 * @author pschatzmann
 *
 */

public class QuandlEuronextParser extends QuandlBaseParser implements IInputParser, Serializable {
	private static final long serialVersionUID = 1L;
	private StockID id;

	public QuandlEuronextParser() {
	}
	

	@Override
	public StockRecord parse(String line) throws ParseException {
		StockRecord sr = new StockRecord();
		String[] sa = line.split(getSeparator());
		if (sa.length==7) {
			double adjustmentFactor = 1.0;
			sr.setAdjustmentFactor(adjustmentFactor);;

			sr.setDate(this.getDate(sa[0]));
			sr.setOpen(toDouble(sa[1],adjustmentFactor));
			sr.setHigh(toDouble(sa[2],adjustmentFacto