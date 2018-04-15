package simpleSubstitution;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by QSLabs on 4/14/2018.
 */

public class SimpleSubstitution {
    // Member variables
    public ArrayList<Letter> input = new ArrayList<Letter>();
    public String output;

    public static char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'}; // static alphabet array
    public static char[] reverseAlphabet = {'Z', 'Y', 'X', 'W', 'V', 'U', 'T', 'S', 'R', 'Q', 'P', 'O', 'N', 'M', 'L', 'K', 'J', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'}; // static reverse alphabet array

    public boolean direction = false; // 'false' indicates encryption, 'true' decryption

    private ArrayList<Character> cipherAlphabet;
  
    // Constructors
    public SimpleSubstitution(){

    }

    // constructor generating cipherAlphabet
    public SimpleSubstitution(String phrase){
        Set<Character> key_non_duplicate = new LinkedHashSet<Character>();
        phrase = phrase.toUpperCase();
        phrase = phrase.replaceAll("[^a-zA-Z]", "");
        
        for(int i = 0; i < phrase.length(); i++)
        {
            key_non_duplicate.add(phrase.charAt(i));
        }

        ArrayList<Character> key_array = new ArrayList<Character>();
        key_array.addAll(key_non_duplicate);

        for(int i = 0; i < reverseAlphabet.length; i++)
        {
            if(!key_array.contains(reverseAlphabet[i]))
            {
                key_array.add(reverseAlphabet[i]);
            }
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < key_array.size(); i++)
        {
            sb.append(key_array.get(i));
        }

        setCipherAlphabet(key_array);
    }

    // Methods
    public ArrayList<Character> getCipherAlphabet(){
        return cipherAlphabet;
    }

    public void setCipherAlphabet(ArrayList<Character> temp){
        cipherAlphabet = new ArrayList<Character>(temp);
    }

    // method that converts inpt to its respective encrypted/decrypted form; implements Letter and Stats classes, utilizes QuickSort and Partition functions; type 'false' = encryption, type 'true' = decryption
    public String translate(String inpt, boolean type){
    	ArrayList<Character> cipher = getCipherAlphabet();
        if(cipher == null){
            System.out.println("Cipher alphabet needs generated. Use the non-default constructor.");
            return "";
        }
        
        // reset class for new translation
        if(input != null){
            input.clear();
        }

        if(output != null){
            output = "";
        }

        direction = type;
        inpt = inpt.toString().toUpperCase();

        // Removing non-alphabetic characters from user input
        Pattern p = Pattern.compile("[^a-zA-Z]");
        if(p.matcher(inpt).find()){
            System.out.println("Non-alphabetic characters removed.");
            inpt = inpt.replaceAll("[^a-zA-Z]", "");
        }

        if(inpt.length() == 0){
            return "";
        }

        for(int i = 0; i < inpt.length(); i++)
        {
            int val = ((int) inpt.charAt(i));

            Letter letter = new Letter();
            letter.character = val;
            letter.index = i;
            letter.place = (-1); // sentinel value
            input.add(letter);
        }

        QuickSort(input, 0, input.size() - 1, 0);

        // input is now sorted according to ASCII value

        Stats stats = new Stats();
        stats.assess(input, false); // calculating frequency of each character in input
        
        // ENCRYPTION
        if(direction == false){
            int iter = 0;
            for(int i = 0; i < 26; i++) {
            	for(int j = 0; j < stats.frequencies[i]; j++) {
                    input.get(iter).character = cipher.get(i); // overwriting input character with encrypted character
                    iter++;
            	}
            	if(iter == input.size()) {
            		break;
            	}
            }

            QuickSort(input, 0, input.size()-1, 1);

            // input is now sorted according to index; encryption complete
        } else{ // DECRYPTION

            // setting place variable of Letter objects in input
            int iter = 0;
            for(int i = 0; i < 26; i++){
            	for(int j = 0; j < stats.frequencies[i]; j++) {
                    char placeholder = (char) input.get(iter).character; // converting int 'character' variable value back to char in order to search the cipherarray for that character
                    input.get(iter).place = cipher.indexOf(placeholder);
                    iter++;
                }
            	if(iter == input.size()) {
            		break;
            	}
            }

            QuickSort(input, 0, input.size() - 1, 2);

            // input is now sorted according to its equivalent cipherAlphabet characters' alphabetical order

            stats.assess(input, true); // calculating frequency of each cipherAlphabet character in input
            
            int iter2 = 0;
            for(int i = 0; i < 26; i++){
            	for(int j = 0; j < stats.frequencies[i]; j++) {
                    input.get(iter2).character = alphabet[i]; // overwriting input character with decrypted character
                    iter2++;
                }
                if(iter2 == input.size()){
                    break;
                }
            }
            
            QuickSort(input, 0, input.size() - 1, 1);
            
            // input is now sorted according to index; decryption complete
        }

        // Inserting spaces (between every 4 characters) into output for encryption standardization
        int remainder = input.size() / 4;

        char[] outptArray;
        if(input.size() % 4 == 0)
        {
            outptArray = new char[input.size() + (remainder - 1)];
        } else {
            outptArray = new char[input.size() + (remainder)];
        }

        int count = 0; // quantifies the number of spaces inserted into outptArray
        int input_Index = 0;
        for(int i = 0; i < outptArray.length; i++)
        {
            if(((i-count) % 4 == 0) && (i != 0))
            {
                outptArray[i] = ((char) 32);
                i++;
                count++;
            }

            outptArray[i] = ((char) input.get(input_Index).character);
            input_Index++;
        }

        output = new String(outptArray);
        return output;
    }

    void QuickSort(ArrayList<Letter> array, int lo, int hi, int type) // type '0' = character sort, type '1' = index sort, type '2' = place sort
    {
        if(lo >= hi)
        {
            return;
        }

        int i = Partition(array, lo, hi, type);
        if(i != 0)
        {
            QuickSort(array, lo, i-1, type);
        }

        if(i != hi)
        {
            QuickSort(array, i+1, hi, type);
        }
    }

    int Partition(ArrayList<Letter> arr, int lo, int hi, int type)
    {
        int r = hi;
        int l = lo;

        if(type == 0) // character partitioning
        {
            while(true)
            {
                while(arr.get(r).character > arr.get(lo).character)
                {
                    if(r == lo)
                    {
                        break;
                    }

                    r--;
                }

                while(arr.get(l).character <= arr.get(lo).character)
                {
                    if(l == hi)
                    {
                        l++;
                        break;
                    }

                    l++;
                }

                if(l > r)
                {
                    break;
                }

                Letter temp = arr.get(r);
                arr.set(r, arr.get(l));
                arr.set(l, temp);
            }

            Letter temp = arr.get(lo);
            arr.set(lo, arr.get(r));
            arr.set(r, temp);

            return r;
        } else if(type == 1) // index partitioning
        {
            while(true)
            {
                while(arr.get(r).index > arr.get(lo).index)
                {
                    if(r == lo)
                    {
                        break;
                    }

                    r--;
                }

                while(arr.get(l).index <= arr.get(lo).index)
                {
                    if(l == hi)
                    {
                        l++;
                        break;
                    }

                    l++;
                }

                if(l > r)
                {
                    break;
                }

                Letter temp = arr.get(r);
                arr.set(r, arr.get(l));
                arr.set(l, temp);
            }

            Letter temp = arr.get(lo);
            arr.set(lo, arr.get(r));
            arr.set(r, temp);

            return r;
        } else { // place partitioning
            while (true) {
                while (arr.get(r).place > arr.get(lo).place) {
                    if (r == lo) {
                        break;
                    }

                    r--;
                }

                while (arr.get(l).place <= arr.get(lo).place) {
                    if (l == hi) {
                        l++;
                        break;
                    }

                    l++;
                }

                if (l > r) {
                    break;
                }

                Letter temp = arr.get(r);
                arr.set(r, arr.get(l));
                arr.set(l, temp);
            }

            Letter temp = arr.get(lo);
            arr.set(lo, arr.get(r));
            arr.set(r, temp);

            return r;
        }
    }

}
