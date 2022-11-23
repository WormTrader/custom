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
import com.wormtrader.history.indicators.MACD;
import com.wormtrader.positions.TapeWorm;

public final class StochasticWorm extends TapeWorm
	{
	private Stochastic m_stochasic;
	private MACD m_macd;
	int state = 0;

	public void initTape(Tape tape)
		{
		m_stochasic = (Stochastic)tape.addStudy ( Stochastic.STUDY_NAME, "" );
		m_macd = (MACD)tape.addStudy ( MACD.STUDY_NAME, "" );
		}

	public void automata( int index, Bar bar )
		{
		if ( index < 1 )
			return;
		int prev = index - 1;
		int thisMACD = m_macd.getMACD(index);
		int prevMACD = m_macd.getMACD(prev);

		if ( thisMACD > prevMACD )
			{
			if ( state == -1 )
				{ goFlat(); state = 0; }
			else if ( state == 0 )
				{
				if ( m_stochasic.isOversold(prev) && !m_stochasic.isOversold(index))
					{ goLong(); state = 1; }
				}
			}
		else if ( thisMACD < prevMACD )
			{
			if ( state == 1 )
				{ goFlat(); state = 0; }
			else if ( state == 0 )
				{
				if ( m_stochasic.isOverbought(prev) && !m_stochasic.isOverbought(index))
					 { goShort(); state = -1; }
				}
			}
		else
			{
			goFlat(); state = 0;
			}
/*
		if ( m_stochasic.isOversold(prev) && !m_stochasic.isOversold(index))
			goLong();
		if ( !m_stochasic.isOversold(prev) && m_stochasic.isOversold(index))
			goFlat();
		if ( m_stochasic.isOverbought(prev) && !m_stochasic.isOverbought(index))
			goShort();
		if ( !m_stochasic.isOverbought(prev) && m_stochasic.isOverbought(index))
			goFlat();
*/
		}
	}