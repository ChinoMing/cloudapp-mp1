import java.io.File;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;
    
public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];
        
        /** 
         * 1.Divide each sentence into a list of words using delimiters 
         * provided in the “delimiters” variable.
         */       
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName), "UTF-8"))) {
            String line;
            String UTF8_BOM = "\uFEFF";
            List<String> stopWordsList = Arrays.asList(stopWordsArray);
            Integer[] indexes = getIndexes();
            String[] lines = new String[50000];
        
            Map<String,Integer> hashMap = new HashMap<String,Integer>();
            ValueComparator bvc = new ValueComparator(hashMap);
            TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
            
            boolean firstLine = true;
            int count = 0;
            
            while ((line = br.readLine()) != null) {
                //Handle BOM issue
                if (firstLine) {
                    if (line.startsWith(UTF8_BOM)) {
                        line = line.substring(1);
                    }
                    firstLine = false;
                }
            /**/    
                lines[count] = line;
                count++;
            }
            
            for(int j = 0 ;j<indexes.length; j++){
                line = lines[indexes[j]];
            /**/    
                
                //System.out.println("Line = "+line);
                StringTokenizer st = new StringTokenizer(line, delimiters);
                while (st.hasMoreTokens()) {
                    /**
                     * 2. Make all the tokens lowercase and remove any tailing 
                     * and leading spaces
                     */
                    String output = st.nextToken();
                    output = output.toLowerCase();
                    output = output.trim();
                    //System.out.println("Output = "+output);
                
                    /**
                     * 3. Ignore all common words provided in the 
                     * “stopWordsArray” variable.
                     */
                    if (!stopWordsList.contains(output)){
                        /**
                         * 4. Keep track of word frequencies
                         */
                        //System.out.println("hash map: " + hashMap);
                        if(hashMap.containsKey(output)){ //add to key
                            //System.out.println("Output = "+output);
                            int i = hashMap.get(output) + 1;
                            //System.out.println("i = "+i);
                            hashMap.put(output,i);
                            //System.out.println("hash map2: " + hashMap);
                        }
                        else { //create key
                            hashMap.put(output,1);
                        }
                    }
                }
            }
            
            /**
             * 5. Sort the list by frequency in a descending order. 
             * If two words have the same number count, use the lexigraphy
             */
            //System.out.println("unsorted map: " + hashMap);
            sorted_map.putAll(hashMap);
            //System.out.println("results: " + sorted_map);
            
            /**
             * 6. Return the top 20 items from the sorted list as a String Array.
             */
            ret = sorted_map.keySet().toArray(new String[0]);
            ret = Arrays.copyOfRange(ret, 0, 20);
        }
        
        return ret;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}

class ValueComparator implements Comparator {
    Map base;

    public ValueComparator(Map base) {
        this.base = base;
    }

    public int compare(Object a1, Object b1) {
        String a = (String) a1;
        String b = (String) b1;
        
        if ((Integer)base.get(a) > (Integer)base.get(b)) {
            return -1;
        }
        else if ((Integer)base.get(a) == (Integer)base.get(b)) {
            if(a.compareTo(b)<0){
                return -1;
            }
            else {
                return 1;
            }
        }
        else {
            return 1;
        } // returning 0 would merge keys
    }
}