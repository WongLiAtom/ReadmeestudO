package ch.pschatzmann.stocks.execution;

import ch.pschatzmann.stocks.accounting.IAccount;
import ch.pschatzmann.stocks.execution.price.IPriceLogic;

public interface ITrader {

	/**
	 * Executes all open orders for the current account
	 */
	void execute();

	/**
	 * Returns the trading account
	 * @return
	 */
	IAccount getAccount();
	
	/**
	