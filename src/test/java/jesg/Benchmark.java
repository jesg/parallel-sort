package jesg;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Benchmark {
    
    public static void main(String[] args) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        System.out.println("Parallelism: " + pool.getParallelism());
        
        int[] ar = new int[1000000];
        Random random = new Random();
        for(int i=0; i<ar.length; i++)
            ar[i] = random.nextInt();
        
        int reps = 50;
        
        long time = 0;
//        warm up threads
        for(int i=0; i<100; i++)
            jre8ParallelSort(Arrays.copyOf(ar, ar.length));
        
        for(int i=0; i<reps; i++)
            time += jre8ParallelSort(Arrays.copyOf(ar, ar.length));
        
        System.out.println("jre8 parallel: " + time/(double)reps);
        
        ParallelSort sorter = new ParallelSort(pool);
//        warm up threads
        for(int i=0; i<100; i++)
            forkJoin(Arrays.copyOf(ar, ar.length), sorter);
        
        time = 0;
        for(int i=0; i<reps; i++)
            time += forkJoin(Arrays.copyOf(ar, ar.length), sorter);

        System.out.println("fork/join: " + time/(double)reps);
        
        time = 0;
        for(int i=0; i<reps; i++)
            time += jre8Sort(Arrays.copyOf(ar, ar.length));
        
        System.out.println("jre8 single: " + time/(double)reps);
    }
    
    static long jre8ParallelSort(int[] ar){
        long start = System.currentTimeMillis();
        Arrays.parallelSort(ar);
        return System.currentTimeMillis() - start;
    }
    
    static long forkJoin(int[] ar, ParallelSort sorter){
        long start = System.currentTimeMillis();
        sorter.sort(ar);
        return System.currentTimeMillis() - start;
    }
    
    static long jre8Sort(int[] ar){
        long start = System.currentTimeMillis();
        Arrays.sort(ar);
        return System.currentTimeMillis() - start;
    }
}
