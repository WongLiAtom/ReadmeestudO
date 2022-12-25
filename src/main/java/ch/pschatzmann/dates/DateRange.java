package ch.pschatzmann.dates;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.stocks.Context;

/**
 * Date range which is defined by a start and end date. The date range can have
 * a name
 * 
 * @author pschatzmann
 *
 */
public class DateRange implements Serializable {
	private static final Logger LOG = LoggerFactory.ge