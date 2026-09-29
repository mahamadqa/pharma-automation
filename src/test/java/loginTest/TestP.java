package loginTest;

import java.util.HashMap;
import java.util.Map;

public class TestP {

	public static void main(String[] args) {

		String s = "java";
		
		Map<Character, Integer> map = new HashMap();
		
		for(int i=0; i<s.length(); i++) {
			
			if(map.containsKey(s.charAt(i))) {
				map.put(s.charAt(i), map.get(s.charAt(i)) +1);
			}
			else {
				map.put(s.charAt(i), 1);
			}
		}
		System.out.println(map);
		

		/*
		 * for (int i = 0; i < s.length(); i++) {
		 * 
		 * 
		 * boolean alredyChecked = false;
		 * 
		 * for (int j = 0; j < i; j++) { if (s.charAt(i) == s.charAt(j)) { alredyChecked
		 * = true; break; } }
		 * 
		 * if (!alredyChecked) { int count = 0; for (int k = i; k < s.length(); k++) {
		 * 
		 * if (s.charAt(i) == s.charAt(k)) { count++; }
		 * 
		 * } if(count > 1) { System.out.println(s.charAt(i) + " : " +count);} } }
		 */

		
		
	}
}
