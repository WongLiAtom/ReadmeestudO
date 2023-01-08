//package ch.pschatzmann.stocks.download;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.regex.Pattern;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import ch.pschatzmann.stocks.errors.UserException;
//import net.sf.sevenzipjbinding.IInArchive;
//import net.sf.sevenzipjbinding.PropID;
//import net.sf.sevenzipjbinding.SevenZip;
//import net.sf.sevenzipjbinding.SevenZipException;
//import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
//
///**
// * Extract the files from a 7zip compressed archive file
// * 
// * @author pschatzmann
// *
// */
//
//public class Extract7zip {
//	private static Logger LOG = LoggerFactory.getLogger(Extract7zip.class);
//	private File archive;
//	private File outputDirectory;
//	private boolean test;
//	private String filterRege