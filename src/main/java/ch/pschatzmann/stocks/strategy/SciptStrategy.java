
package ch.pschatzmann.stocks.strategy;

import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Strategy;
import org.ta4j.core.BarSeries;

import ch.pschatzmann.stocks.IStockData;
import ch.pschatzmann.stocks.strategy.optimization.InputParameterName;

/**
 * Strategies implemented by a scripting language. e.e JavaScript
 * 
 * @author pschatzmann
 *
 */

public class SciptStrategy extends CommonTradingStrategy implements ITradingStrategy {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger LOG = LoggerFactory.getLogger(SciptStrategy.class);
	private ScriptEngine engine;
	private String script;
	private String engineName;
