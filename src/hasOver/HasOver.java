package hasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import cse332.exceptions.NotYetImplementedException;

public class HasOver {
	public static boolean hasOver(int val, int[] arr, int sequentialCutoff) {

		class SumThread extends RecursiveTask<Boolean> {
			private static final long serialVersionUID = 1L;
			int lo;
			int hi;
			int val;
			int[] arr;
			int cutoff;

			public SumThread(int l, int h, int val, int[] a, int c) {
				lo = l;
				hi = h;
				this.val = val;
				arr = a;
				cutoff = c;
			}

			protected Boolean compute() {
				if (hi - lo < cutoff) {
					boolean ret = false;
					for (int i = lo; i < hi; i++) {
						if (arr[i] > val) {
							ret = true;
						}
					}

					return ret;

				} else {
					SumThread left = new SumThread(lo, (hi + lo) / 2, val, arr, cutoff);
					SumThread right = new SumThread((hi + lo) / 2, hi, val, arr, cutoff);
					left.fork();
					Boolean rightAns = right.compute();
					Boolean leftAns = left.join();
					return leftAns || rightAns;
				}
			}
		}
		SumThread thd = new SumThread(0, arr.length, val, arr, sequentialCutoff);
		return ForkJoinPool.commonPool().invoke(thd);

	}

	private static void usage() {
		System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
		System.exit(2);
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			usage();
		}

		int val = 0;
		int[] arr = null;

		try {
			val = Integer.parseInt(args[0]);
			String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
			arr = new int[stringArr.length];
			for (int i = 0; i < stringArr.length; i++) {
				arr[i] = Integer.parseInt(stringArr[i]);
			}
			System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
		} catch (NumberFormatException e) {
			usage();
		}

	}
}
