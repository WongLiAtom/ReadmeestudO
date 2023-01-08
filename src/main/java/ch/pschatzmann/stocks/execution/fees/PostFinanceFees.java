package ch.pschatzmann.stocks.execution.fees;

/**
 * Trading fees for postfinance 
 * https://www.postfinance.ch/content/dam/pfch/doc/prod/acc/etrad_cond_en.pdf
 * @author pschatzmann
 *
 */
public class PostFinanceFees implements IFeesModel {

	@Override
	public double getFeesPerTrade(double qty, double value) {
		if (value <= 1000) {
			return 25;
		}
		if (value <= 5000) {
			return 35;
		}
		if (value <= 10000) {
			return 40;
		}