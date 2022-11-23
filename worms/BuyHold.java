package com.wormtrader.custom.worms;
/*
 * Strategy.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.positions.TapeWorm;

public final class BuyHold extends TapeWorm
	{
	public String getShortName() { return "Hold"; }
	public void automata( int index, Bar bar )
		{
		}
	}