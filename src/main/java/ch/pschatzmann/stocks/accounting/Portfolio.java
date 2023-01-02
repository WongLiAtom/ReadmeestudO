package ch.pschatzmann.stocks.accounting;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.IStockID;

/**
 * Stock Portfolio information for a specific date. We provide summarize information at a specific date
 * for a each individual stocks which was held at any point of time.
 * 
 * @author pschatzmann
 *
 */

public class Portfolio implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(Portfolio.class);
	private IAccount ta;
	private Date date;
	private Map <IStockID, PortfolioStockInfo> data = new HashMap();
	/**
	 * Basic first constructor which connects the portfolio with the account
	 * @param ta
	 * @param date
	 */
	public Portfolio(IAccount ta, Date date) {
		LOG.debug("Portfolio new "+Context.format(date));
		this.ta = ta;
		this.date = date;
	}
	
	/**
	 * We setup the protfolio for a new date as a copy of the information of the prior date.
	 * 
	 * @param