package edu.mit.mitmobile2;

import java.util.ArrayList;
import java.util.List;

public class NaturalSort {

	private static int NUMBER_PRIORITY = 0;
	private static int LETTER_PRIORITY = 1;
	
	static final int ZERO_CODE_POINT = "0".codePointAt(0);
	static final int NINE_CODE_POINT = "9".codePointAt(0);
	
	public static int compare(String first, String second) {
		// first decompose into units
		
		List<SingleUnit> firstUnits = units(first);
		List<SingleUnit> secondUnits = units(second);
		
		// now compare each string unit by unit
		
		int length = Math.min(firstUnits.size(), secondUnits.size());
		
		for(int index = 0; index < length; index++) {
			SingleUnit aUnit = firstUnits.get(index);
			SingleUnit bUnit = secondUnits.get(index);
			
			if(aUnit.typePriority() != bUnit.typePriority()) {
				return aUnit.typePriority() - bUnit.typePriority();
			}
			
			if(aUnit.compare(bUnit) != 0) {
				return aUnit.compare(bUnit);
			}
		}
		
		// all the letters compared so far are the same, use the length to order
		return firstUnits.size() - secondUnits.size();
	}
	
	private static List<SingleUnit> units(String text) {
		ArrayList<SingleUnit> units = new ArrayList<SingleUnit>();
		
		while(text.length() > 0) {
			SingleUnit unit = NumberUnit.extractUnit(text);
			if(unit == null) {
				unit = LetterUnit.extractUnit(text);
			}
			
			units.add(unit);
			
			text = text.substring(unit.chars());
		}
		
		return units;
	}
	
	private abstract static class SingleUnit {
		public abstract int typePriority();
		
		// this return negative, zero, or positive number to do the comparison
		public abstract int compare(SingleUnit other);
		
		// this returns the number of characters this unit represents
		public abstract int chars();
	}
	
	private static class LetterUnit extends SingleUnit {
		int mCodePoint;
		
		private LetterUnit(int codePoint) {
			mCodePoint = codePoint;
		}
		
		public static SingleUnit extractUnit(String text) {
			return new LetterUnit(text.codePointAt(0));
		}
		
		@Override
		public int compare(SingleUnit other) {
			LetterUnit otherLetter = (LetterUnit) other;
			return (mCodePoint - otherLetter.mCodePoint);
		}

		@Override
		public int typePriority() {
			return LETTER_PRIORITY;
		}

		@Override
		public int chars() {
			return 1;
		}
	}
	
	private static class NumberUnit extends SingleUnit {
		int mValue = 0;
		
		// keep track of the number of digits in case we have leading zeros
		// and want to compare the value of say 001 to 1
		int mDigits = 0;
		
		public void addCodePoint(int codePoint) {
			mDigits++;
			mValue = mValue * 10 + (codePoint - ZERO_CODE_POINT);
		}

		@Override
		public int compare(SingleUnit other) {
			NumberUnit otherNumber = (NumberUnit) other;
			if(mValue != otherNumber.mValue) {
				return (mValue - otherNumber.mValue);
			}
			
			// this is so numbers with leading zeros come first
			return (otherNumber.mDigits - mDigits);
		}

		@Override
		public int typePriority() {
			return NUMBER_PRIORITY;
		}

		@Override
		public int chars() {
			return mDigits;
		}

		public static SingleUnit extractUnit(String text) {
			NumberUnit numberUnit = new NumberUnit();
			
			for(int index=0; index < text.length(); index++) {				
				int codePoint = text.codePointAt(index);
				
				if(codePoint >= ZERO_CODE_POINT && codePoint <= NINE_CODE_POINT) {
					numberUnit.addCodePoint(codePoint);
				} else {
					break;
				}
			}
			
			if(numberUnit.mDigits > 0) {
				return numberUnit;
			} else {
				return null;
			}
		}
	}
}
