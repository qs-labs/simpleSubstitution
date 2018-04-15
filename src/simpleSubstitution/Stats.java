package simpleSubstitution;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by QSLabs on 4/14/2018.
 */

public class Stats {
	public int[] frequencies = new int[26];
	
    public void assess(ArrayList<Letter> arr, boolean type) // type 'false' = encryption, type 'true' = decryption
    {
    	Arrays.fill(frequencies, 0);
    	int index = 65;
    	int check;
        for(int i = 0; i < arr.size(); i++)
        {
        	if(type) {
        		check = arr.get(i).place + 65;
        	} else {
        		check = arr.get(i).character;
        	}
        
            boolean seen = false;

            while(!seen)
            {
		        if (check == index) {
		            frequencies[index - 65]++;
		            seen = true;
		        } else {
		        	index++;
		        }
			}
        }
    }
}