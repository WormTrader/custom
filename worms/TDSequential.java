package com.wormtrader.custom.worms;
/*
 * TDSequential.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.Tape;
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.positions.TapeWorm;

public final class TDSequential extends TDSetup
	{
	public String getShortName() { return "TDSeq"; }

/**********
	public void initTape(Tape tape)
		{
		super.initTape(tape);
		}
**********/

	private int m_8bar = 0; // bar 8's low/high in case of buy/sell countdown
	public void automata( int index, Bar bar )
		{
		if ( index < 4 )
			return;
		super.automata( index, bar );

		if ( m_phase == BUY_COUNTDOWN )
			{
//			if (( close <= lookback(index, 2).getLow())
			if ( bar.getLow() <= lookback(index, 2).getLow())
				{
				if ( --m_countdownCount == -8 ) // to qualify countdown complete, low
					m_8bar = bar.getLow();        // of final bar must be < low of bar 8
				else if (( m_countdownCount <= -13 ) // contdown complete
	//			     &&  ( bar.getLow() < m_8bar )   // qualifier satisfied
				     &&  ( getQty() <= 0 ))          // not already long
					goLong();
				}
			}
		else if ( m_phase == SELL_COUNTDOWN )
			{
//			if (( close >= lookback(index, 2).getHigh())
			if ( bar.getHigh() >= lookback(index, 2).getHigh())
				{
				if ( ++m_countdownCount == 8 ) // to qualify countdown complete, high
					m_8bar = bar.getHigh();      // of final bar must be > high of bar 8
				else if (( m_countdownCount >= 13 )  // contdown complete
	//			     &&  ( bar.getHigh() > m_8bar )  // qualifier satisfied
				     &&  ( getQty() >= 0 ))          // not already short
					goShort();
				}
			}
		}
	}