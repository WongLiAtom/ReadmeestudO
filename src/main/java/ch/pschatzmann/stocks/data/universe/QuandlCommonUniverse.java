package ch.pschatzmann.stocks.data.universe;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.IStockID;
import ch.pschatzmann.stocks.StockID;
import ch.pschatzmann.stocks.errors.UniverseException;
import ch.pschatzmann.stocks.input.IReader;
import ch.pschatzmann.stocks.utils.Streams;

/**
 * Provides the stocks which are available via the Quandl WIKI database
 * 
 * @author pschatzmann
 *
 */

public abstract class QuandlCommonUniverse implements IUniverse, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(QuandlCommonUniverse.class);
	private static String apiKey = Context.getPropertyMandatory("QuandlAPIKey");
	private static CacheAccess<String, List<IStockID>> cache;;
	private String databaseCode;
	private S