package textprocessor;

import java.util.Comparator;
import java.util.Map;

class ValueComparator implements Comparator<String> { // this class is a custom comparator to sort the hashmap in decreasing order of the Value.
	 
    Map<String, Integer> map;
 
    public ValueComparator(Map<String, Integer> base) {
        this.map = base;
    }
 
    public int compare(String a, String b) { // sorts in descending order
        if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys 
    }
}
