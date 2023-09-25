import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.Arrays;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode min = null;
    public HeapNode first = null;
    private int size = 0;
    private int totalMarks = 0;
    public int totalTrees = 0;
    private static int totalLinks = 0;
    private static int totalCuts = 0;

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     */
    public boolean isEmpty()
    {
        return size==0;
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     */
    public HeapNode insert(int key)
    {
        HeapNode node = new HeapNode(key);
        this.totalTrees ++;


        if (this.size==0){//empty heap
            this.first = node;
            this.min = node;
            this.first.setNext(this.first);
            this.first.setPrev(this.first);
            this.size++;
            return node;
        }
        this.size++;
        HeapNode prev = this.first.getPrev();
        HeapNode last_first = this.first;
        if (this.min.getKey()>node.key) this.min=node;
        this.first = node;
        this.first.setNext(last_first);
        last_first.setPrev(this.first);
        this.first.setPrev(prev);
        prev.setNext(this.first);
        return node;
    }


    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     */
    public void deleteMin()
    {
        HeapNode minnode=this.min;
        if (minnode==null){return;}
        else {
            HeapNode minchild = minnode.getChild();
            if (minchild == null)
            {
                if (this.first==this.first.getNext()){this.first=null;this.min=null;totalTrees=0;totalMarks=0;size=0;return;}
                else
                {
                    if (min==this.first){this.first=min.getNext();}
                    this.min.getPrev().setNext(this.min.getNext());
                    this.min.getNext().setPrev(this.min.getPrev());
                }
            }
            else
            {
                HeapNode firstchild = minchild;
                if (firstchild.getMark() == true) {
                    totalMarks--;
                }
                firstchild.setMark(false);
                HeapNode x = firstchild.getNext();
                HeapNode y = firstchild;
                while (x != firstchild) {
                    y.setParent(null);
                    y = y.getNext();
                    x = x.getNext();
                    if (x.getMark() == true) {
                        totalMarks--;
                    }
                    x.setMark(false);
                }
                if (this.first==this.first.getNext())
                {
                    HeapNode lstchild=firstchild.getPrev();
                    this.first=firstchild;
                    this.first.setPrev(lstchild);
                    lstchild.setNext(this.first);
                }
                else
                {

                    this.min.getPrev().setNext(this.min.getNext());
                    this.min.getNext().setPrev(this.min.getPrev());
                    if (min==this.first){this.first=min.getNext();this.first.setPrev(min.getPrev());min.getPrev().setNext(this.first);}
                    HeapNode oldfirst=this.first;
                    HeapNode LSTNODE=this.first.getPrev();
                    HeapNode lstchild=firstchild.getPrev();
                    this.first=firstchild;
                    firstchild.setPrev(LSTNODE);
                    lstchild.setNext(oldfirst);
                    oldfirst.setPrev(lstchild);
                    LSTNODE.setNext(this.first);

                }

            }
            consolidate();
            this.min = searchmin();
            this.size--;
            int[] arr = countersRep();
            int z = 0;
            for (int i = 0; i < arr.length; i++) {
                z += arr[i];
            }
            this.totalTrees = z;
            return;
        }
    }

    public void consolidate()
    {
        int cnt=0;
        HeapNode[]arr=new HeapNode[size];
        HeapNode tmp=this.first;
        HeapNode hh=null;
        while (this.first!=hh)
        {
            hh=tmp.getNext();
            if (arr[tmp.getRank()]!=null)
            {
                int o=tmp.getRank();
                HeapNode newnode=link(arr[o],tmp);
                arr[o]=null;
                while (arr[newnode.getRank()]!=null)
                {
                    int xx=newnode.getRank();
                    newnode=link(arr[newnode.getRank()],newnode);
                    cnt++;
                    arr[xx]=null;
                }
                arr[newnode.getRank()]=newnode;
                cnt++;
            }
            else{ arr[tmp.getRank()]=tmp;}
            tmp=hh;
        }
        totalLinks+=cnt;
        int firsst=0;
        HeapNode helpp=null;
        int lastt=0;
        for (int i=0;i<arr.length;i++)
        {
            if (arr[i]!=null)
            {
                firsst++;
                if (firsst==1){this.first=arr[i];helpp=this.first;lastt=i;}
                else {
                helpp.setNext(arr[i]);
                arr[i].setPrev(helpp);
                helpp=helpp.getNext();
                lastt=i;}
            }
        }
        arr[lastt].setNext(this.first);
        this.first.setPrev(arr[lastt]);
        this.min=searchmin();
        return;
    }

    public HeapNode link(HeapNode h1,HeapNode h2)
    {
        HeapNode tmp;
        if (h1.getKey()>h2.getKey()){tmp=h1;h1=h2;h2=tmp;}
        if (h1.getChild()==null){h1.setChild(h2);h2.setParent(h1); h2.setNext(h2);h2.setPrev(h2);}
        else
        {
            h2.setPrev(h1.getChild().getPrev());
            h1.getChild().getPrev().setNext(h2);
            h1.getChild().setPrev(h2);
            h2.setNext(h1.getChild());
        }
        h1.setChild(h2);
        h2.setParent(h1);
        h1.setRank(h1.getRank()+1);
        return h1;
    }

    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     *
     */
    public HeapNode findMin()
    {
        if (size==0){return null;}return this.min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
    public void meld (FibonacciHeap heap2)
    {
        if (heap2.isEmpty()) return;//if heap2 empty
        this.totalTrees += heap2.totalTrees;
        this.totalMarks += heap2.totalMarks;
        HeapNode other_first = heap2.first;
        HeapNode other_min = heap2.min;
        int other_size = heap2.size();
        heap2.first = null;
        heap2.min = null;
        heap2.size = 0;
        heap2.totalMarks = 0;
        heap2.totalTrees = 0;

        if (this.size==0){//our heap is empty
            this.first = other_first;
            this.min = other_min;
            this.size += other_size;
            return;
        }

        if (other_min.getKey()<this.min.getKey()) this.min = other_min;
        this.size += other_size;
        HeapNode last = this.first.getPrev();
        this.first.setPrev(other_first.getPrev());
        other_first.getPrev().setNext(this.first);
        last.setNext(other_first);
        other_first.setPrev(last);
        return;
    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     */
    public int size()
    {
        return this.size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     *
     */
    public int[] countersRep()
    {
        int[] arr = new int[size];
        if (this.size==0) return arr;
        HeapNode pointer = this.first;//we will fo through all the roots
        int index = this.first.getRank();//for returning the array in wanted len
        arr[pointer.getRank()]++;
        pointer = pointer.getNext();
        while (pointer!=this.first){
            arr[pointer.getRank()]++;
            if (index<pointer.getRank()) index = pointer.getRank();
            pointer = pointer.getNext();
        }
        int[] result = Arrays.copyOf(arr, index+1);
        return result;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     *
     */
    public void delete(HeapNode x)
    {
        if (size==0) return; //empty heap
        decreaseKey(x, Integer.MAX_VALUE);
        deleteMin();
        return;
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        if (this.size==0) return; //empty heap
        x.setKey(x.getKey()-delta);
        if (this.min.getKey()>x.getKey()) this.min = x;//if after increase the node has the smallest key value
        if (x.getParent()!=null){
            if (x.getKey()<x.getParent().getKey()) {castingcut(x);} //now the heap is not in correct form -> we call to cascading cuts to fix
        }
        return;
    }

    private void castingcut(HeapNode x){
        HeapNode y = x.getParent();
        cut(x);
        if (y.getParent()!=null){
            if (y.getMark()==false) {
                y.setMark(true);//we cut from y a child
                totalMarks++;
            }
            else{
                castingcut(y);
            }
        }
        return;
    }

    private void cut(HeapNode x){//cut x from its parent y. assume y!=null
        HeapNode y = x.getParent();
        x.setParent(null);
        if(x.getMark()) {
            totalMarks--;
            x.setMark(false);
        }
        y.setRank(y.getRank()-1);
        totalCuts++;//we cut
        totalTrees++;//we create new tree
        if (x.next==x) { //y has one kid which we cut -> no kids
            y.setChild(null);
        }
        else
        {
            y.setChild(x.getNext());
            x.getPrev().setNext(x.getNext());
            x.getNext().setPrev(x.getPrev());
        }
        HeapNode last_first = this.first;
        this.first = x;
        x.setPrev(last_first.getPrev());
        last_first.getPrev().setNext(x);
        x.setNext(last_first);
        last_first.setPrev(x);
    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential()
    {
        return (this.totalTrees + 2*this.totalMarks);
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks()
    {
        return totalLinks;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {
        return totalCuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        HeapNode [] brr=new HeapNode[k*(int)Math.floor(Math.log(H.size())+1)];
        HeapNode [] helpp=new HeapNode[k*(int)Math.floor(Math.log(H.size())+1)];
        int [] res=new int[k];
        HeapNode h=H.first.getNext();
        int i=1;
        brr[0]=H.first;
        while (h!=H.first)
        {
            brr[i]=h;
            h=h.getNext();
            i++;
        }
        int index=0;
        for (int j=0;j<(int)Math.floor((int)Math.floor(Math.log(H.size())+1))+1;j++)
        {
            int index2=0;
            for (int l=0;l< i;l++)
            {
                 if (index<k){  res[index] = H.minonarrayandsavethechild(brr, helpp);index++;}
                else {break;}
            }
            if (index<k)
            {
                brr=new HeapNode[(int)Math.floor(Math.log(H.size())+1)];
                int v=0;
                while (index2< helpp.length&&helpp[index2]!=null&&helpp!=new HeapNode[k*(int)Math.floor(Math.log(H.size())+1)])
                {
                     h=helpp[index2].getChild().getNext();
                    brr[v]=helpp[index2].getChild();
                    v++;
                     helpp[index2]=null;
                     index2++;
                    while (h!=null&&h!=brr[v])
                    {
                        brr[v]=h;
                        h=h.getNext();
                        v++;
                    }
                }
            }
            else {break;}
        }
        for (int l=0;l< brr.length;l++)
        {
           if (index<k){  res[index] = H.minonarrayandsavethechild(brr, helpp);index++;}
            else {break;}

        }
        return res;
    }
    public int minonarrayandsavethechild(HeapNode []arr,HeapNode []helpp)
    {
        int x=arr[0].getKey();
        HeapNode xx=arr[0];
        int j=0;
        for (int i=0;i<arr.length;i++)
        {
            if (arr[i]!=null&&arr[i].getKey()<x)
            {
                x=arr[i].getKey();
                xx=arr[i];
            }
        }
        for (int i=0;i<arr.length;i++)
        {
            if (arr[i]==xx)
            {
                arr[i]=null;
            }
        }
        if (xx.getChild()!=null){helpp[j]=xx;j++;}
        return x;
    }
    public HeapNode searchmin()
    {
        HeapNode h=this.first.getNext();
        int x=this.first.getKey();
        HeapNode xx=this.first;
        while (h!=this.first)
        {
            if (h.getKey()<x){x=h.getKey();xx=h;}
            h=h.getNext();
        }
        return xx;
    }

    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode{

        public int key;
        public int rank = 0;
        public boolean mark = false;
        public HeapNode child = null;
        public HeapNode next = null;
        public HeapNode prev = null;
        public HeapNode parent = null;

        public HeapNode(int key) {
            this.key = key;
        }

        public int getKey() {
            return this.key;
        }

        public void setKey(int key) {this.key = key;}

        public int getRank() {return this.rank;}

        public void setRank(int rank) {
            this.rank = rank;
        }

        public boolean getMark() {
            return this.mark;
        }

        public void setMark(boolean mark) {
            this.mark = mark;
        }
        public HeapNode getChild() {
            return this.child;
        }

        public void setChild(HeapNode child) {
            this.child = child;
        }
        public HeapNode getNext() {
            return this.next;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }
        public HeapNode getPrev() {
            return this.prev;
        }

        public void setPrev(HeapNode prev) {
            this.prev = prev;
        }
        public HeapNode getParent() {
            return this.parent;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }
    }
}
