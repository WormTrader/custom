package com.wormtrader.custom.worms;
/*
 * MA3.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.MovingAverage;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;

public final class MA3 extends TapeWorm
	{
	private MovingAverage m_ma10; // fast
	private MovingAverage m_ma35; // medium
	private MovingAverage m_ma50; // slow

	public MA3()
		{
		super();
		setWarmUpPeriods( 20 ); // 25
		}

	public void initTape(Tape tape)
		{
		m_ma10 = (MovingAverage)tape.addStudy ( "EMA", "10,C" );
		m_ma35 = (MovingAverage)tape.addStudy ( "EMA", "35,C" );
		m_ma50 = (MovingAverage)tape.addStudy ( "EMA", "50,C" );
		}

	public String getShortName() { return "Cross"; }

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{																			// if debugging
		int slow = m_ma50.getValue(index);
		int fast = m_ma10.getValue(index);
		return "slow=" + slow + " fast=" + fast
					+ ((fast > slow) ? " +" : " -");
		}

	public void automata( int index, Bar bar )
		{
		int _50 = m_ma50.getValue(index);
		int _35  = m_ma35.getValue(index);
		int _10 = m_ma10.getValue(index);
		int c = bar.getClose();

		if ( c > _50 )
			{
			if (( _10 > _35 ) && ( _10 > _50 ) && ( getQty() <= 0 ))
				goLong();
			else if (( _10 < _35 ) && ( getQty() != 0 ))
				goFlat();
			}
		else if ( c < _50 )
			{
			if (( _10 < _35 ) && ( _10 < _50 ) && ( getQty() >= 0 ))
				goShort();
			else if (( _10 > _35 ) && ( getQty() != 0 ))
				goFlat();
			}
		}
	}