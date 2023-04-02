package ch.pschatzmann.stocks.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.IStockData;
import ch.pschatzmann.stocks.IStockID;
import ch.pschatzmann.stocks.data.universe.EdgarUniverse;
import ch.pschatzmann.stocks.data.universe.IUniverse;
import ch.pschatzmann.stocks.data.universe.JsonUniverse;
import ch.pschatzmann.stocks.data.universe.MarketUniverse;
import ch.pschatzmann.stocks.data.universe.NasdaqUniverse;
import ch.pschatzmann.stocks.data.universe.NyseUniverse;
import ch.pschatzmann.stocks.data.universe.QuandlBSEUniverse;
import ch.pschatzmann.stocks.data.universe.QuandlEuronextUniverse;
import ch.pschatzmann.stocks.data.universe.QuandlSixUniverse;
import ch.pschatzmann.stocks.data.universe.QuandlWIKIUniverse;
import ch.pschatzmann.stocks.data.universe.exchanges.ExchangesFromFile;
import ch.pschatzmann.stocks.data.universe.exchanges.ExchangesMappings;
import ch.pschatzmann.stocks.data.universe.exchanges.ExchangesMappings.ExchangeNameSpace;
import ch.pschatzmann.stocks.errors.UniverseException;

/**
 * Test classes which are testing the functionality around the Universe which provides
 * a stream of Stock IDs
 * 
 * @author pschatzmann
 *
 */

public class TestUniverse {
	private static final Logger LOG = LoggerFactory.getLogger(TestUniverse.class);
	
	@BeforeClass 
	public static void setup() throws Exception{
		System.out.println("*** "+TestUniverse.class.getSimpleName()+" ***");
	}	
	
	@Test
	public void testCsvUniverse() throws Exception {
		LOG.info("testCsvUniverse");
		IUniverse fu = new MarketUniverse();	
		List<IStockID> result = fu.list().stream().collect(Collectors.toList());
		LOG.info("{}",result);
		int count = result.size();
		Assert.assertTrue(count>0);
	}
	
	@Test
	public void testFileUniverseSingle() throws Exception {
		LOG.info("testFileUniverseSingle");
		IUniverse fu = new MarketUniverse("NASDAQ","AAPL");
		List<IStockID> result = fu.list().stream().collect(Collectors.toList());
		LOG.info("{}",result);
		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void testFileUniverseRegex() throws Exception {
		LOG.info("testFileUniverseRegex");
		IUniverse fu = new MarketUniverse("NASDAQ","A.*");	
		List<IStockID> result = fu.list().stream().collect(Collectors.toList());
		LOG.info("{}",result);
		int count = result.size();
		Assert.assertTrue(count>0);
		Assert.assertTrue(count<1000);		
	}
	@Test
	public void testFileUniverseNASDAQ() throws Exception {
		LOG.info("testFileUniverseNASDAQ");
		IUniverse fu = new MarketUniverse("NASDAQ");
		List<IStockID> result = fu.list().stream().collect(Collectors.toList());
		LOG.info("{}",result);
		Assert.assertTrue(result.size()>1000);	
	}

	@Test
	public void testFileUniverseAll() throws Exception {
		LOG.info("testFileUniverseAll");
		IUniverse fu = new MarketUniverse();
		//fu.list().forEach(a -> LOG.info(a));
		Set<String> result = fu.list().stream().map(a -> a.getExchange()).collect(Collectors.toSet());
		LOG.info("Count of all exhanges: "+result.size());

		Assert.assertEquals(new TreeSet(new ExchangesFromFile().list()), new TreeSet(result));
	}
	
	@Test
	public void testJson() throws Exception {
		JsonUniverse fu = new JsonUniverse("/NASDAQ-GlobalExtremaStrategy-Annealing.json");
		Assert.assertTrue(fu.list().stream().iterator().hasNext());
		LOG.info("{}",fu.list().stream().collect(Collectors.toList()));
	}

	@Ignore
	@Test
	public void testQuandlWIKI() throws Exception {
		LOG.info("testQuandlWIKI");
		QuandlWIKIUniverse fu = new QuandlWIKIUniverse();
		List<IStockID> result = fu.stream().collect(Collectors.toList());
		LOG.info("{}",result);
		Assert.assertFalse(result.isEmpty());		
		IStockData sd = Context.getStockData(result.get(0),fu.getReader());
		LOG.info("{}",sd);
		Assert.assertFalse(sd.isEmpty());
	}
	
	
	@Test
	public void testExchangesFromFile() throws IOException {
		Collection<String> list = new ExchangesFromFile().list();
		LOG.info("testExchangesFromFile: "+list);
		Assert.assertFalse(list.isEmpty());
		Assert.assertTrue(list.contains("AMEX"));
		Assert.assertTrue(list.contains("USMF"));
	}

	@Test
	public void testSixUniverse() throws Exception {
		QuandlSixUnive