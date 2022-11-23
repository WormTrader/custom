package com.wormtrader.custom.worms;
/********************************************************************
* @(#)BWLevelI.java 1.01 20120725
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* BWLevelI: Implements Bill Williams Ideas from "Trading Chaos" as a Level I
* trader. See page 96ff.
* 
* @author Rick Salamone
* @version 1.0
* 20120725 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.*;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;

public final class BWLevelI
	extends TapeWorm
	{
	private MFI  fMFI;

	public BWLevelI()
		{
		super();
		setWarmUpPeriods(3);
		}

	public void initTape(Tape tape)
		{
		fMFI = (MFI)tape.addStudy(MFI.STUDY_NAME, "" );
		}

	public String getShortName() { return "BW Level I"; }

	public String getMetrics(int index, Bar bar)
		{
		return MFI.STUDY_NAME + "(" + index + "): " + fMFI.getToolTipText(index);
		}

	private static final String[] barTypeDescs =
		{
		"BB 33: Gravestone",
		"BM 32: Climber",
		"BT 31: Climber",
		"MB 23: Drifter",
		"MM 22: Indecision",
		"MT 21: Climber",
		"TB 13: Drifter",
		"TM 12: Drifter",
		"TT 11: Hammer"
		};

	private final int barType(Bar bar)
		{
		int range3 = bar.range()/3;
		int open = bar.getOpen();
		int close = bar.getClose();
		if (range3 == 0)
 			range3=1; 
		int low = bar.getLow();
		if (open < low + 2*range3)
			{
			return (close < (low + range3))?   3
			     : (close < (low + 2*range3))? 4
			     :                             5;
			}
		if (open < low + range3)
			{
			return (close < (low + range3))?   0
			     : (close < (low + 2*range3))? 1
			     :                             2;
			}
		else
			{
			return (close < (low + range3))?   6
			     : (close < (low + 2*range3))? 7
			     :                             8;
			}
		}

	public void automata( int index, Bar bar )
		{
		if ( index < 1 )
			return;
		int type = barType(bar);
		int midpoint = bar.midpoint();
		Bar prev = getTape().get(index-1);
		char iTrend = (midpoint > prev.getHigh())? '+'
		            : (midpoint < prev.getLow())? '-'
		            : '0';
		alert( "" + iTrend + barTypeDescs[type] + fMFI.getToolTipText(index)
		       + "(Trend, Type, MFI)");
		}
	}