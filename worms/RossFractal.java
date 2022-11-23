package com.wormtrader.custom.worms;
/********************************************************************
* @(#)RossFractal.java 1.00 20120911
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* RossFractal:
*
* @author Rick Salamone
* @version 1.00
* 20120911 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.Alligator;
import com.wormtrader.history.indicators.Fractals;
import static com.wormtrader.history.indicators.Fractals.IS_UP;
import static com.wormtrader.history.indicators.Fractals.IS_DOWN;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;

public final class RossFractal
	extends TapeWorm
	{
	private Alligator fGator;
	private Fractals  fFractals;

	public RossFractal()
		{
		super();
		setWarmUpPeriods( 8 );
		}

	public void initTape(Tape tape)
		{
		fGator = new Alligator(tape);
		fFractals = (Fractals)tape.addStudy(Fractals.STUDY_NAME);
		}

	public String getShortName() { return "RossFractal"; }

	public String getMetrics ( int index, Bar bar )
		{
		return "bias: " + fGator.bias(index)
				+ " pos: " + fGator.barPosition(index);
		}

	int[] fPrices = new int[6];
	char[] fPattern = new char[6];
	public void automata( int index, Bar bar )
		{
		if ( index < 8 ) return;
		int qty = getQty();
		int rangeHi = 0;
		int rangeLo = 0;

		Tape tape = getTape();
		int i = 0;
		for (int x = index-2; x >= 0; x--)
			{
			byte flags = fFractals.getValue(x);
			if (flags == 0) continue;
			if ((flags & IS_UP) != 0)
				{
				int price = tape.get(x).getHigh();
				if ( i>0 && fPattern[i-1] == 'U') // take higher price
					price = Math.max(price, fPrices[--i]); // decrement i!!
				fPrices[i] = price;
				fPattern[i] = 'U';
				if (++i >= fPrices.length) break;
				}
			if ((flags & IS_DOWN) != 0)
				{
				int price = tape.get(x).getLow();
				if ( i>0 && fPattern[i-1] == 'D')
					price = Math.min(price, fPrices[--i]); // decrement i!!
				fPrices[i] = price;
				fPattern[i] = 'D';
				if (++i >= fPrices.length) break;
				}
			}
		String anal = analysis(i);
		System.out.println(bar.hhmm() + " " + anal);
		while( --i >= 0 )
			System.out.print(" " + fPattern[i] + " " + SBFormat.toDollarString(fPrices[i]));
		System.out.println();
if (qty != 0)
	goFlat(anal);
else if (anal.charAt(0)=='+') goLong(anal);
else if (anal.charAt(0)=='-') goShort(anal);
		}

	private String analysis(int num)
		{
		if (matches("UDU"))
			{
			if ( fPrices[0] < fPrices[2] && fPrices[1] < fPrices[0] )
				return "-123 high, sell " + SBFormat.toDollarString(fPrices[1]);
			if ( fPrices[0] > fPrices[2] && fPrices[1] < fPrices[2] )
				return "+up trend RH buy " + SBFormat.toDollarString(fPrices[0]);
			if ( fPrices[0] == fPrices[2] && fPrices[1] < fPrices[2] )
				return "-double top";
			return "UDU" + " huh?";
			}
		else if (matches("DUD"))
			{
			if ( fPrices[0] > fPrices[2] && fPrices[1] > fPrices[0] )
				return "+123 low, buy " + SBFormat.toDollarString(fPrices[1]);
			if ( fPrices[0] < fPrices[2] && fPrices[1] > fPrices[2] )
				return "-down trend RH sell " + SBFormat.toDollarString(fPrices[0]);
			if ( fPrices[0] == fPrices[2] && fPrices[1] > fPrices[2] )
				return "+double bottom";
			return "DUD" + " huh?";
			}
		return "No match: " + fPattern[0] + fPattern[1] + fPattern[2];
		}

	private boolean matches(String aString)
		{
		for ( int i = 0; i < aString.length(); i++)
			if (aString.charAt(i) != fPattern[i])
				return false;
		return true;
		}
	}
