package com.wormtrader.custom.worms;
/*
 * StochasticWorm.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.Stochastic;
import com.wormtrader.positions.TapeWorm;

public final class SlowStochastic extends TapeWorm
	{
	private Stochastic m_stochasic;
	private int m_bias = 0;

	public void initTape(Tape tape)
		{
		m_stochasic = (Stochastic)tape.addStudy ( Stochastic.STUDY_NAME, "" );
		}

	public void automata( int index, Bar bar )
		{
		int k = m_stochasic.slowK(index);
		int d = m_stochasic.slowD(index);
		int newBias = k - d;
		if ( index < 3 ) return;

		if ( newBias * m_bias <= 0 ) // cross
			{
			if ( newBias > 0 ) goLong("StochX");
			else if ( newBias < 0 ) goShort("StochX");
			}
		m_bias = newBias;
		}

	public String  getMetrics ( int index, Bar bar )
		{
//		int k = m_stochasic.fastK(index);
//		int d = m_stochasic.fastD(index);
		int k = m_stochasic.slowK(index);
		int d = m_stochasic.slowD(index);
		return String.format ( "K %2d - D %2d = %3d", k, d, k - d );
		}
	}