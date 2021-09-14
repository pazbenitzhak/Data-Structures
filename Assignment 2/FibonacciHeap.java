//Name: Guy Paz Ben-Itzhak  Username:pazbenithak   ID:315328963
//Name: Rai Pheterson       Username:raipheterson  ID:311214852

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {

	private HeapNode first;
	private HeapNode min;
	private int size;
	private int trees;
	private int marked;
	private static int totalLinks;
	private static int totalCuts;

	/**
	 * public FibonacciHeap()
	 * a constructor made for empty heaps
	 * Sets all values to null or 0, as expected
	 * precondition: nothing written in parenthesis
	 * postcondition: An empty FibonacciHeap is created
	 * Complexity(WC and Amortized): O(1)
	 */
	public FibonacciHeap() {
		this.first = null;
		this.min = null;
		this.size = 0;
	}
	
	
	/**
	 * public FibonacciHeap(HeapNode first, HeapNode min, int size)
	 * a constructor made for a new FibonacciHeap
	 * Sets first node (the leftmost node), node with minimal value and size
	 * precondition: type(first,min)==HeapNode&&type(size)==int
	 * postcondition: An empty tree is created
	 * Complexity (WC and Amortized): O(1)
	 * @param first
	 * @param min
	 * @param size
	 */
	public FibonacciHeap(HeapNode first, HeapNode min, int size) {
		this.first = first;
		this.min = min;
		this.size = size;
	}

	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is empty.
	 * 
	 */
	public boolean isEmpty() {
		if (this.size() == 0) {
			return true;
		}
		return false; // should be replaced by student code
	}
	
	
	/**
	 * public HeapNod getFirst()
	 * returns the heap's 'first' HeapNode
	 * precondition: nothing in parenthesis
	 * postcondition: a HeapNode (or null if empty heap)
	 * Complexity (WC and Amortized): O(1)
	 * @return the leftmost HeapNode in the heap
	 */
	public HeapNode getFirst() {
		return this.first;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts
	 * it into the heap.
	 * 
	 * Returns the new node created.
	 */
	public HeapNode insert(int key) {
		HeapNode node = new HeapNode(key);
		if (this.isEmpty()) {
			this.first = node;
			this.min = node;
			node.setNext(node);
			node.setPrev(node);
			this.size = 1;
			this.trees++;
			return node;
		}
		node.setPrev(this.first.prev);
		this.first.setPrev(node);
		node.setNext(this.first);
		node.getPrev().setNext(node);

		if (node.getKey() < this.min.getKey()) {
			this.min = node;
		}
		this.first = node;
		this.size++;
		this.trees++;
		return node;
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin() {
		if (this.size() == 0) {
			return;
		}
		if (this.size() == 1) {
			this.first = null;
			this.min = null;
			this.size = 0;
			this.trees--;
			return;
		}
		HeapNode minNode = this.min;
		if (minNode.getKey()==this.first.getKey()) {//change the first so it doesn't get erased
			this.first = this.first.getNext();
		}
		if (minNode.getRank() == 0) {
			minNode.arrangeSiblings();
			this.consolidateLinking();
			return;
		}
		HeapNode leftChild = minNode.getChild();
		HeapNode rightChild = leftChild.getPrev();
		if (this.trees==1) {//no neighbors to attach
			this.first=leftChild;
			minNode.disconnectChildren();
			minNode.setChild(null);
			this.consolidateLinking();
			return;
		}
		minNode.disconnectChildren();
		leftChild.setPrev(minNode.getPrev());
		minNode.getPrev().setNext(leftChild);
		rightChild.setNext(minNode.getNext());
		minNode.getNext().setPrev(rightChild);
		minNode.setChild(null);
		this.consolidateLinking();
	}

	/**
	 * public void consolidateLinking()
	 * puts all the heap's trees in a HashMap, and after linking them concatenates them into the current FibonacciHeap
	 * precondition: a FibonacciHeap without the deleted node, and with no pointers to it
	 * postcondition: an 'organized' heap (as Binomial Heap)
	 * Complexity WC: O(n)
	 * Complexity Amortized: O(logn) 
	 * @return none
	 */
	public void consolidateLinking() {
		HashMap<Integer, HeapNode> bucketList = new HashMap<Integer, HeapNode>();
		HeapNode pivot = this.first;
		HeapNode nextElem;
		HeapNode minNode = this.first;
		int firstKey = pivot.getKey();
		int nextElemKey = -1;
		while (nextElemKey != firstKey) {
			minNode = updateMin(minNode, pivot);
			nextElem = pivot.getNext();
			nextElemKey = nextElem.getKey();
			pivot.setNext(null);
			pivot.setPrev(null);
			bucketList = pivot.linking(bucketList);
			pivot = nextElem;
		}
		Set<Integer> bucketSet = bucketList.keySet();
		List<Integer> ranksList = new ArrayList<Integer>(bucketSet);
		Collections.sort(ranksList);
		HeapNode node;
		HeapNode nextNode;
		if (ranksList.size() == 1) {
			nextNode = bucketList.get(ranksList.get(0));
		} else {
			nextNode = bucketList.get(ranksList.get(1));
		}
		for (int i = 0; i < ranksList.size() - 1 ; i ++) {
			node = bucketList.get(ranksList.get(i));
			nextNode = bucketList.get(ranksList.get(i+1));
			node.setNext(nextNode);
			nextNode.setPrev(node);
		}
		node = bucketList.get(ranksList.get(0));//change index
		nextNode.setNext(node);
		node.setPrev(nextNode);
		this.min = minNode;
		this.first = nextNode;
		this.size = this.size() - 1;
		this.trees = ranksList.size();
	}
	
	
	/**
	 * public HeapNode updateMin(HeapNode minNode, HeapNode pivot)
	 * checks which node has the smaller key and returns it
	 * precondition: type(minNode, pivot)==HeapNode
	 * postcondition: a HeapNode
	 * Complexity (WC and Amortized): O(1)
	 * @param minNode
	 * @param pivot
	 * @return the HeapNode with the smaller key
	 */
	public HeapNode updateMin(HeapNode minNode, HeapNode pivot) {  //might needs to be static
		if (pivot.getKey() < minNode.getKey()) {
			return pivot;
		}
		return minNode;
	}

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal.
	 *
	 */
	public HeapNode findMin() {
		return this.min;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2) {
		if (this.min.getKey() > heap2.min.getKey()) { // updating minimum
			this.min = heap2.min;
		}
		HeapNode lastHeap1 = this.first.getPrev(); // concatination
		lastHeap1.setNext(heap2.first);
		heap2.first.getPrev().setNext(this.first);
		this.first.setPrev(heap2.first.getPrev());
		heap2.first.setPrev(lastHeap1);

		this.size += heap2.size(); // update size
		this.trees = this.trees + heap2.trees;
		this.marked = this.marked + heap2.marked;
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {
		return this.size;
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of
	 * trees of order i in the heap.
	 * 
	 */
	public int[] countersRep() {
		if (this.size() == 0) {
			return new int[0];
		}
		
		int arraySize = (int) (Math.log(this.size()+1) + 2);
		int[] arr = new int[arraySize];
		int firstKey = this.first.getKey();
		HeapNode node = this.first;
		HeapNode nextNode = node.getNext();
		arr[node.getRank()]++;
		while (nextNode.getKey() != firstKey) {
			arr[nextNode.getRank()]++;
			node = node.getNext();
			nextNode = nextNode.getNext();
		}
		int index = 0;
		for (int i = arraySize - 1 ; i >= 0 ; i--) {
			if (arr[i] != 0) {
				index = i;
				break;
			}
		}
		if (index == arraySize - 1) {
			return arr;
		}
		int[] newArray = Arrays.copyOfRange(arr, 0, index + 1);
		return newArray; 
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 *
	 */
	public void delete(HeapNode x) {
		this.decreaseKey(x, Integer.MIN_VALUE);
		this.deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the
	 * heap should be updated to reflect this chage (for example, the cascading cuts
	 * procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta) {
		x.updateKey(delta);
		if (x.getKey() < this.min.getKey()) {
			this.min = x;
		}
		if (x.getParent() == null) { // may cause exception
			return;
		}
		if (x.getKey() < x.getParent().getKey()) { // otherwise, nothing to change
			HeapNode node = x;
			HeapNode parent = x.getParent();
			int mark = parent.getMark();
			if (x.getMark()==1) {//need to take care if x is marked and deleted
				x.setMark();
				this.marked--;
			}
			this.removeFromTree(x);
			this.trees++;
			while (mark == 1) {
				node = parent;
				parent = parent.getParent();
				mark = parent.getMark();
				node.setMark();
				this.marked--;
				this.removeFromTree(node);
				this.trees++;
			}
			if (parent.getParent() != null) {
				parent.setMark();
				this.marked++;
			}
		}
	}
	
	/**public void removeFromTree(HeapNode x)
	 * removes a HeapNode from the heap (by checking 3 cases and using submethods)
	 * precondition: type(x)==HeapNode
	 * postcondition: executes removal of a node from the heap by using submethods
	 * Complexity (WC and Amortized): O(1) 
	 * @param x
	 */
	public void removeFromTree(HeapNode x) {
		if (x.isOnlyChild()) {
			x.getParent().setRank(0);
			x.getParent().setChild(null); // may cause exception
			x.setParent(null);
			this.cut(x);
			return;
		}
		if (x.isLeftChild()) {
			x.getParent().setChild(x.getNext());
			this.removeSubTree(x);
			return;
		} else { // middle child, has siblings
			this.removeSubTree(x);
			return;
		}
	}
	
	
	/**
	 * public void removeSubTree(HeapNode x)
	 * removes a subtree from a heap (opposed to just a node) using the cut method
	 * precondition: type(x)==HeapNode
	 * postcondition: executes removal of a subtree from the heap by using submethods
	 * Complexity (WC and Amortized): O(1) 
	 * @param x
	 */
	public void removeSubTree(HeapNode x) {
		x.arrangeSiblings();
		x.getParent().setRank(x.getParent().getRank() - 1);
		x.setParent(null);
		this.cut(x);
	}
	
	/**
	 * public void cut(HeapNode x)
	 * transfers the node and its subtree into the first place in the heap
	 * precondition: type(x)==HeapNode
	 * postcondition: concatenates the node with the heap using the meld method
	 * Complexity (WC and Amortized): O(1)
	 * @param x
	 */
	public void cut(HeapNode x) {
		FibonacciHeap newHeap = new FibonacciHeap(x, x, 0); // size initialized to 0, size of original heap doesn't //
															// change
		newHeap.meld(this);
		this.first = newHeap.first;
		this.min = newHeap.min;
		FibonacciHeap.totalCuts++;
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked The potential equals to the number of trees in the heap
	 * plus twice the number of marked nodes in the heap.
	 */
	public int potential() {
		return this.trees + 2 * this.marked; 
	}
	
	/**
	 * public int getNumOfTrees()
	 * returns the number of trees in the heap (the 'trees' field)
	 * precondition: nothing in parenthesis
	 * postcondition: type(@res)==int
	 * Complexity (WC and Amortized): O(1) 
	 * @return number of trees in the heap
	 */
	public int getNumOfTrees () {
		return this.trees;
	}
	
	
	/**
	 * public HeapNode[] getTrees()
	 * returns an array with each tree's root (tree in heap)
	 * precondition: nothing in parenthesis
	 * postcondition: @res = a HeapNode array
	 * Complexity (WC): O(n)
	 * Complexity(Amoritzed): O(logn)
	 * @return an array containing all the heep's trees' roots
	 */
	public HeapNode[] getTrees() {
		HeapNode[] array = new HeapNode[this.trees];
		HeapNode node = this.first;
		for (int i = 0; i<this.trees;i++) {
			array[i] = node;
			node = node.getNext();
		}
		return array;
	}
	


	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two trees of the same rank, and generates a tree of rank bigger by one,
	 * by hanging the tree which has larger value in its root on the tree which has
	 * smaller value in its root.
	 */
	public static int totalLinks() {
		return FibonacciHeap.totalLinks; // should be replaced by student code
	}

	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * diconnects a subtree from its parent (during decreaseKey/delete methods).
	 */
	public static int totalCuts() {
		return FibonacciHeap.totalCuts; // should be replaced by student code
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k)
	 *
	 * This static function returns the k minimal elements in a binomial tree H. The
	 * function should run in O(k*deg(H)). You are not allowed to change H.
	 */
	public static int[] kMin(FibonacciHeap H, int k) {
		int[] arr = new int[k];
		if (k == 0) {
			return arr;
		}
		HashMap<HeapNode, HeapNode> nodeMatch = new HashMap<HeapNode, HeapNode>();
		FibonacciHeap newHeap = new FibonacciHeap();
		HeapNode firstInsert = newHeap.insert(H.first.getKey());
		HeapNode child = H.first;
		nodeMatch.put(firstInsert, child);
		for (int i = 0; i < k ; i++) {
			child = newHeap.findMin();
			arr[i] = child.getKey();
			newHeap.deleteMin();
			nodeMatch = newHeap.addAllChildren(child, nodeMatch);
		}
		return arr; 
	}

	/**
	 * public HashMap<HeapNode, HeapNode> allAllChildren(HeapNode node, HashMap<HeapNode, HeapNode> dict)
	 * adds all the node's current children to the 'other' heap, and inserts them into a dictionary
	 * The dictionary maps each child to its copy in the 'other' heap
	 * precondition: type(node)==HeapNode&&type(dict)==HashMap<HeapNode, HeapNode>
	 * postcondition: type(@res)==HashMap<HeapNode, HeapNode>
	 * Complexity (WC and Amortized): O(n) [when n is the number of node's children]
	 * @param node
	 * @param dict
	 * @return HashMap<HeapNode, HeapNode> containing each child and its copy in the current heap
	 */
	public HashMap<HeapNode, HeapNode> addAllChildren(HeapNode node, HashMap<HeapNode, HeapNode> dict) {
		HeapNode pivot = dict.get(node);
		if (pivot.getChild() != null) {  //else, nothing to insert
			HeapNode child = pivot.getChild();
			int firstKey = child.getKey();
			HeapNode insertNode = this.insert(firstKey);
			dict.put(insertNode, child);
			HeapNode nextNode = child.getNext();
			while (nextNode.getKey() != firstKey) {
				insertNode = this.insert(nextNode.getKey());
				dict.put(insertNode, nextNode);
				nextNode = nextNode.getNext();
			}
		}
		return dict;
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 */
	public class HeapNode {

		public int key;
		private int rank;
		private int mark;
		private HeapNode child;
		private HeapNode next;
		private HeapNode parent;
		private HeapNode prev;

		
		/**public HeapNode(int k)
		 * A constructor for the class HeapNode. sets the 'key' field to k, and other field to be default values
		 * precondition: type(k)==int
		 * postcondition: A new HeapNode is created
		 * Complexity (WC and Amortized): O(1)
		 * @param k
		 */
		public HeapNode(int k) {
			this.key = k;
			this.rank = 0;
			this.mark = 0;
			this.child = null;
			this.next = null;
			this.parent = null;
			this.prev = null;
		}

		/**HashMap<Integer, HeapNode> linking(HashMap<Integer, HeapNode> bucketlist)
		 * for each node: checks if there ia a node with the same rank in bucketlist
		 * if not, adds it to bucketlist with its rank
		 * else, sends it to chained link to link it with the other node with same rank
		 * Then a recursive call is being made, and bucketlist is returned
		 * precondition: type(bucketlist)==HashMap<Integer, HeapNode>
		 * postcondition: type(@res)==HashMap<Integer, HeapNode> 
		 * Complexity (WC): O(logn)
		 * complexity (amortized): O(1)
		 * @param bucketList
		 * @return bucketlist updated with current node after linking (if necessary)
		 */
		public HashMap<Integer, HeapNode> linking(HashMap<Integer, HeapNode> bucketList) {
			HeapNode node = this;
			int rank = this.getRank(); 
			if (!bucketList.containsKey(rank)) {
				bucketList.put(rank, node);
				return bucketList;
			}
			node = node.chainedLink(bucketList.get(rank));
			bucketList.remove(rank);
			return node.linking(bucketList);
		}

		/**public HeapNode chainedLink(HeapNode heapNode)
		 * concatenates 2 nodes and adds 1 to total links
		 * precondition: type(heapNode)==HeapNode
		 * postcondition: type(@res)==HeapNode
		 * Complexity (WC and Amortized)= O(1)
		 * @param heapNode
		 * @return the node with minimal value (which would be the root of the new tree)
		 */
		public HeapNode chainedLink(HeapNode heapNode) {
			HeapNode minNode;
			HeapNode childNode;
			if (this.getKey() < heapNode.getKey()) {
				minNode = this;
				childNode = heapNode;
			} else {
				minNode = heapNode;
				childNode = this;
			}
			if (minNode.getRank()==0) {//initial case, avoid null next and prev
				minNode.setNext(minNode);
				minNode.setPrev(minNode);
				childNode.setNext(childNode);
				childNode.setPrev(childNode);
			}
			if (minNode.getChild()!=null) {
				childNode.setPrev(minNode.getChild().getPrev());
				minNode.getChild().setPrev(childNode);
				childNode.setNext(minNode.getChild());
				childNode.getPrev().setNext(childNode);
			}
			minNode.setChild(childNode);
			childNode.setParent(minNode);
			minNode.setRank(minNode.getRank() + 1);
			FibonacciHeap.totalLinks++;
			return minNode;
		}

		/**public void disconnectChildren()
		 * Disconnects a node's children from the node (sets their parent to be null)
		 * precondition: this.getChild()!=null
		 * postcondition: none
		 * Complexity (WC): O(logn)
		 */
		public void disconnectChildren() {
			HeapNode node = this.getChild();
			int key = node.getKey();
			int pivotKey = -1;
			while (key != pivotKey) {
				node.setParent(null);
				node = node.getNext();
				pivotKey = node.getKey();
			}
		}

		/**public void arrangeSiblings()
		 * Disconnects a node from its siblings, and connects its siblings within themselves
		 * precondition: this.getNext()!=null&&this.getPrev()!=null
		 * postcondition: the node is disconnected from its siblings
		 * Complexity (WC and Amortized): O(1)
		 */
		public void arrangeSiblings() {
			this.getNext().setPrev(this.getPrev());
			this.getPrev().setNext(this.getNext());
			this.setNext(this);
			this.setPrev(this);
		}

		/**
		 * public boolean isOnlyChild
		 * Checks if a node is an only child
		 * precondition: this.getNext()!=null
		 * postcondition: true iff the node is an only child
		 * Complexity (WC and Amortized): O(1)
		 * @return true iff this.getNext().getKey() == this.getKey()
		 */
		public boolean isOnlyChild() {
			return (this.getNext().getKey() == this.getKey());
		}

		/**
		 * public boolean isLeftChild
		 * Checks if a node is a left child (meaning that the node's parent child pointer is the node)
		 * precondition: this.getParent()!=null
		 * postcondition: true iff the node is a left child
		 * Complexity (WC and Amortized): O(1)
		 * @return true iff this.getParent().getChild().getKey() == this.getKey()
		 */
		public boolean isLeftChild() {
			return (this.getParent().getChild().getKey() == this.getKey());
		}

		/**
		 * public int getKey()
		 * returns the node's key field
		 * precondition: the node's key is initialized
		 * postcondition: type(@res)==int
		 * Complexity (WC and Amortized): O(1)
		 * @return the node's key
		 */
		public int getKey() {
			return this.key;
		}

		/**public void updateKey(int k)
		 * decreases the node's key by k
		 * precondition: type(k)==int
		 * postcondition: the key is decreased by k
		 * Complexity (WC and Amortized): O(1)
		 * @param k
		 */
		public void updateKey(int k) {
			this.key -= k;
		}

		/**
		 * public int getRank()
		 * returns the node's rank field
		 * precondition: the node's rank is initialized
		 * postcondition: type(@res)==int
		 * Complexity (WC and Amortized): O(1)
		 * @return the node's rank
		 */
		public int getRank() {
			return this.rank;
		}

		/**public void setRank(int r)
		 * Sets the node's rank to be r
		 * precondition: type(r)==int
		 * postcondition: the node rank isr
		 * Complexity (WC and Amortized): O(1)
		 * @param r
		 */
		public void setRank(int r) {
			this.rank = r;
		}

		/**
		 * public int getMark()
		 * returns the node's mark field (@res==1 iff node is marked)
		 * precondition: nothing is in parenthesis
		 * postcondition: type(@res)==int
		 * Complexity (WC and Amortized): O(1)
		 * @return the node's mark
		 */
		public int getMark() {
			return this.mark;
		}

		/**public void setMark()
		 * Sets the node's mark to be the opposite: if the node was marked, it would be not marked
		 * If the node wasn't marked, than it would be marked
		 * precondition: nothing in parenthesis
		 * postcondition: node is marked iff node.mark==1
		 * Complexity (WC and Amortized): O(1)
		 */
		public void setMark() {
			this.mark = 1 - this.mark;
		}

		/**
		 * public HeapNode getChild()
		 * returns the node's child field
		 * precondition: nothing in parenthesis
		 * postcondition: type(@res)==HeapNode||@res==null
		 * Complexity (WC and Amortized): O(1)
		 * @return the node's child
		 */
		public HeapNode getChild() {
			return this.child;
		}

		/**public void setChild(HeapNode node)
		 * Sets the node's child to be node
		 * precondition: type(node)==HeapNode
		 * postcondition: the node's child is node
		 * Complexity (WC and Amortized): O(1)
		 * @param node
		 */
		public void setChild(HeapNode node) {
			this.child = node;
		}

		/**
		 * public HeapNode getNext()
		 * returns the node's next field
		 * precondition: nothing in parenthesis
		 * postcondition: type(@res)==HeapNode||@res==null
		 * Complexity (WC and Amortized): O(1)
		 * @return the node which comes after the current node
		 */
		public HeapNode getNext() {
			return this.next;
		}

		/**public void setChild(HeapNode node)
		 * Sets the node's next field to be node
		 * precondition: type(node)==HeapNode
		 * postcondition: the node's next is node
		 * Complexity (WC and Amortized): O(1)
		 * @param node
		 */
		public void setNext(HeapNode node) {
			this.next = node;
		}

		/**
		 * public HeapNode getParent()
		 * returns the node's parent field
		 * precondition: nothing in parenthesis
		 * postcondition: type(@res)==HeapNode||@res==null
		 * Complexity (WC and Amortized): O(1)
		 * @return the node's parent node
		 */
		public HeapNode getParent() {
			return this.parent;
		}

		/**public void setParent(HeapNode node)
		 * Sets the node's parent field to be node
		 * precondition: type(node)==HeapNode
		 * postcondition: the node's parent is node
		 * Complexity (WC and Amortized): O(1)
		 * @param node
		 */
		public void setParent(HeapNode node) {
			this.parent = node;
		}

		/**
		 * public HeapNode getPrev()
		 * returns the node's prev field
		 * precondition: nothing in parenthesis
		 * postcondition: type(@res)==HeapNode||@res==null
		 * Complexity (WC and Amortized): O(1)
		 * @return the node which comes before the current node
		 */
		public HeapNode getPrev() {
			return this.prev;
		}

		/**public void setPrev(HeapNode node)
		 * Sets the node's prev field to be node
		 * precondition: type(node)==HeapNode
		 * postcondition: the node 'before' this node is node
		 * Complexity (WC and Amortized): O(1)
		 * @param node
		 */
		public void setPrev(HeapNode node) {
			this.prev = node;
		}
		
	}
	
	
}
