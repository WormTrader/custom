package com.wormtrader.custom.worms;
/********************************************************************
* @(#)LBR2PeriodROC.java 1.00 20120916
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* LBR2PeriodROC:
*
* @author Rick Salamone
* @version 1.00
* 20120916 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.positions.TapeWorm;

public final class LBR2PeriodROC
	extends TapeWorm
	{
	int close;  // current close
	int close1; // close of previous bar
	int close2; // close 2 bars ago
	String metrics = "";

	public LBR2PeriodROC()
		{
		super();
		setWarmUpPeriods( 3 );
		}

	public void initTape(Tape tape) {}

	public String getShortName() { return "LBR2ROC"; }

	public String getMetrics(int index, Bar bar) { return metrics; }

	public void automata( int index, Bar bar )
		{
		int roc2 = (close - close2);
		int pivot = close1 + roc2;
		close2 = close1;
		close1 = close;
		close = bar.getClose();
		if ( index < 3 ) return;
		metrics = "close: " + close
		     + " roc2: " + roc2
				+ " pivot: " + pivot;
		if (close > pivot) goLong(metrics);
		else if (close < pivot) goShort(metrics);
		else goFlat(metrics);
		metrics += " bias: " + getBiasString();
		}
	}
