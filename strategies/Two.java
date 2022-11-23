package com.wormtrader.custom.strategies;
/*
 * SimStrategy.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.positions.Strategy;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.ui.SBDialog;

public final class Two extends Strategy
	{
	TapeWorm m_trendWorm = null;
	int bias = 0;
	public void addLeg ( PositionLeg leg )
		{
		super.addLeg(leg);
		m_trendWorm = addWorm( "CrossOver", 0, BarSize.ONE_DAY );
		}

	@Override public final void goLong( TapeWorm worm, String why )
		{
		if ( worm == m_trendWorm )
			{
log ( "TREND UP" );
			bias = 1;
			if ( worm.getLeg().getQty() < 0 ) // we're short
				goFlat( worm.getLeg(), "up trend");
			}
		else if ( bias >= 1 ) goLong( worm.getLeg(), "Trend & " + why);
		}

	@Override public final void goShort( TapeWorm worm, String why )
		{
		if ( worm == m_trendWorm )
			{
log ( "TREND DOWN" );
			bias = -1;
			if ( worm.getLeg().getQty() < 0 ) // we're long
				goFlat( worm.getLeg(), "down trend");
			}
		else if ( bias <= -1 ) goShort( worm.getLeg(), "Trend & " + why);
		}

	public String getSoundFile() { return "cash_register"; }
	} // 365