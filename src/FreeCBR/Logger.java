/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

import java.io.*;
import java.util.*;

/**
 * This class takes care of logging
 *
 * @author Lars Johanson
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class Logger implements java.io.Serializable
{
	/**
	 * Is the logger ready to log?
	 */
	private boolean ready;
	
	/**
	 * Name of the file to use as log file
	 */
	private String logfile;
	
	/**
	 * Output object
	 * @since 1.0
	 */
	private transient PrintWriter out;
	
	/**
	 * Calendar object
	 * @since 1.0
	 */
	private GregorianCalendar calendar;
	
	/**
	 * Will the logger output data to screen? If <code>true</code> then not.
	 * @since 1.0
	 */
	private boolean silent;
	
	
	/**
	* Empty constructor
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Logger()
	{
		ready = false;
		logfile = "";
		calendar = new java.util.GregorianCalendar();
		silent = false;
	}
	/**
	* Constructor that initiates the logger
	* 
	* @param logfile path to the file to write log to. May be set to "null" 
	*		which means no logging.
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Logger(String logfile)
	{
		ready = false;
		this.logfile = logfile;
		
		try
		{
			calendar = new java.util.GregorianCalendar();
			if (logfile != null)
			{
				out = new PrintWriter(new BufferedWriter(new FileWriter(logfile)));
				ready = true;
			}
		} catch (Exception e)
		{
			this.logfile = "";
		}
	}
	

	/**
	* Returns the current state
	* 
	* @return the current ready state (true/false)
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean getReady()
	{
		return this.ready;
	}
	
	
	/**
	* Returns the current silence state
	* 
	* @return the current silence state
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean getSilent()
	{
		return this.silent;
	}
	
	/**
	* Sets the current silence state
	* 
	* @param silent the silence state to set
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setSilent(boolean silent)
	{
		this.silent = silent;
	}
	
	
	/**
	* Returns the current log file
	* 
	* @return the current log file
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String getLogfile()
	{
		return this.logfile;
	}
	
	/**
	* Sets the current log file
	* 
	* @param logfile the file to use as logfile
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setLogfile(String logfile)
	{
		this.logfile = logfile;
	}
	
	
	/**
	* Reads the object from stream
	* 
	* @param in stream to read from
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void readObject(java.io.ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		try
		{
			if (logfile != null)
			{
				out = new PrintWriter(new BufferedWriter(new FileWriter(logfile)));
			}
		} catch (Exception e)
		{
			ready = false;
			this.logfile = "";
		}
	}
	
	
	/**
	 * Writes a message to log file, always starts with current date and 
	 *		time and ends with a newline
	 * 
	 * @param message the message to write
	 */
	protected void write(String message)
	{
		String logMessage = new String(getTime() + " " + message);
		
		if (!silent)
		{
			System.err.print(logMessage);
		}
		if (ready)
		{
			out.write(logMessage);
		}
		writeNL();
	}
	
	
	/**
	 * Writes a message to log file. No date, time or newline.
	 * 
	 * @param message the message to write
	 */
	protected void writeShort(String message)
	{
		if (!silent)
		{
			System.err.print(message);
		}
		if (ready)
		{
			out.write(message);
			out.flush();
		}
	}
	
	
	/**
	 * Writes only a newline to log file.
	 * 
	 */
	protected void writeNL()
	{
		if (!silent)
		{
			System.err.println();
		}
		if (ready)
		{
			out.println();
			out.flush();
		}
	}
	
	
	/**
	 * Returns current time in the format YYYY-MM-DD HH:MM:SS.ms
	 * 
	 * @return current time
	 */
	private String getTime()
	{
		int year = calendar.get(Calendar.YEAR);
		short month = (short) (calendar.get(Calendar.MONTH) + 1);
		short day = (short) calendar.get(Calendar.DAY_OF_MONTH);
		short hour = (short) calendar.get(Calendar.HOUR);
		short min = (short) calendar.get(Calendar.MINUTE);
		short sec = (short) calendar.get(Calendar.SECOND);
		short ms = (short) calendar.get(Calendar.MILLISECOND);
		StringBuffer sb;
		
		sb = new StringBuffer().append(year).append("-");
		if (month < 10)
			sb.append("0");
		sb.append(month).append("-");
		if (day < 10)
			sb.append("0");
		sb.append(day).append(" ").append(hour).append(":");
		if (min < 10)
			sb.append("0");
		sb.append(min).append(":");
		if (sec < 10)
			sb.append("0");
		sb.append(sec).append(".");
		if (ms < 100)
			sb.append("0");
		if (ms < 10)
			sb.append("0");
		sb.append(ms);
		
		return sb.toString();
	}
}

