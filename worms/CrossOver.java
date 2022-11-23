package com.wormtrader.custom.worms;
/*
 * CrossOver.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.MovingAverage;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;

public final class CrossOver extends TapeWorm
	{
	private MovingAverage m_maFast;
	private MovingAverage m_maSlow;
	private int m_bias = 0;

	public CrossOver()
		{
		super();
		setWarmUpPeriods( 9 ); // 25
		}

	public void initTape(Tape tape)
		{
		m_maFast = (MovingAverage)tape.addStudy ( "SMA", "9,C" );
		m_maSlow = (MovingAverage)tape.addStudy ( "SMA", "25,C" );
		}

	public String getShortName() { return "Cross"; }

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{																			// if debugging
		int slow = m_maSlow.getValue(index);
		int fast = m_maFast.getValue(index);
		return "slow=" + slow + " fast=" + fast
					+ ((fast > slow) ? " +" : " -");
		}

	public void automata( int index, Bar bar )
		{
		int slow = m_maSlow.getValue(index);
		int fast = m_maFast.getValue(index);

		int newBias = fast - slow;
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