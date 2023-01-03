package ch.pschatzmann.stocks.accounting;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.IStockID;
import ch.pschatzmann.stocks.StockID;

/**
 * Order to buy or sell an individual stock. This object is also used to record
 * cash transfers in and out of the account;
 * 
 * @author pschatzmann
 *
 */

public class Transaction implements Serializable, Comparable<Transaction> {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(Transaction.class);
	private static IDGenerator idGenerator = new IDGenerator();

	/**
	 * Type of Order (Market, Limit, Stop...)
	 */
	public enum Type {
		Market, Limit, Stop, CashTransfer
	}

	/**
	 * Requested Action
	 */
	public enum Action {
		SellAll, BuyMax
	}

	public enum Status {
		Planned, Submitted, Filled, Cancelled
	}

	/**
	 * Automatically Determined if Buy or Sell
	 */
	public enum BuyOrSell {
		Sell, Buy, NA
	};

	@JsonDeserialize(as = StockID.class)
	private IStockID stockID;
	private Date date;
	private long quantity;
	private double requestedPrice;
	private double cash;
	private Type transactionType;
	private double filledPrice;
	private double fees;
	private String comment = "";
	private String id = null;
	private Status status = Status.Planned;

	public Transaction() {
	}

	public Transaction(Date date, IStockID id, long quantity) {
		this.stockID = id;
		this.date = date;
		this.quantity = quantity;
		this.transactionType = Type.Market;
	}

	public Transaction(Date date, IStockID id, IAccount acc, Action act) {
		this.stockID = id;
		this.date = date;

		switch (act) {
		case SellAll:
			Portfolio p = acc.getPortfolio(date);
			transactionType = Type.Market;
			PortfolioStockInfo line = p.getInfo(id);
			this.quantity = line.getQuantity();
		case BuyMax:
			Double cash = acc.getCash(date);
			Double price = acc.getStockPrice(id, date);
			this.quantity = Double.valueOf(cash / price).longValue();
		}
	}

	public Transaction(Date date, StockID id, long quantity, double price, Type orderType) {
		this.stockID = id;
		this.date = date;
		this.quantity = quantity;
		this.transactionType = orderType;
		this.requestedPrice = price;
	}

	/**
	 * Transfer cash into the account (positive) or out of the account (negative)
	 */
	public Transaction(Date date, double cash) {
		this.transactionType = Type.CashTransfer;
		this.stockID = Context.cashID();
		this.cash = cash;
		this.date = date;
		this.status = Status.Filled;
	}

	public IStockID getStockID() {
		return stockID;
	}

	public Date getDate() {
		return date;
	}

	public Long getQuantity() {
		return quantity;
	}

	/**
	 * Indicates the oder type
	 */
	public Type getRequestedPriceType() {
		return transactionType;
	}

	/**
	 * Defines the Price Type
	 * 
	 * @param t
	 */
	public void setRequestedPriceType(Type t) {
		this.transactionType = t;
	}

	/**
	 * Not available for market orders. Indicates the Limit or Stop price of the
	 * order
	 * 
	 * @return
	 */
	public Double getRequestedPrice() {
		return requestedPrice;
	}

	public double getFees() {
		return fees;
	}

	public void setFees(double fees) {
		this.fees = fees;
	}

	public double getFilledPrice() {
		return filledPrice;
	}

	public BuyOrSell getBuyOrSell() {
		if (this.getQuantity() == 0) {
			return BuyOrSell.NA;
		} else if (this.getQuantity() > 