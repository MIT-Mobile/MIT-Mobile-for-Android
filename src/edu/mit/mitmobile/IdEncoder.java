package edu.mit.mitmobile;

public class IdEncoder {
	
	public static String shortenId(int idValue) {
		short[] base62 = new short[10];
		int base62Index = 0;
		
		while(idValue > 0) {
			base62[base62Index] = (short) (idValue % 62);
			idValue = idValue/62;
			base62Index++;
		}
		
		int base62Length = base62Index;
		
		char[] chars = new char[base62Length];
		for(int i = 0; i < base62Length; i++) {
			short base62Digit = base62[base62Length-i-1];
			
			if(base62Digit < 26) {
				chars[i] = (char) (((short)'a') + base62Digit);
				continue;
			} 
			
			base62Digit -= 26;
			if(base62Digit < 26) {
				chars[i] = (char) (((short)'A') + base62Digit);
				continue;	
			} 
			
			base62Digit -= 26;
			chars[i] = (char) (((short)'0') + base62Digit);			
		}
		
		return new String(chars, 0, base62Length);			
	}

}
