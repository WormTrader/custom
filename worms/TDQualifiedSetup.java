package com.wormtrader.custom.worms;
/****************************************
*
* TDQualifiedSetup.java
*
*
* "In order to record a completed buy setup, the setup qualifier
*  requires that the low of the 7, 8, or 9 price bar of a buy setup be below
*  the low of the sixth price bar.
*   ...
*  Conversely, in order to record a complete sell setup, the setup qualifier
*  requires that the high of the 7th, 8th, or 9th price bar must be above the
*  high of the sixth bar."
*
******/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.Tape;
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.positions.TapeWorm;

public final class TDQualifiedSetup extends TDSetup
	{
	public String getShortName() { return "TDQSet"; }

/**********
	public void initTape(Tape tape)
		{
		super.initTape(tape);
		}
**********/

	public void automata( int index, Bar bar )
		{
		if ( index < 4 )
			return;
		super.automata( index, bar );

		if (( m_setupCount <= -9 ) && ( getQty() <= 0 ))
			{
			int lo6 = lookback(index, 3).getLow();
			for ( int i = 0; i < 3; i++ )
				if ( lookback(index, i).getLow() < lo6 )
					{
					goLong(); // it's qualified
					return;
					}
			}
		else if (( m_setupCount >= 9 ) && ( getQty() >= 0 ))
			{
			int hi6 = lookback(index, 3).getHigh();
			for ( int i = 0; i < 3; i++ )
				if ( lookback(index, i).getHigh() > hi6 )
					{
					goShort(); // it's qualified
					return;
					}
			}
		}
	}