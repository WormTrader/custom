package com.wormtrader.custom.worms;
/*
 * RevTwoUp.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.positions.TapeWorm;

public final class RevTwoUp extends TapeWorm
	{
	public RevTwoUp()
		{
		super();
		setWarmUpPeriods( 3 );
		}

	public String getShortName() { return "RevTwoUp"; }

	public void automata( int index, Bar bar )
		{
		if ( index < 1 ) return;

		if (( getBias() <= NEUTRAL_BIAS ) // if we're short or flat, check for a buy signal
		&&  ( bar.getClose() > bar.getOpen()))    // an up bar
			{
			bar = getTape().get(index-1);       // check previous bar
			if ( bar.getClose() > bar.getOpen())   // twice in a row -> BUY
				goShort();
			}

		else if (( getBias() >= NEUTRAL_BIAS ) // if flat or long, check for a sell signal
		&& ( bar.getClose() < bar.getOpen()))    // a down bar
			{
			bar = getTape().get(index-1);       // check previous bar
			if ( bar.getClose() < bar.getOpen())   // twice in a row -> SELL
				goLong();
			}
		}
	}