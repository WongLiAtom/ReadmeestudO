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
	 * Returns the value of all available stocks valuated at the market price
	 * 
	 * @return
	 */
	public double getActualValue() {
		return this.getPortfolio().getActualValue();
	}

	/**
	 * Returns the value of all available stocks valuated at purchasing price
	 * 
	 * @return
	 */
	public double getPurchasedValue() {
		return this.getPortfolio().getPurchasedValue();
	}

	/**
	 * Returns the total value of the account (stocks valuated at the market price
	 * and cash)
	 * 
	 * @return
	 */
	public double getTotalValue() {
		return getCash() + getActualValue();
	}

	/**
	 * Returns the total of the values of the account ( stocks valuated at
	 * purchasing price and cash)
	 * 
	 * @return
	 */
	public double getTotalPurchasedValue() {
		return getCash() + getPurchasedValue();
	}

	/**
	 * Returns the total of all fees
	 * 
	 * @return
	 */
	public double getTotalFees() {
		return this.getTransactions().stream().filter(t -> !t.isCashTransfer()).mapToDouble(t -> t.getFees()).sum();
	}

	/**
	 * Returns the realized gain. We do not consider trading fees!
	 * 
	 * @return
	 */
	public double getRealizedGain() {
		return this.getPortfolio().getRealizedGains();
	}

	/**
	 * Returns the unrealized gains
	 * 
	 * @return
	 */
	public double getUnrealizedGain() {
		return this.getPortfolio().getUnrealizedGains();
	}

	/**
	 * Returns the total profit
	 * 
	 * @return
	 */
	public double getTotalProfit() {
		return this.getPortfolio().getTotalProfit();
	}

	/**
	 * Determines the total number of trades
	 * 
	 * @return
	 */

	public long getNumberOfTrades() {
		return getPortfolio().getNumberOfTrades();
	}

	/**
	 * Returns the portfolio history for all transaction dates
	 * 
	 * @return
	 */
	public Stream<Portfolio> getTradingPortfolioHistory() {
		List<Portfolio> result = new ArrayList();
		Portfolio lastPortfolio = null;
		Date currentDate = new Date();
		for (Date date : getAllTransactionDates()) {
			Portfolio portfolio = this.getPortfolio(date);
			if (portfolio == null) {
				Portfolio temp = new Portfolio(this, date, lastPortfolio);
				getTransactionsForDate(date).forEach(t -> temp.recordOrder(t, date));
				temp.updateActualPrices();
				portfolio = temp;
			}
			result.add(portfolio);
			lastPortfolio = portfolio;
		}
		return result.stream();
	}

	/**
	 * Gets the portfolio history over all dates
	 * 
	 * @return
	 */
	public Stream<Portfolio> getPortfolioHistory() {
		List<Portfolio> result = new ArrayList();
		Portfolio lastPortfolio = null;
		Date currentDate = new Date();
		for (Date date : getAllDates()) {
			Portfolio portfolio = this.getPortfolio(date);
			if (portfolio == null) {
				Portfolio temp = new Portfolio(this, date, lastPortfolio);
				getTransactionsForDate(date).forEach(t -> temp.recordOrder(t, date));
				temp.updateActualPrices();
				portfolio = temp;
			}
			result.add(portfolio);
			lastPortfolio = portfolio;
		}
		return result.stream();
	}

	/**
	 * Returns the portfolio history for a single stock
	 * 
	 * @return
	 */
	public Stream<PortfolioStockInfo> getPortfolioStockInfoHistory(IStockID id) {
		return getPortfolioHistory().map(p -> p.getInfo(id));
	}

	/**
	 * Determines the history of the % returns for the indicated stock. The return
	 * includes the fees.
	 * 
	 * @param id
	 * @param adjument
	 * @return
	 */
	public Stream<IHistoricValue> getStockHistoryReturns(StockID id, double adjument) {
		List<PortfolioStockInfo> historyList = getPortfolioStockInfoHistory(id).sorted().collect(Collectors.toList());
		List<IHistoricValue> result = new ArrayList();

		for (int i = 1; i < historyList.size(); i++) {
			Date date = historyList.get(i).getDate();
			double value = historyList.get(i).getActualValue() - historyList.get(i).getFees();
			double priorValue = historyList.get(i - 1).getActualValue() - historyList.get(i - 1).getFees();
			long qty = historyList.get(i).getQuantity();
			if (qty != 0L) {
				result.add(new HistoricValue(date, ((value - priorValue) / priorValue) + adjument));
			} else {
				result.add(new HistoricValue(date, 0.0));
			}
		}
		return result.stream();
	}

	private Set<Date> getAllTransactionDates() {
		Set<Date> result = new TreeSet();
		result.addAll(this.getOrderDates());
		result.addAll(this.getTradingDates());
		return result;
	}

	/**
	 * Returns the information on the portfolio which with the