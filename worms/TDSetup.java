package com.wormtrader.custom.worms;
/*
 * TDSetup.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.Tape;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.Trader;
import com.shanebow.util.SBLog;

public class TDSetup extends TapeWorm
	{
	public static final byte SETUP = 0;
	public static final byte BUY_SETUP = 1;
	public static final byte BUY_INTERSECTION = 2;
	public static final byte BUY_COUNTDOWN = 3;
	public static final byte SELL_SETUP = 4;
	public static final byte SELL_INTERSECTION = 5;
	public static final byte SELL_COUNTDOWN = 6;
	public static final String PHASE_NAMES[] =
		{ "SETUP", "BUY_SETUP", "BUY_INTERSECTION", "BUY_COUNTDOWN",
		      "SELL_SETUP", "SELL_INTERSECTION", "SELL_COUNTDOWN" };
	protected byte m_phase = 0;
	protected int  m_setupStartIndex = 0;
	protected int  m_setupCount = 0;  // negative values for buy setups
	private int    m_cancelTrigger = 0; // using highest true high / lowest true low
	protected int  m_countdownCount = 0;
	protected String m_debugMsg = "";

	public String getShortName() { return "TDSet"; }

	public void initTape(Tape tape)
		{
		m_phase = SETUP;
		m_setupCount = 0;
		m_cancelTrigger = 0; // highest true hi or lowest true low of setup period
		m_countdownCount = 0;
/*
		int numbars = tape.size();
SBLog.alert("%s.initTape(%s) size: %d", getShortName(), tape.toString(), numbars );
		for ( int i = 4; i < numbars; i++ )
			automata ( i, tape.get(i));
SBLog.alert("%s.initTape(%s) end setup: %d", getShortName(), tape.toString(), m_setupCount );
*/
		}

	public void historyDone()
		{
super.historyDone();
SBLog.format("%s.historyDone(%s) end setup: %d", getShortName(), getTape().toString(), m_setupCount );
		}

	public String getMetrics ( int index, Bar bar )
		{
		return String.format ( "%s %2d %2d %s", PHASE_NAMES[m_phase], m_setupCount,
													m_countdownCount, m_debugMsg );
		}

	public void automata( int index, Bar bar )
		{
		if ( index < 4 )
			return;

		m_debugMsg = "";
		int close = bar.getClose();
		int prevClose = lookback(index, 4).getClose();
		int change = close - prevClose;
		if ( change == 0 )
			{
			m_setupCount = 0;
			m_cancelTrigger = 0;
			}
		else if ( change < 0 ) // BUY SETUP
			{
			if ( m_setupCount >= 0 ) // price flip
				{
				m_setupCount = -1;
				m_setupStartIndex = index;
				m_cancelTrigger = Math.max( bar.getHigh(), lookback(index, 1).getClose());
				}
			else if ( --m_setupCount == -9 )
				{
				m_phase = BUY_COUNTDOWN;
				m_countdownCount = 0;
				}
			else // check for cancelled buy setup
				{
				if ( bar.getLow() > m_cancelTrigger )
					{
					m_setupCount = 0;
					if ( m_phase == BUY_COUNTDOWN )
						{
						m_debugMsg = PHASE_NAMES[BUY_COUNTDOWN] + " & ";
						m_phase = SETUP;
						m_countdownCount = 0;
						}
					m_debugMsg += "BUY SETUP cancelled countertrend";
					}
				else m_cancelTrigger = Math.max( bar.getHigh(), m_cancelTrigger );
				}
			}
		else if ( change > 0 ) // SELL SETUP
			{
			if ( m_setupCount <= 0 ) // price flip
				{
				m_setupCount = 1;
				m_setupStartIndex = index;
				m_cancelTrigger = Math.min( bar.getLow(), lookback(index, 1).getClose());
				}
			else if ( ++m_setupCount == 9 )
				{
				m_phase = SELL_COUNTDOWN;
				m_countdownCount = 0;
				}
			else // check for cancelled sell setup
				{
				if ( bar.getHigh() < m_cancelTrigger )
					{
					m_setupCount = 0;
					if ( m_phase == SELL_COUNTDOWN )
						{
						m_debugMsg = PHASE_NAMES[SELL_COUNTDOWN] + " & ";
						m_phase = SETUP;
						m_countdownCount = 0;
						}
					m_debugMsg += "SELL SETUP cancelled countertrend";
					}
				else m_cancelTrigger = Math.min( bar.getLow(), m_cancelTrigger );
				}
			}
		}

	protected final Bar lookback( int index, int howFar )
		{
		return getTape().get(index - howFar);
		}
	}