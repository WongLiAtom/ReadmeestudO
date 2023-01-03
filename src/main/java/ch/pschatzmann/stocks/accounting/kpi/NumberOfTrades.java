package ch.pschatzmann.stocks.accounting.kpi;

import java.io.Serializable;
import java.util.Collection;

import ch.pschatzmann.stocks.accounting.IBasicAccount;

/**
 * Calculation logic for Number of trades, selles, buys...
 * 
 * @author pschatzmann
 *
 */
public class NumberOfTrades implements IKPICollector, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IBasicAccount account;

	public NumberOfTrades(IBasicAccount account) {
		this.account = account;
	}

	public Long getNumberOfTrades() {
		return this.account.getTransactions().stream().filter(t -> !t.isCashTransfer()).count();
	}

	public Long getNumberOfSells() {
		return this.account.getTransactions().stream().filter(t -> !t.isCashTransfer() && t.isSell()).count();
	}

	public Long getNumberOfBuys() {
		return this.account.getTransactions().stream().filter(t -> !t.isCashTransfer() && t.isBuy()).count();
	}

	public Long getNumberOfCashTransfers() {
		return this.account.getTransactions().stream().filter(t -> t.isCashTransfer()).count();
	}
	
	public Long getNumberTradedStocks() {
		return this.account.getTransactio