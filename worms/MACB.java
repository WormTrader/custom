package com.wormtrader.custom.worms;
/*
 * MACB.java
 *
 * variation of the MACB Strategy from
 * Bernstein, Jake, "The Compleat Guide to Day Trading Stocks", page 144-
 * relaxes the buy & sell signals to only one bar completely above or
 * below the specified averages
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.MovingAverage;
import com.shanebow.util.SBFormat;
import com.wormtrader.positions.TapeWorm;

public final class MACB extends TapeWorm
	{
	private MovingAverage m_ma8Lo;
	private MovingAverage m_ma10Hi;

	public MACB()
		{
		super();
		setWarmUpPeriods( 8 );
		}

	public void initTape(Tape tape)
		{
		m_ma8Lo = (MovingAverage)tape.addStudy ( "SMA",  "8,L" );
		m_ma10Hi = (MovingAverage)tape.addStudy( "SMA", "10,H" );
		}

	public String getShortName() { return "MACB10_8"; }

	public String getMetrics ( int index, Bar bar )
		{
		return "loAvg: " + SBFormat.toDollarString(m_ma8Lo.getValue(index))
				+ " hiAvg: " + SBFormat.toDollarString(m_ma10Hi.getValue(index));
		}

	public void automata( int index, Bar bar )
		{
		if ( index < 1 ) return;

		if (( getQty() <= 0 ) // if we're short or flat, check for a buy signal
		&&  ( bar.getLow() > m_ma10Hi.getValue(index)))   // bar completely above
			{                                               //  10 period SMA of highs
			bar = getTape().get(index-1);                // check previous bar
			if ( bar.getLow() > m_ma10Hi.getValue(index-1)) // twice in a row = BUY
				goLong();
			}

		else if (( getQty() >= 0 ) // if flat or long, check for a sell signal
		&& ( bar.getHigh() < m_ma8Lo.getValue(index)))    // bar completely below
			{                                               //  8 period SMA of lows
			bar = getTape().get(index-1);                // check previous bar
			if ( bar.getHigh() < m_ma8Lo.getValue(index-1)) // twice in a row!!
				goShort();
			}
		}
	}