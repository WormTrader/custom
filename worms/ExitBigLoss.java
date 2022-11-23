package com.wormtrader.custom.worms;
/********************************************************************
* @(#)ExitBigLoss.java 1.00 20130503
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* ExitBigLoss: On every bar, checks the symbol's unrealized gain
* if it exceeds a specified amount, it closes the position.
*
* @author Rick Salamone
* @version 1.00
* 20130503 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;

public final class ExitBigLoss
	extends TapeWorm
	{
	public String getShortName() { return "BigLoss"; }

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{
		return "" + SBFormat.toDollarString(getLeg().getUnreal());
		}

	public void automata( int index, Bar bar )
		{
		if (getLeg().getUnreal() < -20000)
				getLeg().fireStop("< -200");
		}
	}