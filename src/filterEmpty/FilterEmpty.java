package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import cse332.exceptions.NotYetImplementedException;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();

    public static int[] filterEmpty(String[] arr) {
        int[] bitset = mapToBitSet(arr);
        //System.out.println(java.util.Arrays.toString(bitset));
        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bitset);
        //System.out.println(java.util.Arrays.toString(bitsum));
        int[] result = mapToOutput(arr, bitsum);
        return result;
    }

    public static int[] mapToBitSet(String[] arr) {
        int[] bits = new int[arr.length];
        for(int i = 0; i < arr.length; i++) {
        	if(arr[i].length() > 0) {
        		bits[i] = 1;
        	}
        }
        return ParallelPrefixSum.parallelPrefixSum(bits);
    }

    
    public static int[] mapToOutput(String[] input, int[] bitsum) {
    	int last = bitsum[bitsum.length - 1];
        int[] output = new int[last];1111
        int j = 0;
        int sum = 0;
        for(int i = 0; i < input.length; i++) {
        	if(bitsum[i] != sum) {
        		output[j] = input[i].length();
        		sum += bitsum[i];
        	}
        }
        return output;
    }

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}