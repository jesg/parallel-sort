package jesg;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelSort {
    private ForkJoinPool pool;

    public ParallelSort(ForkJoinPool pool) {
        this.pool = pool;
    }

    public void sort(int[] ar) {
        pool
        .submit(new SortTask(ar, ar.length / pool.getParallelism(), 0,ar.length))
        .join();
    }

    private static class SortTask extends RecursiveAction {
        private final int[] ar;
        private final int[] temp;
        private final int threshold;
        private final int lo;
        private final int hi;

        private SortTask(int[] ar, int[] temp, int threshold, int lo, int hi) {
            this.ar = ar;
            this.lo = lo;
            this.hi = hi;
            this.temp = temp;
            this.threshold = threshold;
        }

        SortTask(int[] ar, int threshold, int lo, int hi) {
            this.ar = ar;
            this.lo = lo;
            this.hi = hi;
            this.temp = new int[ar.length];
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            if (hi - lo < threshold) {
                Arrays.sort(ar, lo, hi);
            } else {
                int mid = (lo + hi) >>> 1;
                invokeAll(new SortTask(ar, temp, threshold, lo, mid),
                        new SortTask(ar, temp, threshold, mid, hi));
                merge(mid);
            }

        }

        private void merge(final int mid) {
            int i = lo;
            int j = mid;
            int k = lo;

            while ((i < mid) && (j < hi)) {
                if (ar[i] <= ar[j]) {
                    temp[k++] = ar[i++];
                } else {
                    temp[k++] = ar[j++];
                }
            }

            while (i < mid)
                temp[k++] = ar[i++];

            while (j < hi)
                temp[k++] = ar[j++];

            for (int t = lo; t < hi; t++)
                ar[t] = temp[t];
        }
    }
}
