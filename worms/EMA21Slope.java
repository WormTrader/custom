package com.wormtrader.custom.worms;
/*
 * Seykota.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.MovingAverage;
import com.wormtrader.positions.TapeWorm;

public final class EMA21Slope extends TapeWorm
	{
	public final String MA_PARAMS="21,C";
	private static final int PERIOD = 2;
	private MovingAverage m_ma;

	public EMA21Slope()
		{
		super();
		setWarmUpPeriods( 10 ); // 25
		}

	public void initTape(Tape tape)
		{
		m_ma = (MovingAverage)tape.addStudy ( "EMA",  MA_PARAMS );
		}

	public String getShortName() { return "EMA21"; }
	public String getMetrics ( int index, Bar bar )
		{
		if ( index < PERIOD ) return "";
		int now = m_ma.getValue(index);
		int delta = now - m_ma.getValue(index-PERIOD);
		return String.format("EMA(%s) %5d delta %5d", MA_PARAMS, now, delta );
		}

	public void automata( int index, Bar bar )
		{
		if ( index < PERIOD ) return;
		int now = m_ma.getValue(index);
		int delta = now - m_ma.getValue(index-PERIOD);

		if (( getBias() < LONG_BIAS ) && ( delta > 0 ))
			goLong();
		else if (( getBias() > SHORT_BIAS ) && ( delta < 0 ))
			goShort();
		}
	}