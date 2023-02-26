package ch.pschatzmann.stocks.strategy.allocation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.pschatzmann.stocks.strategy.ITradingStrategy;

/**
 * Distributes the weights randomly. We make sure that we keep the defined factor even
 * if strategies are removed and then re-added
 * 
 * @author pschatzmann
 *
 */
pu