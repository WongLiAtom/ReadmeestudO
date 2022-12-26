package ch.pschatzmann.stocks.accounting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.pschatzmann.dates.DateRange;
import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.IStockData;
import ch.pschatzmann.stocks.IStockID;
import ch.pschatzmann.stocks.IStockRecord;
import ch.pschatzmann.stocks.StockID;
import ch.pschatzmann.stocks.accounting.Transaction.Status;
import ch.pschatzmann.stocks.accounting.kpi.DrawDown;
import ch.pschatzmann.stocks.accounting.kpi.IKPICollector;
import ch.pschatzmann.stocks.accounting.kpi.KPI;
import ch.pschatzmann.stocks.accounting.kpi.KPIValue;
import ch.pschatzmann.stocks.accounting.kpi.NumberOfTrades;
import ch.pschatzmann.stocks.accounting.kpi.Return;
import ch.pschatzmann.stocks.accounting.kpi.SharpeRatio;
import ch.pschatzmann.stocks.execution.fees.IFeesModel;
import ch.pschatzmann.stocks.input.IReader;

/**
 * Implementation of Account KPIs for any basic Account.
 * 
 * @author pschatzmann
 *
 */
public class Account implements IKPICollector, IAccount, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(Account.class);
	private static IStockID defaultValue = new StockID("", "");
	private IBasicAccount account;
	private double riskFreeReturnInPercent = 0.0;
	protected transient Map<IStockID, IReader> readerMap = new HashMap();
	protected transient Map<IStockID, IStockData> stockDataMap = new HashMap();
	

	/**
	 * No Arg Constructor for Serialization
	 */
	public Account() {
	}

	/**
	 * Create new Account based on IBasicAccount
	 * 
	 * @param account
	 */
	public Account(IBasicAccount account) {
		this.account = account;
		//this.account = new BasicAccount(account.getId(),account.getCurrency(),account.getInitialCash(),account.getOpenDate(), account.getFeesModel());
		if (account instanceof Account) {
			this.stockDataMap.putAll(((Account) account).stockDataMap);
			this.readerMap.putAll(((Account) account).readerMap);
		}
	}

	public Account(String id, String currency, Double cash, Date openDate, IFeesModel fees) {
		this(new BasicAccount(id, currency, cash, openDate, fees));
	}

	@Override
	public List<Transaction> getTransactions() {
		return account.getTransactions();
	}

	@Override
	public void collectKPIValues(Collection<KPIValue> result) {
		result.add(new KPIValue(KPI.TotalFees, "Total Fees", this.getTotalFees()));
		result.add(new KPIValue(KPI.Cash, "Cash", this.getCash()));
		result.add(new KPIValue(KPI.ActualValue, "Total Value (at actual rates) including cash", this.getTotalValue()));
		result.add(new KPIValue(KPI.PurchasedValue, "Total Value (at purchased rates)", this.getTotalPurchasedValue()));

		result.add(new KPIValue(KPI.RealizedGains, "Realized Gains", this.getRealizedGain()));
		result.add(new KPIValue(KPI.UnrealizedGains, "Unrealized Gains", this.getUnrealizedGain()));

	}

	/**
	 * Returns the amout of cash which is currently available
	 * 
	 * @return
	 */
	@Override
	public double getCash() {
		return this.getTransactions()
			.stream()
			.mapToDouble(o -> o.getImpactOnCash())
			.sum();
	}

	/**
	 * Returns the amout of cash which is available at the indicated date
	 * 
	 * @param date
	 * @return
	 */
	@Override
	public double getCash(Date date) {
		return this.getTransactions().stream().filter(d -> d.getDate().getTime() <= date.getTime())
				.mapToDouble(o -> o.getImpactOnCash()).sum();
	}

	/**
	 * Returns the 