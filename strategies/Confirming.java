package com.wormtrader.custom.strategies;
/*
 * SimStrategy.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.positions.PositionLeg;
import com.wormtrader.positions.Strategy;
import com.wormtrader.positions.TapeWorm;

public final class Confirming
	extends Strategy
	{
	@Override public final void goLong( TapeWorm worm, String why )
		{
//		if ( worm.getLeg().getQty() < 0 ) // we're short
//			goFlat( worm.getLeg(), "");
		for ( TapeWorm w : getWorms())
			if ( w.getBias() != TapeWorm.LONG_BIAS )
				return;
		goLong( worm.getLeg(), "Confirmed " + why);
		}

	@Override public final void goShort( TapeWorm worm, String why )
		{
//		if ( worm.getLeg().getQty() > 0 ) // we're short
//			goFlat( worm.getLeg(), "");
		for ( TapeWorm w : getWorms())
			if ( w.getBias() != TapeWorm.SHORT_BIAS )
				return;
		goShort( worm.getLeg(), "Confirmed " + why);
		}
	}