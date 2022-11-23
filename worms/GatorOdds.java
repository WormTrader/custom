package com.wormtrader.custom.worms;
/********************************************************************
* @(#)GatorOdds.java 1.00 20120916
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* GatorOdds:
*
* @author Rick Salamone
* @version 1.00
* 20120916 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarList;
import com.wormtrader.bars.BarSize;
import com.wormtrader.dao.TallyUpDown;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.Alligator;
import com.wormtrader.history.indicators.AO;
import com.wormtrader.history.indicators.Fractals;
import com.wormtrader.positions.TapeWorm;

public final class GatorOdds
	extends TapeWorm
	{
	private ProbabilitiesChecker fChecker;

	public void initTape(Tape tape) {}

	public String getShortName() { return "GatorOdds"; }

	public String getMetrics ( int index, Bar bar )
		{
		return (fChecker == null)? "warm up" : fChecker.details();
		}

	public void automata( int index, Bar bar )
		{
		if (fChecker == null)
			{
			Tape wormTape = getTape();
			fChecker = new ProbabilitiesChecker(wormTape.getSymbol(),
			            wormTape.getBarSize(), bar.getTime()-1);
			}

		int probabilityUp = fChecker.computeOdds(bar);
		if ( index < 8 )
			{
			if (index == 0) System.out.println("automata index 0");
			return;
			}
		int qty = getQty();

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

class ProbabilitiesChecker
	{
	private final Tape fHistory;
	private final Alligator fGator;
	private final AO fAO;
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
	public ProbabilitiesChecker(String aSymbol, BarSize aBarSize, long aTime)
		{
		fHistory = new Tape(aBarSize);
		fHistory.reset(aSymbol, new long[]{0, aTime-1});
		fGator = new Alligator(fHistory);
		fFractals = (Fractals)fHistory.addStudy(Fractals.STUDY_NAME);
		fAO = (AO)fHistory.addStudy(AO.STUDY_NAME);
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
		boolean[] barUp = new boolean[3];
		int aoPos = fAO.position(size-1);
		int aoTrend = fAO.trend(size-1);
		barUp[0] = fHistory.get(size-1).isUp();
		barUp[1] = fHistory.get(size-2).isUp();
		barUp[2] = fHistory.get(size-3).isUp();

		// look for the pattern - what we are looking for
		TallyUpDown runs = new TallyUpDown("");
		for (int i = 2; i < size-2; i++)
			{
			if (aoPos != fAO.position(i) || aoTrend != fAO.trend(i))
				continue;
			Bar b = fHistory.get(i);
			if (	barUp[0] == b.isUp()
			&&   barUp[1] == fHistory.get(i-1).isUp()
			&&   barUp[2] == fHistory.get(i-2).isUp())
				tallyHit(runs, b.getClose(), i);
			}
		fDetails = runs.toString();
//		System.out.println(aBar.hhmm() + fDetails);
		return runs.upPercent();
		}

	private void tallyHit(TallyUpDown runs, int priceNow, int indexM5)
		{
		BarList bars = fHistory.getBars();
		int nextUp = fFractals.nextUp(indexM5);
		int nextDn = fFractals.nextDown(indexM5);
		int runTo = (nextUp < nextDn)? bars.get(nextUp).getHigh()
		          : (nextDn < nextUp)? bars.get(nextDn).getLow()
		          :                    bars.get(nextUp).midpoint();
		runs.delta(runTo,priceNow);
		}
	}
