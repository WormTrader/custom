package com.wormtrader.custom.worms;
/*
 * TDCombo.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.Tape;
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.positions.TapeWorm;

public final class TDCombo extends TDSetup
	{
	public static final int BUY_SETUP_COMPLETE=9;
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
			if ( m_setupCount == BUY_SETUP_COMPLETE )
				{
				
				}
			int close = bar.getClose();
			if (( close        <= lookback(index, 2).getLow())
			&&  ( bar.getLow() <  lookback(index, 1).getLow())
//			&&  ( close        <  m_prevCountdownClose )
)
				{
//				m_prevCountdownClose = bar.getClose();
				if (( --m_countdownCount == -13 ) && ( getQty() <= 0 ))
					goLong();
				}
			}
		else if ( m_phase == SELL_COUNTDOWN )
			{
			int close = bar.getClose();
			if (( bar.getClose() >= lookback(index, 2).getHigh())
			&&  ( bar.getHigh()  >  lookback(index, 1).getHigh())
//			&&  ( close > m_prevCountdownClose )
)
				{
//				m_prevCountdownClose = bar.getClose();
				if (( ++m_countdownCount >= 13 ) &&  ( getQty() >= 0 ))
					goShort();
				}
			}
		}
	}