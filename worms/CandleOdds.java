package com.wormtrader.custom.worms;
/********************************************************************
* @(#)CandleOdds.java 1.00 20120916
* Copyright © 2012-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* CandleOdds: As each bar arrives, a pattern is set up with the height and
* travel of the last three bars. Then we go back and look for matches in
* this symbol's history. The hits are compiled with TallyUpDn...
*
* @author Rick Salamone
* @version 1.00
* 20120916 rts created
* 20104217 rts removed gator stuff & documented
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.bars.BarList;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.indicators.Candle;
import com.wormtrader.history.indicators.Fractals;
import com.wormtrader.dao.TallyUpDown;

public final class CandleOdds
	extends TapeWorm
	{
	private ProbabilitiesCheckerTwo fChecker;

	public String getShortName() { return "CandleOdds"; }

	public String getMetrics ( int index, Bar bar )
		{
		return (fChecker == null)? "warm up" : fChecker.details();
		}

	public void automata( int index, Bar bar )
		{
		if (fChecker == null)
			{
			Tape wormTape = getTape();
			fChecker = new ProbabilitiesCheckerTwo(wormTape.getSymbol(), wormTape.getBarSize(),
	                                      bar.getTime()-1);
			}
		int probabilityUp = fChecker.computeOdds(bar);
		if ( index < 8 )
			{
			if (index == 0) System.out.println("automata index 0");
			return;
			}
		int qty = getQty();

//		if (probabilityUp > 60) goLong(fChecker.details());
//		else if (probabilityUp <= 40) goShort(fChecker.details());
 if (qty != 0) goFlat(fChecker.details());
		else if (qty == 0)
			{
			if (probabilityUp > 50) goLong(fChecker.details());
			else if (probabilityUp <= 50) goShort(fChecker.details());
			return;
			}
		else if (qty < 0 && probabilityUp >= 60) goLong(fChecker.details());
		else if (qty > 0 && probabilityUp <= 40) goLong(fChecker.details());
		else goFlat(fChecker.details());
		}
	}

class ProbabilitiesCheckerTwo
	{
	private final Tape fHistory;
	private final Candle fCandle;
	private final Fractals fFractals;
	String fDetails;

	public final String details() { return fDetails; }
	/**
	* ctor thaws the bars that will be sent to the query and creates a
	* a Fractals object to be used for checking the runs on the queries
	* hits.
	* @param String aSymbol - so we can grab history bars
	* @param long[] aDateRange - which bars to initial grab
	*/
	public ProbabilitiesCheckerTwo(String aSymbol, BarSize aBarSize, long aTime)
		{
		fHistory = new Tape(aBarSize);
		fHistory.reset(aSymbol, new long[]{0, aTime-1});
		fCandle = (Candle)fHistory.addStudy(Candle.STUDY_NAME);
		fFractals = (Fractals)fHistory.addStudy(Fractals.STUDY_NAME);
		}

	/**
	* computeOdds uses the characteritics of the current bar to set up a
	* query that will look for similar bars from the past. The results of
	* running the query are tallied up and returned as a "probability"
	* Also, the bar is added to the history used for checking subsequent bars
	* @return an int from 0 - 100 that measures the odds of have a positive
	*         run following this bar
	*/
	public int computeOdds(Bar aBar)
		{
		fHistory.realtimeBar(aBar);
		int size = fHistory.size();
		if (size < 3) return 0;

		// set up the pattern we are looking for - i.e. the current state
		byte[] height = new byte[3];
		height[0] = fCandle.height(size-1);
		height[1] = fCandle.height(size-2);
		height[2] = fCandle.height(size-3);

		int[] travel = new int[3];
		travel[0] = fCandle.travel(size-1);
		travel[1] = fCandle.travel(size-2);
		travel[2] = fCandle.travel(size-3);
	/******
		System.out.format("pattern\n %02d %7s, %02d %7s, %02d %7s\n",
			travel[0], Candle.nameFor(height[0]),
			travel[1], Candle.nameFor(height[1]),
			travel[2], Candle.nameFor(height[2]));
	******/

		// look for the pattern - what we are looking for
		TallyUpDown runs = new TallyUpDown("");
		for (int i = 2; i < size-2; i++)
			if (	fCandle.isMatch(i,   height[0], travel[0])
			&&   fCandle.isMatch(i-1, height[1], travel[1]))
//			&&   fCandle.isMatch(i-2, height[2], travel[2]))
				tallyHit(runs, fHistory.get(i).getClose(), i);

		fDetails = runs.toString();
//		System.out.println(aBar.hhmm() + fDetails);
		return runs.upPercent();
		}

	private void tallyHit(TallyUpDown runs, int priceNow, int indexM5)
		{
		BarList barsM5 = fHistory.getBars();
		int nextUp = fFractals.nextUp(indexM5);
		int nextDn = fFractals.nextDown(indexM5);
		int runTo = (nextUp < nextDn)? barsM5.get(nextUp).getHigh()
		          : (nextDn < nextUp)? barsM5.get(nextDn).getLow()
		          :                    barsM5.get(nextUp).midpoint();
		runs.delta(runTo,priceNow);
		}
	}
