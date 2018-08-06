package longestSequence;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import cse332.exceptions.NotYetImplementedException;

public class LongestSequence {
	public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {

		class SumThread extends RecursiveTask<SequenceRange> {
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

			protected SequenceRange compute() {
				if (hi - lo <= cutoff) {
					int left = 0;
					boolean leftConsec = true;
					int right = 0;
					int longest = 0;
					int streak = 0;

					for (int i = lo; i < hi; i++) {
						if (arr[i] == val) {
							if (leftConsec) {
								left++;
							}
							right++;
							streak++;
							if (streak > longest) {
								longest = streak;
							}
						}

						else {
							right = 0;
							leftConsec = false;
							streak = 0;
						}

					}
					SequenceRange seq = new SequenceRange(left, right, longest, hi - lo);
					return seq;

				} else {
					int mid = lo + (hi - lo) / 2;
					SumThread left = new SumThread(lo, mid, val, arr, cutoff);
					SumThread right = new SumThread(mid, hi, val, arr, cutoff);
					left.fork();
					SequenceRange rightAns = right.compute();
					SequenceRange leftAns = left.join();

					return combine(leftAns, rightAns);

				}

			}
		}
		SumThread thd = new SumThread(0, arr.length, val, arr, sequentialCutoff);
		return ForkJoinPool.commonPool().invoke(thd).longestRange;

	}

	private static SequenceRange combine(SequenceRange leftAns, SequenceRange rightAns) {

		int longest = Math.max(rightAns.longestRange, leftAns.longestRange);
		
		if (leftAns.matchingOnRight + rightAns.matchingOnLeft > longest) {
			
			longest = leftAns.matchingOnRight + rightAns.matchingOnLeft;
		}

		if (leftAns.sequenceLength == leftAns.longestRange && rightAns.sequenceLength != rightAns.longestRange) {
			return new SequenceRange(leftAns.matchingOnLeft + rightAns.matchingOnLeft,
					rightAns.matchingOnRight, longest,
					leftAns.sequenceLength + rightAns.sequenceLength);
		}
		if (leftAns.sequenceLength != leftAns.longestRange && rightAns.sequenceLength == rightAns.longestRange) {
			return new SequenceRange(leftAns.matchingOnLeft ,
					rightAns.matchingOnRight+ leftAns.matchingOnRight, longest,
					leftAns.sequenceLength + rightAns.sequenceLength);
		}
		
		if (leftAns.sequenceLength == leftAns.longestRange && rightAns.sequenceLength == rightAns.longestRange) {
			return new SequenceRange(leftAns.matchingOnLeft + rightAns.matchingOnLeft,
					rightAns.matchingOnRight + leftAns.matchingOnRight, longest,
					leftAns.sequenceLength + rightAns.sequenceLength);
		}
		
		return new SequenceRange(leftAns.matchingOnLeft, rightAns.matchingOnRight, longest,
				leftAns.sequenceLength + rightAns.sequenceLength);
	}


	private static void usage() {
		System.err.println("USAGE: LongestSequence <number> <array> <sequential cutoff>");
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
			System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
		} catch (NumberFormatException e) {
			usage();
		}
	}
}