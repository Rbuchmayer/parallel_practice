package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import cse332.exceptions.NotYetImplementedException;

public class FilterEmpty {
	static ForkJoinPool POOL = new ForkJoinPool();

	public static int[] filterEmpty(String[] arr) {
		int[] bitset = mapToBitSet(arr);

		int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bitset);
		int[] result = mapToOutput(arr, bitsum);
		return result;
	}

	public static int[] mapToBitSet(String[] arr) {

		class SumThread extends RecursiveTask<int[]> {
			private static final long serialVersionUID = 1L;
			int lo;
			int hi;
			String[] arr;

			public SumThread(int l, int h, String[] a) {
				lo = l;
				hi = h;
				arr = a;
			}

			protected int[] compute() {
				if (hi - lo <= 1) {
					int[] bits = new int[hi - lo];
					for (int i = lo; i < hi; i++) {
						if (arr[i].length() > 0) {
							bits[0] = 1;
						}
					}
					return bits;
				}

				else {
					SumThread left = new SumThread(lo, (hi + lo) / 2, arr);
					SumThread right = new SumThread((hi + lo) / 2, hi, arr);
					left.fork();
					int[] rightAns = right.compute();
					int[] leftAns = left.join();
					int[] out = new int[rightAns.length + leftAns.length];
					for (int i = 0; i < leftAns.length; i++) {
						out[i] = leftAns[i];
					}
					int j = 0;
					for (int i = leftAns.length; i < out.length; i++) {
						out[i] = rightAns[j];
						j++;
					}
					return out;
				}

			}

		}
		SumThread thd = new SumThread(0, arr.length, arr);
		return ForkJoinPool.commonPool().invoke(thd);

	}

	public static int[] mapToOutput(final String[] input, int[] bitsum) {
		class SumThread extends RecursiveTask<int[]> {
			private static final long serialVersionUID = 1L;
			int lo;
			int hi;
			String[] in;
			int[] bitsum;
			int[] bitset = mapToBitSet(input);

			public SumThread(int l, int h, String[] input, int[] bitsum, int[] bitset) {
				lo = l;
				hi = h;
				in = input;
				this.bitsum = bitsum;
				this.bitset = bitset;
			}

			protected int[] compute() {

				if (hi - lo <= 1) {

					int[] output;
					if (bitset.length == 0) {
						return new int[0];
					}

					if (bitset[lo] == 1) {
						output = new int[1];
						output[0] = input[lo].length();

					} else {
						output = new int[0];
					}

					return output;
				}

				else {
					SumThread left = new SumThread(lo, (hi + lo) / 2, in, bitsum, bitset);
					SumThread right = new SumThread((hi + lo) / 2, hi, in, bitsum, bitset);
					left.fork();
					int[] rightAns = right.compute();
					int[] leftAns = left.join();

					int[] out = new int[rightAns.length + leftAns.length];
					for (int i = 0; i < leftAns.length; i++) {
						out[i] = leftAns[i];
					}
					int j = 0;
					for (int i = leftAns.length; i < out.length; i++) {
						out[i] = rightAns[j];
						j++;
					}
					return out;

				}

			}

		}

		SumThread thd = new SumThread(0, input.length, input, bitsum, mapToBitSet(input));
		return ForkJoinPool.commonPool().invoke(thd);
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