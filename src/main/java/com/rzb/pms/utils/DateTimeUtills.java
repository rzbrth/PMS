package com.rzb.pms.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

public class DateTimeUtills {

	public static Object[] getArrayOFDate(String dayRef) {

		LocalDate today = LocalDate.now();
		LocalDate rangeOne = null, rangeTwo = null;

		try {

			switch (dayRef.toUpperCase()) {

			case "TODAY": {
				rangeOne = today;
				return new Object[] { rangeOne};
			}
			case "YESTERDAY": {
				rangeOne = today.minusDays(1);
				rangeTwo = today;
				return new Object[] { rangeOne, rangeTwo };
			}
			case "LAST_30_DAYS": {
				rangeOne = today.minusDays(30);
				rangeTwo = today;
				return new Object[] { rangeOne, rangeTwo };

			}
			case "LAST_7_DAYS": {
				rangeOne = today.minusDays(7);
				rangeTwo = today;
				return new Object[] { rangeOne, rangeTwo };
			}
			case "LAST_MONTH": {
				rangeOne = today.minusMonths(1);
				rangeTwo = today;
				return new Object[] { rangeOne, rangeTwo };
			}
			case "THIS_MONTH": {
				rangeOne = YearMonth.now().atDay(1);
				rangeTwo = today;
				return new Object[] { rangeOne, rangeTwo };
			}
			case "THIS_YEAR": {
				rangeOne = today.with(TemporalAdjusters.firstDayOfYear());
				rangeTwo = today;
				return new Object[] { rangeOne, rangeTwo };
			}
			}

		} catch (Exception e) {

		}

		return new Object[] { rangeOne, rangeTwo };
	}

}
