package getLeftMostIndex;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import cse332.exceptions.NotYetImplementedException;

public class GetLeftMostIndex {
	public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {

		class SumThread extends RecursiveTask<Integer> {
			private static final long serialVersionUID = 1L;
			int lo;
			int hi;
			char[] needle;
			char[] haystack;
			int cutoff;

			public SumThread(int l, int h, char[] needle, char[] haystack, int c) {
				lo = l;
				hi = h;
				this.needle = needle;
				this.haystack = haystack;
				cutoff = c;
			}

			protected Integer compute() {
				if (hi - lo <= cutoff) {

					for (int i = lo; i < hi; i++) {
						int k = 0;
						while (k < needle.length && needle[k] == haystack[i + k]) {
							if (k == needle.length - 1) {
								return (i - lo);
							}
							k++;
						}

					}
					return -1;

				} else {
					SumThread left = new SumThread(lo, (hi + lo) / 2, needle, haystack, cutoff);
					SumThread right = new SumThread((hi + lo) / 2, hi, needle, haystack, cutoff);
					left.fork();
					int rightAns = right.compute();
					int leftAns = left.join();
					if (rightAns != -1 && leftAns != -1) {
						return Math.min(leftAns, rightAns);
					}
					if (rightAns == -1) {
						return leftAns;
					}
					if (leftAns == -1) {
						return rightAns;
					}
					return -1;
				}
			}
		}
		SumThread thd = new SumThread(0, haystack.length, needle, haystack, sequentialCutoff);
		return ForkJoinPool.commonPool().invoke(thd);
	}

	private static void usage() {
		System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
		System.exit(2);
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			usage();
		}

		char[] needle = args[0].toCharArray();
		char[] haystack = args[1].toCharArray();
		try {
			System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
		} catch (NumberFormatException e) {
			usage();
		}
	}
}
