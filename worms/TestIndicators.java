package com.wormtrader.custom.worms;
/*
 * TestIndicators.java
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.*;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;

public final class TestIndicators extends TapeWorm
	{
	ATR            atr;
	BollingerBands bb;
	MACD           macd;
	MovingAverage  ma;
	Momentum       mo;
	SAR            sar;

	public void initTape(Tape tape)
		{
	//	atr = (ATR)tape.addStudy(ATR.STUDY_NAME, "" );
	//	bb = (BollingerBands)tape.addStudy(BollingerBands.STUDY_NAME, "" );
	//	ma = (MovingAverage)tape.addStudy(SMA.STUDY_NAME, "25,C" );
	//	macd = (MACD)tape.addStudy(MACD.STUDY_NAME, "" );
	//	mo = (Momentum)tape.addStudy(Momentum.STUDY_NAME, "" );
		sar = (SAR)tape.addStudy (SAR.STUDY_NAME, "" );
		}

	public String getShortName() { return "Test"; }

	public String getMetrics(int index, Bar bar)
		{
		/******** ATR
		return String.format("atr=%5d", atr.getValue(index));
		*********/
		/******** BollingerBands
		return = String.format("%3d) low: %5d up: %5d range: %d - %d",
				index, bb.getLowerBand(index), bb.getUpperBand(index),
							bb.getRangeMinimum(), bb.getRangeMaximum());
		*********/
		/******** MACD
		return String.format("%3d) macd: %5d sig: %5d histo: %5d range: %d - %d",
					index, macd.getMACD(index), macd.getSignal(index),
						macd.getHistogram(index),
							macd.getRangeMinimum(), macd.getRangeMaximum());
		*********/
		/******** Momentum
		return = String.format("%3d) mo: %5d range: %d - %d",
				index, mo.getValue(index),
							mo.getRangeMinimum(), mo.getRangeMaximum());
		*********/
		/******** Moving Average
		int atr = m_atr.getValue(index);
		return String.format("ma=%5d", ma.getValue(index));
		*********/
		/******** SAR
		return = String.format("%3d) mo: %5d range: %d - %d",
				index, sar.getValue(index),
							sar.getRangeMinimum(), sar.getRangeMaximum());
*/
return sar.debug;
		}

	public void automata( int index, Bar bar ) {}
	}