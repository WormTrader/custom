package com.wormtrader.custom.scans;

import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;

public class FontanillsMove extends Scanner
	{
	private int m_minPrice = 700; // $7
	@Override	public String toString()
		{ return "Fontanills Up or Down 20% on 300K, or 30% on 1000K"; }

	@Override public boolean isHit(Tape tape)
		{
		int index = tape.size() - 1;
		Bar bar = tape.get(index);
		int todayClose = bar.getClose();
		if ( todayClose < m_minPrice )
			return false;
		long volume = bar.getVolume();
		if ( volume < 3000 ) // only interested in >= 300,000 shares
			return false;
		int yestClose = tape.get(--index).getClose();
		int percentMove = 100 * ( todayClose - yestClose ) / yestClose;
		if ( percentMove < 0 ) percentMove = -percentMove;
		if ( percentMove < 20 )
			return false;

		return ( percentMove >= 30 )    // up 30% on at least 300,000 volume
		    || ( volume >= 10000 );     // or up 20% on at least a million volume
		}
	}