package com.wormtrader.custom.worms;
/********************************************************************
* @(#)VIX.java	1.00 20091120
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* VIX: A worm that looks at crosses of 20 x 5 EMA of the VIX close
* to generate signals. 
*
* @version 1.00 11/20/09
* @author Rick Salamone
* 20091120 RTS 0.00 demo for VIX
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.MovingAverage;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBDate;

public final class VIX extends TapeWorm
	{
	private static final Tape vix = new Tape( "VIX@CBOE", BarSize.ONE_DAY, SBDate.today );
	private static MovingAverage m_maFast;
	private static MovingAverage m_maSlow;
	static
		{
		vix.setGoBack( "All dates" ); // backfill all daily VIX data
		m_maFast = (MovingAverage)vix.addStudy ( "EMA", "5,C" );
		m_maSlow = (MovingAverage)vix.addStudy ( "EMA", "20,C" );
		vix.dump();
		}

	// To synchronize the tape of the VIX with the tape of the traded
	// security, we maintain an offset into VIX tape. For efficiency,
	// the VIX tape is loaded once and includes ALL dates. The tape of
	// the tradable is variously backfilled anywhere from one day to
	// several weeks depending upon the app (e.g. the trader, trainer,
	// or backtester) as well as user settings.
	private Tape m_tape; // the tape for the traded security
	private int m_offset = -1;
	private int m_bias = 0;

	public String getShortName() { return "VIX5x20"; }
	public BarSize getMaxBarSize() { return BarSize.ONE_DAY; }
	public BarSize getMinBarSize() { return BarSize.ONE_DAY; }

	public void initTape(Tape tape)
		{
		m_tape = tape;
		tape.dump();
		}

	/** calcOffset: Figures out an index into the vix tape corresponds with
	* the first date on the tape, so we can use when reading the vix parameters
	*/
	private void calcOffset()
		{
		if ( m_offset == -1 )
			{
			m_offset = vix.indexOf(m_tape.get(0).getTime());
			log ( "set VIX offset to %d, %s for tape bar 0 %s", m_offset,
				SBDate.yyyymmdd(vix.get(m_offset).getTime()),
				SBDate.yyyymmdd(m_tape.get(0).getTime()));
			}
		}

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{																			// if debugging
		calcOffset();
		int slow = m_maSlow.getValue(index+m_offset);
		int fast = m_maFast.getValue(index+m_offset);
		return "slow=" + slow + " fast=" + fast
					+ ((fast > slow) ? " +" : " -");
		}

	public void automata( int index, Bar bar )
		{
		calcOffset();
		int slow = m_maSlow.getValue(index+m_offset);
		int fast = m_maFast.getValue(index+m_offset);

		int newBias = fast - slow + 1;
		if ( newBias * m_bias <= 0 )
			{
			if ( newBias > 0 ) goLong();
			else if ( newBias < 0 ) goShort();
			}
		m_bias = newBias;
/***
		if (( fast > slow ) && ( getQty() <= 0 ))
			goLong();
		else if (( fast < slow ) && ( getQty() >= 0 ))
			goShort();
***/
		}
	}