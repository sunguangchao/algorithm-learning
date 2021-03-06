import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by 11981 on 2016/12/1.
 */
public class MaxPQ<Key> implements Iterable<Key>{
    private Key[] pq; //基于堆的完全二叉树
    private int n; //存储于pq[1...n]中，pq[0]没有使用
    private Comparator<Key> comparator;

    public MaxPQ(int initCapacity){
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    public MaxPQ(){
        this(1);
    }

    public MaxPQ(int initCapacity, Comparator<Key> comparator){
        this.comparator = comparator;
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    public MaxPQ(Comparator<Key> comparator){
        this(1, comparator);
    }

    public MaxPQ(Key[] keys){
        n = keys.length;
        pq = (Key[]) new Object[keys.length + 1];
        for(int i=0; i<n; i++)
            pq[i+1] = keys[i];
        for(int k = n/2; k>= 1; k--) // ?
            sink(k);
        assert isMaxHeap();
    }

    public boolean isEmpty(){
        return n == 0;
    }

    public int size(){
        return n;
    }

    public Key max(){
        if(isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    // helper function to double the size of the heap array
    private void resize(int capacity){
        assert capacity > n;
        Key[] temp = (Key[]) new Object[capacity];
        for(int i=1; i <=n; i++){
            temp[i] = pq[i];
        }
        pq = temp;
    }

    public void insert(Key x){
        if(n >= pq.length - 1) resize(2 * pq.length);
        pq[++n] = x;
        swim(n);
        assert isMaxHeap();
    }

    public Key delMax(){
        if(isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        Key max = pq[1]; //从根节点得到最大元素
        exch(1, n--);    //将其和最后一个结点交换
        sink(1);         //恢复堆的有序性
        pq[n+1] = null;  // to avoid loiterig and help with garbage collection
        if((n>0) && (n==(pq.length - 1)/4)) resize(pq.length / 2);
        assert isMaxHeap();
        return max;
    }
    private void swim(int k){
        while(k > 1 && less(k/2, k)){
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k){
        while(2*k <= n){
            int j = 2*k;
            if(j<n && less(j, j+1)) j++;
            if(!less(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    private boolean less(int i, int j){
        if(comparator == null){
            return((Comparable<Key>) pq[i]).compareTo(pq[j]) < 0;
        }else{
            return comparator.compare(pq[i], pq[j]) < 0;
        }
    }
    private void exch(int i, int j) {
        Key swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

    private boolean isMaxHeap(){
        return isMaxHeap(1);
    }

    private boolean isMaxHeap(int k){
        if(k > n) return true;
        int left = 2*k;
        int right = 2*k + 1;
        if(left <= n && less(k, left)) return false;
        if(right <= n && less(k, right)) return false;
        return isMaxHeap(left) && isMaxHeap(right);
    }

    public Iterator<Key> iterator(){
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Key>{
        private MaxPQ<Key> copy;

        public HeapIterator(){
            if(comparator == null) copy = new MaxPQ<Key>(size());
            else                   copy = new MaxPQ<Key>(size(), comparator);
            for(int i=1; i <=n; i++)
                copy.insert(pq[i]);
        }

        public boolean hasNext(){return !copy.isEmpty();}
        public void remove(){throw new UnsupportedOperationException();}
        public Key next(){
            if(!hasNext()) throw new NoSuchElementException();
            return copy.delMax();
        }
    }

    public static void main(String[] args){
        MaxPQ<String> pq = new MaxPQ<String>();
        while(!StdIn.isEmpty()){
            String item = StdIn.readString();
            if(!item.equals("-")) pq.insert(item);
            else if(!pq.isEmpty()) StdOut.print(pq.delMax() + " ");
        }

        StdOut.println("(" + pq.size() + " left on pq)");
    }


}
