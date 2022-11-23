package com.wormtrader.custom.worms;
/*
 * RideWave.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;

public final class RideWave extends TapeWorm
	{
	public RideWave()
		{
		super();
		setWarmUpPeriods( 22 ); // 25
		}
	public BarSize getMinBarSize() { return BarSize.ONE_DAY; }
	public String getShortName() { return "VVA"; }

	public void automata( int index, Bar bar )
		{
		}
	}