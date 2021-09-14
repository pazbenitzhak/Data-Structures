/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with distinct integer keys and info
 *
 */

//Rai Pheterson, 311214852, raipheterson
//Guy Paz Ben Itzhak, 315328963, pazbenitzhak

public class AVLTree {

	private IAVLNode root;

	/**
	 * public AVLTree(IAVLNode node) the AVLTree's empty constructor precondition:
	 * nothing in parenthesis postcondition: sets a new empty AVLTree Complexity:
	 * 0(1)
	 * 
	 * @param node
	 */
	public AVLTree() {
		this.root = null;
	}

	/**
	 * public AVLTree(IAVLNode node) the AVLTree's constructor precondition: node in
	 * parenthesis postcondition: sets a new AVLTree Complexity: 0(1)
	 * 
	 * @param node
	 */
	public AVLTree(IAVLNode node) {
		this.root = node;
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		return (this.root == null); // to be replaced by student code
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree otherwise,
	 * returns null
	 */
	public String search(int k) {
		if (this.empty()) {
			return null;
		}
		if (k == this.root.getKey()) {
			return this.root.getValue();
		}
		if (k > this.root.getKey()) {
			AVLNode rightNode = (AVLNode) this.root.getRight();
			return rightNode.searchRec(k);
		} else {
			AVLNode leftNode = (AVLNode) this.root.getLeft();
			return leftNode.searchRec(k);
		}
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree. the tree must remain
	 * valid (keep its invariants). returns the number of rebalancing operations, or
	 * 0 if no rebalancing operations were necessary. promotion/rotation - counted
	 * as one rebalance operation, double-rotation is counted as 2. returns -1 if an
	 * item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {
		if (this.empty()) {// empty tree
			this.root = new AVLNode(k, i, 0); // set node as tree's root
			return 0;
		}
		AVLNode root = (AVLNode) this.root;
		AVLNode parent = root.insFindNode(k);
		if (parent == null) { // nothing to insert
			return -1;
		}
		AVLNode newNode = new AVLNode(k, i, 0);
		if (!parent.isLeaf()) { 
			return this.caseB(newNode, parent);
		} else {
			return this.caseA(newNode, parent);
		}
	}

	/**
	 * public int caseB(AVLNode newNode, AVLNode parent)
	 *
	 * submethod of insert. inserts newNode as a right or left child of parent.
	 * called upon only if parent is an unary node. having no rebalance operations
	 * conducted, return 0.
	 */
	public int caseB(AVLNode newNode, AVLNode parent) {
		if (newNode.getKey() > parent.getKey()) {
			parent.setRight(newNode);
			newNode.setParent(parent);
		} else {
			parent.setLeft(newNode);
			newNode.setParent(parent);
		}
		parent.setSize();
		parent.updateSize();
		return 0;
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. demotion/rotation
	 * - counted as one rebalance operation, double-rotation is counted as 2.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		if (this.empty()) {// empty tree, nothing to erase
			return -1;
		}
		AVLNode root = (AVLNode) this.root;
		AVLNode node = (AVLNode) root.delFindNode(k);
		if (node == null) {// no node with key==k in this tree
			return -1;
		}
		char side = node.parentSide();
		boolean rootTerm = (node.getLeft().getKey() != -1 && node.getRight().getKey() != -1);
		if (side == 'N' && (rootTerm == false)) {
			return this.deleteRoot(node);
		}
		if (side == 'L') {
			if (node.isLeaf()) {
				int leftEdge = node.parent.getHeight() - node.getHeight();
				int rightEdge = node.parent.getHeight() - node.parent.getRight().getHeight();
				AVLNode parent = (AVLNode) node.parent;
				if (leftEdge == 1 && rightEdge == 1) {// first case ("easy one", just delete)
					node.parent.setLeft(new AVLNode());
					node.setParent(null);
					parent.updateSize();
					return 0;
				}
				if (leftEdge == 1 && rightEdge == 2) {
					node.parent.setLeft(new AVLNode());
					node.setParent(null);
					parent.setHeight(parent.getHeight() - 1);
					parent.setSize();
					return this.delRecTwos(parent);
				} else {// leftEdge==2&&rightEdge==1
					node.parent.setLeft(new AVLNode());
					node.setParent(null);
					return this.delRecTriOne(parent, side);
				}
			}
			if ((node.left != null && node.right == null) || (node.left == null && node.right != null)) {// node is //
																											// unary
				int leftEdge = node.parent.getHeight() - node.getHeight();
				int rightEdge = node.parent.getHeight() - node.parent.getRight().getHeight();
				if ((leftEdge == 1 && rightEdge == 1)) {
					if (node.left != null) {
						AVLNode parent = node.delUnaryLeft();
						parent.updateSize();
						return 0;
					} else {// node.right!=null
						AVLNode parent = node.delUnaryRight();
						parent.updateSize();
						return 0;
					}
				}

				if ((leftEdge == 1 && rightEdge == 2)) {
					if (node.left != null) {
						return this.delRecTwos(node.delUnaryLeft());
					} else {// node.right!=null
						return this.delRecTwos(node.delUnaryRight());
					}
				} else {// leftEdge==2&&rightEdge==1
					if (node.left != null) {
						return this.delRecTriOne(node.delUnaryLeft(), side);
					} else {// node.right!=null
						return this.delRecTriOne(node.delUnaryRight(), side);
					}
				}
			}

		}

		if (side == 'R') {
			if (node.isLeaf()) {
				int rightEdge = node.parent.getHeight() - node.getHeight();
				int leftEdge = node.parent.getHeight() - node.parent.getLeft().getHeight();
				AVLNode parent = (AVLNode) node.parent;
				if (leftEdge == 1 && rightEdge == 1) {// first case ("easy one", just delete)
					node.parent.setRight(new AVLNode());
					node.setParent(null);
					parent.updateSize();
					return 0;
				}
				if (leftEdge == 2 && rightEdge == 1) {
					node.parent.setRight(new AVLNode());
					node.setParent(null);
					parent.setHeight(parent.getHeight() - 1);
					parent.setSize();
					return this.delRecTwos(parent);
				} else {// leftEdge==1&&rightEdge==2
					node.parent.setRight(new AVLNode());
					node.setParent(null);
					return this.delRecTriOne(parent, side);
				}
			}
			if ((node.getLeft().getHeight() != -1 && node.getRight().getHeight() == -1)
					|| (node.getLeft().getHeight() == -1 && node.getRight().getHeight() != -1)) {// node is unary
				int rightEdge = node.parent.getHeight() - node.getHeight();
				int leftEdge = node.parent.getHeight() - node.parent.getLeft().getHeight();
				if ((leftEdge == 1 && rightEdge == 1)) {
					if (node.left != null) {
						AVLNode parent = node.delUnaryLeft();
						parent.updateSize();
						return 0;
					} else {// node.right!=null
						AVLNode parent = node.delUnaryRight();
						parent.updateSize();
						return 0;
					}
				}

				if ((leftEdge == 2 && rightEdge == 1)) {
					if (node.left != null) {
						return this.delRecTwos(node.delUnaryLeft());
					} else {// node.right!=null
						return this.delRecTwos(node.delUnaryRight());
					}
				} else {// leftEdge==1&&rightEdge==2
					if (node.left != null) {
						return this.delRecTriOne(node.delUnaryLeft(), side);
					} else {// node.right!=null
						return this.delRecTriOne(node.delUnaryRight(), side);
					}
				}
			}

		}

		// we get here only if node is binary, and thus not going through any other
		// submethod
		AVLNode successor = this.findSuccessor(node); // successor must be unary/leaf
		if (node.checkRoot()) {
			this.root = successor;
		}
		int numOp = this.delete(successor.key);
		successor.setHeight(node.getHeight());
		successor.size = node.getSize();
		successor.setRight(node.getRight());
		node.right.setParent(successor);
		node.setRight(null);
		successor.setLeft(node.getLeft());
		node.left.setParent(successor);
		node.setLeft(null);
		successor.setParent(node.getParent());
		if (node.parentSide() == 'L') {
			node.parent.setLeft(successor);
		} else if (node.parentSide() == 'R') {// node.parentSide()=='R'
			node.parent.setRight(successor);
		}
		node.setParent(null);
		return numOp;

	}

	/**
	 * public int deleteRoot(AVLNode node)
	 *
	 * submethod of delete method, deletes the root of the tree if it is a leaf or
	 * an unary node returns 0
	 */

	public int deleteRoot(AVLNode node) { // root is unary node or leaf
		if (node.isLeaf()) {
			this.root = null;
		} else { // root is unary
			if (node.getRight().getHeight() != -1) { // root has a right child
				this.root = node.getRight();
				node.getRight().setParent(null);
				node.setRight(null);
			} else { // root has a left child
				this.root = node.getLeft();
				node.getLeft().setParent(null);
				node.setLeft(null);
			}
		}
		return 0;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min() {
		if (this.empty()) {
			return null;
		}
		AVLNode min = (AVLNode) this.root;
		while (min.getLeft().getHeight() != -1) {
			min = (AVLNode) min.getLeft();
		}
		return min.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 */
	public String max() {
		if (this.empty()) {
			return null;
		}
		AVLNode max = (AVLNode) this.root;
		while (max.getRight().getHeight() != -1) {
			max = (AVLNode) max.getRight();
		}
		return max.getValue();
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 */
	public int[] keysToArray() {
		if (this.empty()) {
			return new int[0];
		}
		int[] inOrderKeys = new int[this.size()];
		AVLNode node = this.findMin();
		int index = 0;
		while (index != inOrderKeys.length) {
			inOrderKeys[index] = node.getKey();
			node = this.findSuccessor(node);
			index += 1;
		}
		return inOrderKeys;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		if (this.empty()) {
			return new String[0];
		}
		String[] inOrderInfo = new String[this.size()];
		AVLNode node = this.findMin();
		int index = 0;
		while (index != inOrderInfo.length) {
			inOrderInfo[index] = node.getValue();
			node = this.findSuccessor(node);
			index += 1;
		}
		return inOrderInfo;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */
	public int size() {
		if (this.root == null) {
			return 0;
		}
		return this.root.getSize(); 
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none postcondition: none
	 */
	public IAVLNode getRoot() {
		return this.root;
	}

	/**
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. Returns an array [t1,
	 * t2] with two AVL trees. keys(t1) < x < keys(t2). precondition: search(x) !=
	 * null (i.e. you can also assume that the tree is not empty) postcondition:
	 * none
	 */
	public AVLTree[] split(int x) {
		AVLNode root = (AVLNode) this.root;
		AVLNode node = root.delFindNode(x);
		AVLTree leftTree = new AVLTree(node.getLeft());
		AVLTree rightTree = new AVLTree(node.getRight());
		AVLNode pivot = (AVLNode) node.getParent();
		while (pivot != null) {
			char side = node.parentSide();
			if (side == 'L') {
				leftTree.join(pivot, new AVLTree(pivot.getLeft()));
			} else {// if side =='R'
				rightTree.join(pivot, new AVLTree(pivot.getRight()));

			}
			node = pivot;
			if (!pivot.checkRoot()) {
				pivot = (AVLNode) pivot.getParent();
			} else {
				pivot = null;
			}
		}

		AVLTree[] arrAVL = new AVLTree[2];
		arrAVL[0] = leftTree;
		arrAVL[1] = rightTree;

		return arrAVL;
	}

	/**
	 * public join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. Returns the complexity of the operation
	 * (|tree.rank - t.rank| + 1). precondition: keys(x,t) < keys() or keys(x,t) >
	 * keys(). t/tree might be empty (rank = -1). postcondition: none
	 */
	public int join(IAVLNode x, AVLTree t) {
		if (this.empty() && t.empty()) {
			this.root = x;
			return 1;
		}

		if (this.empty()) {// t is not empty
			int comp = t.getRoot().getHeight() + 1;
			t.insert(x.getKey(), x.getValue());
			this.root = t.root;
			return comp;
		}

		if (t.empty()) {// this is not empty
			int comp = this.getRoot().getHeight() + 1;
			this.insert(x.getKey(), x.getValue());
			return comp;
		}

		int thisRank = this.getRoot().getHeight();
		int otherRank = t.getRoot().getHeight();
		if (thisRank == otherRank) {
			if (this.getRoot().getKey() < x.getKey()) {
				x.setLeft(this.getRoot());
				this.getRoot().setParent(x);
				x.setRight(t.getRoot());
				t.getRoot().setParent(x);
				x.setHeight(x.getLeft().getHeight() + 1);
				this.root = x;
			} else {// this.getRoot().getKey() > x.getKey()
				x.setRight(this.getRoot());
				this.getRoot().setParent(x);
				x.setLeft(t.getRoot());
				t.getRoot().setParent(x);
				x.setHeight(x.getRight().getHeight() + 1);
				this.root = x;
			}
			return (Math.abs(thisRank - otherRank) + 1);
		} // if we got here, than the trees aren't in the same height
		boolean newIsHigher;
		boolean newIsBigger;
		newIsHigher = (this.getRoot().getHeight() < t.getRoot().getHeight());
		newIsBigger = (this.getRoot().getKey() < this.getRoot().getKey());
		AVLNode tempX = (AVLNode) x;
		if (newIsHigher && newIsBigger) {
			t.joinByLeft(tempX, this, 'R');
		}
		if (!newIsHigher && !newIsBigger) {
			this.joinByLeft(tempX, t, 'L');
		}
		if (newIsHigher && !newIsBigger) {
			t.joinByRight(tempX, this, 'R');
		}
		if (!newIsHigher && newIsBigger) {
			this.joinByRight(tempX, t, 'L');
		}
		return (Math.abs(thisRank - otherRank) + 1);
	}

	/**
	 * public int CaseA(AVLNode node, char side) a method that checks all of the
	 * 'problematic cases' and acts accordingly The method uses other methods for
	 * help, and updates each node's size precondition: node==null,
	 * side=='L'||side=='R' postcondition: returns int>0
	 * 
	 * @param node
	 * @param parent
	 * @return number of rebalancing operations Complexity: O(log(n))
	 */
	public int caseA(AVLNode node, AVLNode parent) {
		if (node.getKey() > parent.getKey()) {
			parent.setRight(node);
			node.setParent(parent);
		} else {
			parent.setLeft(node);
			node.setParent(parent);
		}
		parent.setSize();
		parent.setHeight(parent.getHeight() + 1);
		if (parent.parentSide() == 'N') {
			return 1;
		}
		char sideOfNode = node.parentSide();
		int bottomLeftEdge;
		if (sideOfNode == 'L') {
			bottomLeftEdge = 1;
		} else { // side of node is 'R'
			bottomLeftEdge = 2;
		}
		return 1 + this.case1((AVLNode) parent, bottomLeftEdge);
	}

	/**
	 * public int Case1(AVLNode node, int bottomLeftEdge) a method that checks all
	 * of the 'problematic cases' and acts accordingly The method uses other methods
	 * for help, and updates each node's size precondition:
	 * node!=null,bottomLeftEdge==1||bottomLeftEdge==2 postcondition: returns int>0
	 * 
	 * @param node
	 * @param parent
	 * @return number of rebalancing operations Complexity: O(log(n))
	 */

	public int case1(AVLNode node, int bottomLeftEdge) {
		char sideOfNode = node.parentSide();
		if (sideOfNode == 'N') {
			node.setSize();
			return 0;
		}
		if (node.getParent().getHeight() - node.getHeight() == 1) {
			node.setSize();
			node.updateSize();
			return 0;
		}
		if (sideOfNode == 'L') {
			int rightEdge = node.parent.getHeight() - node.parent.getRight().getHeight();
			if (rightEdge == 1) {
				node.parent.setHeight(node.parent.getHeight() + 1);
				node.parent.setSize();
				bottomLeftEdge = 1;
				return 1 + this.case1((AVLNode) node.parent, bottomLeftEdge);
			} else { // rightEdge == 2
				if (bottomLeftEdge == 1) { // case 2 - single rotation
					int opNum = this.singleRotation(node, sideOfNode);
					node.updateSize();
					return opNum;
				} else { // B.L.Edge == 2, case 3, double rotation
					int opNum = this.doubleRotation((AVLNode) node.getRight(), sideOfNode);
					node.updateSize();
					return opNum;
				}
			}
		} else { // sideOfNode == 'R'
			int leftEdge = node.parent.getHeight() - node.parent.getLeft().getHeight();
			if (leftEdge == 1) {
				node.parent.setHeight(node.parent.getHeight() + 1);
				node.parent.setSize();
				bottomLeftEdge = 2;
				return 1 + this.case1((AVLNode) node.parent, bottomLeftEdge);
			} else { // leftEdge == 2
				if (bottomLeftEdge == 2) { // case 2 - single rotation
					int opNum = this.singleRotation(node, sideOfNode);
					node.updateSize();
					return opNum;
				} else { // B.L.Edge == 1, case 3, double rotation
					int opNum = this.doubleRotation((AVLNode) node.getLeft(), sideOfNode);
					node.updateSize();
					return opNum;
				}
			}
		}
	}

	/**
	 * public int singleRotation(AVLNode node, char side)
	 * 
	 * makes a single rotation on a given node precondition: side=='L' || side=='R'
	 * postcondition: res == 2
	 * 
	 * @param node
	 * @param side Complexity: O(1)
	 * @return 2
	 */
	public int singleRotation(AVLNode node, char side) {
		if (side == 'L') {
			char pSide = ((AVLNode) node.getParent()).parentSide();
			node.parent.setLeft(node.right);
			node.right.setParent(node.parent);
			node.setRight(node.parent);
			node.setParent(node.getRight().getParent());
			node.right.setParent(node);
			if (pSide == 'L') {
				node.getParent().setLeft(node);
			} else if (pSide == 'R') {
				node.getParent().setRight(node);
			}
			node.right.setHeight(node.right.getHeight() - 1);
			if (node.checkRoot()) {// we need to update root pointer
				this.root = node;
			}
			node.right.setSize();
			node.setSize();
			return 2;
		}

		else {// side == 'R'
			char pSide = ((AVLNode) node.getParent()).parentSide();
			node.parent.setRight(node.left);
			node.left.setParent(node.parent);
			node.setLeft(node.parent);
			node.setParent(node.getLeft().getParent());
			node.left.setParent(node);
			if (pSide == 'L') {
				node.getParent().setLeft(node);
			} else if (pSide == 'R') {
				node.getParent().setRight(node);
			}
			node.left.setHeight(node.left.getHeight() - 1);
			if (node.checkRoot()) {// we need to update root pointer
				this.root = node;
			}
			node.left.setSize();
			node.setSize();
			return 2;
		}
	}

	/**
	 * public int doubleRotation(AVLNode node, char side) makes a double rotation on
	 * the node (levels it up) precondition = side=='L' || side =='R' postcondition:
	 * res==5
	 * 
	 * @param node
	 * @param side Complexity: O(1)
	 * @return 5
	 */
	public int doubleRotation(AVLNode node, char side) {
		if (side == 'L') {
			this.singleRotation(node, 'R');
			this.singleRotation(node, 'L');
		} else {// side=='R'
			this.singleRotation(node, 'L');
			this.singleRotation(node, 'R');
		}
		node.setHeight(node.getHeight() + 1);
		if (node.checkRoot()) {// we need to update root pointer
			this.root = node;
		}
		return 5;
	}

	/**
	 * public AVLNode findSuccessor(AVLNode node)
	 * 
	 * A method that finds a node's successor in a tree precondition: none
	 * postcondition: returns an AVLNode
	 * 
	 * @param node O(log(n))
	 * @return an AVLNode, the node's successor
	 */
	public AVLNode findSuccessor(IAVLNode node) {

		if (node.getRight().getHeight() != -1) {
			AVLTree subtree = new AVLTree(node.getRight());
			return subtree.findMin();
		}
		AVLNode parent = (AVLNode) node.getParent();
		AVLNode ourNode = (AVLNode) node;
		while (parent.getHeight() != -1 && parent == ourNode.getParent() && ourNode.parentSide() != 'L') {
			ourNode = parent;
			parent = (AVLNode) ourNode.getParent();
		}
		return parent;
	}

	/**
	 * public int delRecTwos(AVLNode node, char side)
	 * 
	 * This method takes care of the (2,2) rank differences cases, and uses other
	 * methods accordingly precondition: side=='R' || side=='L', we got a (2,2) case
	 * postcondition: return a non=negative int Complexity: O(log(n))
	 * 
	 * @param node
	 * @param side
	 * @return number of rebalancing actions
	 */
	public int delRecTwos(AVLNode node) { // demote already done
		if (node == this.root) {
			return 1;
		}
		char pSide = node.parentSide();
		if (pSide == 'L') {
			int leftEdge = node.getParent().getHeight() - node.getHeight();
			int rightEdge = node.getParent().getHeight() - node.getParent().getRight().getHeight();
			if (leftEdge == 2 && rightEdge == 1) { // all is good and we can finish
				node.updateSize();
				return 0;
			}
			if (leftEdge == 2 && rightEdge == 2) {
				node.getParent().setHeight(node.getParent().getHeight() - 1);
				node.getParent().setSize();
				return 1 + this.delRecTwos((AVLNode) node.parent);
			} else {// leftEdge==3
				node.setSize();
				return 1 + this.delRecTriOne((AVLNode) node.getParent(), pSide);
			}

		} else {// side=='R'
			int rightEdge = node.getParent().getHeight() - node.getHeight();
			int leftEdge = node.getParent().getHeight() - node.getParent().getLeft().getHeight();
			if (rightEdge == 2 && leftEdge == 1) {// all is good and we can finish
				node.updateSize();
				return 0;
			}
			if (rightEdge == 2 && leftEdge == 2) {
				node.getParent().setHeight(node.getParent().getHeight() - 1);
				node.getParent().setSize();
				return 1 + this.delRecTwos((AVLNode) node.parent);
			} else { // leftEdge == 3
				return 1 + this.delRecTriOne((AVLNode) node.getParent(), pSide);
			}
		}
	}

	/**
	 * public int delRecTwos(AVLNode node, char side)
	 * 
	 * This method takes care of the (3,1) rank differences cases, and uses other
	 * methods accordingly precondition: side=='R' || side=='L', we got a (3,1) case
	 * postcondition: return a non-negative int Complexity: O(log(n))
	 * 
	 * @param node
	 * @param side
	 * @return number of rebalancing actions
	 */
	public int delRecTriOne(AVLNode node, char side) {
		if (side == 'L') {
			int rightEdge = node.right.getHeight() - node.right.getRight().getHeight();
			int leftEdge = node.right.getHeight() - node.right.getLeft().getHeight();
			if (rightEdge == leftEdge && rightEdge == 1) {// case 2\
				AVLNode rightChild = (AVLNode) node.right;
				int numOp = this.singleRotation(rightChild, 'R');
				node.parent.setHeight(node.parent.getHeight() + 1);
				node.setSize();
				node.updateSize();
				return numOp + 1;
			}
			if (leftEdge == 2) {// case 3
				AVLNode rightChild = (AVLNode) node.right;
				int numOp = this.singleRotation(rightChild, 'R');
				node.setHeight(node.getHeight() - 1); // demote 'z'
				AVLNode parent = (AVLNode) node.parent;
				parent.setSize();
				return numOp + this.delRecTwos(parent);
			} else {// leftEdge==1, case 4
				AVLNode rightsLeftChild = (AVLNode) node.getRight().getLeft();
				int numOp = this.doubleRotation(rightsLeftChild, 'R');
				node.setHeight(node.getHeight() - 1); // demote 'z'
				AVLNode parent = (AVLNode) node.parent;
				node.setSize();
				parent.getRight().setSize();
				parent.setSize();
				return numOp + this.delRecTwos(parent);
			}

		}

		else { // side='R'
			int leftEdge = node.left.getHeight() - node.left.getLeft().getHeight();
			int rightEdge = node.left.getHeight() - node.left.getRight().getHeight();
			if (rightEdge == leftEdge && rightEdge == 1) {// case 2\
				AVLNode leftChild = (AVLNode) node.left;
				int numOp = this.singleRotation(leftChild, 'L');
				node.parent.setHeight(node.parent.getHeight() + 1);
				node.setSize();
				node.updateSize();
				return numOp + 1;
			}
			if (rightEdge == 2) {// case 3
				AVLNode leftChild = (AVLNode) node.left;
				int numOp = this.singleRotation(leftChild, 'L');
				node.setHeight(node.getHeight() - 1); // demote 'z'
				AVLNode parent = (AVLNode) node.parent;
				parent.setSize();
				return numOp + this.delRecTwos(parent);
			} else {// rightEdge==1, leftEdge==2, case 4
				AVLNode leftsRightChild = (AVLNode) node.getLeft().getRight();
				int numOp = this.doubleRotation(leftsRightChild, 'L');
				node.setHeight(node.getHeight() - 1); // demote 'z'
				AVLNode parent = (AVLNode) node.parent;
				node.setSize();
				parent.getLeft().setSize();
				parent.setSize();
				return numOp + this.delRecTwos(parent);
			}
		}
	}

	/**
	 * public AVLNode findMin()
	 * 
	 * Finds the node with the minimal key in the tree, and returns it precondition:
	 * none postcondition: an AVLNode or null Complexity: O(log(n))
	 * 
	 * @return node with minimal key in tree
	 */
	public AVLNode findMin() {
		if (this.empty()) {
			return null;
		}
		AVLNode min = (AVLNode) this.root;
		while (min.getLeft().getHeight() != -1) {
			min = (AVLNode) min.getLeft();
		}
		return min;
	}

	/**
	 * public void joinByLeft(AVLNode node, AVLTree hTree, char parentTree)
	 * 
	 * A function that contacenates two trees, when the taller has the bigger keys
	 * precondition: parentTree=='L'||parentTree=='R' postcondition: none
	 * Complexity: O(log(n))
	 * 
	 * @param node
	 * @param hTree
	 * @param parentTree
	 */
	public void joinByLeft(AVLNode node, AVLTree hTree, char parentTree) {
		AVLNode bNode = (AVLNode) hTree.getRoot();
		while (bNode.getHeight() > this.getRoot().getHeight()) { // connecting the trees
			bNode = (AVLNode) bNode.getLeft();
		}
		node.setHeight(this.getRoot().getHeight() + 1);
		node.setLeft(this.getRoot());
		this.getRoot().setParent(node);
		node.setRight(bNode);
		node.setParent(bNode.getParent());
		node.getParent().setLeft(node);
		node.getRight().setParent(node);
		// end of concatenation
		if (node.getHeight() - node.getRight().getHeight() == 2) {
			hTree.case1(node, 1);
			if (parentTree == 'L') {
				this.root = hTree.getRoot();
			}
			return;
		}
		if (node.getParent().getHeight() - node.getHeight() == 1) {
			if (parentTree == 'L') {// higher tree is t, not our tree
				this.root = hTree.getRoot();// we need to change our tree's root to the higher tree
			}
			return;
		}
		if (node.getParent().getHeight() - node.getParent().getRight().getHeight() == 2) {
			hTree.singleRotation(node, 'L');
			if (parentTree == 'L') {
				this.root = hTree.getRoot();
			}
			node.setHeight(node.getHeight() + 1);
			node.getRight().setHeight(node.getRight().getHeight() + 1);
			return;
		}

		if (node.getParent().getHeight() - node.getParent().getRight().getHeight() == 1) {
			hTree.case1(node, 1);
			if (parentTree == 'L') {
				this.root = hTree.getRoot();
			}
			return;
		}
	}

	/**
	 * public void joinByRight(AVLNode node, AVLTree hTree, char parentTree)
	 * 
	 * A function that contacenates two trees, when the taller has the smaller keys
	 * precondition: parentTree=='L'||parentTree=='R' postcondition: none
	 * Complexity: O(log(n))
	 * 
	 * @param node
	 * @param hTree
	 * @param parentTree
	 */
	public void joinByRight(AVLNode node, AVLTree lTree, char parentTree) {
		AVLNode bNode = (AVLNode) this.getRoot();
		while (bNode.getHeight() > lTree.getRoot().getHeight()) { // connecting the trees
			bNode = (AVLNode) bNode.getRight();
		}
		node.setHeight(lTree.getRoot().getHeight() + 1);
		node.setRight(lTree.getRoot());
		lTree.getRoot().setParent(node);
		node.setLeft(bNode);
		node.setParent(bNode.getParent());
		node.getParent().setRight(node);
		node.getLeft().setParent(node);
		// end of concatenation
		if (node.getHeight() - node.getLeft().getHeight() == 2) {
			this.case1(node, 2);
			if (parentTree == 'R') {
				lTree.root = this.root;
			}
			return;
		}
		if (node.getParent().getHeight() - node.getHeight() == 1) {
			if (parentTree == 'R') {// higher tree is t, not our tree
				lTree.root = this.getRoot();// we need to change our tree's root to the higher tree
			}
			return;
		}
		if (node.getParent().getHeight() - node.getParent().getLeft().getHeight() == 2) {
			this.singleRotation(node, 'R');
			if (parentTree == 'R') {
				lTree.root = this.root;
			}
			node.setHeight(node.getHeight() + 1);
			node.getLeft().setHeight(node.getLeft().getHeight() + 1);
			return;
		}

		if (node.getParent().getHeight() - node.getParent().getLeft().getHeight() == 1) {
			this.case1(node, 2);
			if (parentTree == 'R') {
				lTree.root = this.root;
			}
			return;
		}
	}

	/**
	 * public interface IAVLNode ! Do not delete or modify this - otherwise all
	 * tests will fail !
	 */
	public interface IAVLNode {
		public int getKey(); // returns node's key (for virtuval node return -1)

		public String getValue(); // returns node's value [info] (for virtuval node return null)

		public void setLeft(IAVLNode node); // sets left child

		public IAVLNode getLeft(); // returns left child (if there is no left child return null)

		public void setRight(IAVLNode node); // sets right child

		public IAVLNode getRight(); // returns right child (if there is no right child return null)

		public void setParent(IAVLNode node); // sets parent

		public IAVLNode getParent(); // returns the parent (if there is no parent return null)

		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node

		public void setHeight(int height); // sets the height of the node

		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)

		public void setSize();

		public int getSize();
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree (for example AVLNode), do
	 * it in this file, not in another file. This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode {

		private int key;
		private String val;
		private IAVLNode left;
		private IAVLNode right;
		private IAVLNode parent;
		private int rank;
		private int size;

		/**
		 * public AVLNode()
		 * 
		 * the 'empty' builder, constructs an empty node precondition: no parameters in
		 * parenthesis postcondition: get an empty node Complexity: O(1)
		 */
		public AVLNode() {
			this.key = -1;
			this.val = null;
			this.left = null;
			this.right = null;
			this.parent = null;
			this.rank = -1;
			this.size = 0;
		}

		/**
		 * public AVLNode(int nKey, String nValue, int nRank)
		 * 
		 * the actual builder, constructs a new node with parameters: key, value(info)
		 * and rank left child, right child and parent are defined to be 'empty' nodes
		 * size is determined by the SetSize method predondition: all type(nKey)==int &&
		 * type(nValue)==String && type(nRank)==int postcondition: get a new AVLNode
		 * Complexity: O(1)
		 * 
		 * @param nKey   = the Node's key
		 * @param nValue = represents the Node's info
		 * @param nRank  = the Node's rank/height
		 * 
		 */

		public AVLNode(int nKey, String nValue, int nRank) {
			this.key = nKey;
			this.val = nValue;
			this.left = new AVLNode();
			this.right = new AVLNode();
			this.parent = new AVLNode();
			this.rank = nRank;
			this.size = 1;
		}

		/**
		 * public int getKey()
		 * 
		 * returns the node's key precondition: none postcondition: none Complexity:
		 * O(1)
		 * 
		 */
		public int getKey() {
			return this.key; // to be replaced by student code
		}

		/**
		 * public String getValue()
		 * 
		 * returns the node's info precondition: none postcondition: none Complexity:
		 * O(1)
		 * 
		 */
		public String getValue() {
			return this.val; // to be replaced by student code
		}

		/**
		 * public void setLeft()
		 * 
		 * sets the node's left child to a desired node precondition:
		 * type(Node)==AVLNode postcondition: sets the node's left child to node
		 * Complexity: O(1)
		 * 
		 * @param node
		 * 
		 */
		public void setLeft(IAVLNode node) {
			this.left = node; // to be replaced by student code
		}

		/**
		 * public IAVLNode getLeft()
		 * 
		 * returns the node's left child precondition: none postcondition: none
		 * Complexity: O(1)
		 * 
		 */
		public IAVLNode getLeft() {
			return this.left; // to be replaced by student code
		}

		/**
		 * public void setRight()
		 * 
		 * sets the node's right child to a desired node precondition:
		 * type(node)==AVLNode postcondition: sets the node's right child to node
		 * Complexity: O(1)
		 * 
		 * @param node
		 * 
		 */
		public void setRight(IAVLNode node) {
			this.right = node; // to be replaced by student code
		}

		/**
		 * public IAVLNode getRight()
		 * 
		 * returns the node's right child precondition: none postcondition: none
		 * Complexity: O(1)
		 * 
		 */
		public IAVLNode getRight() {
			return this.right; // to be replaced by student code
		}

		/**
		 * public void setParent()
		 * 
		 * sets the node's parent to a desired node precondition: type(node)==AVLNode
		 * postcondition: sets the node's parent to node Complexity: O(1)
		 * 
		 * @param node
		 * 
		 */
		public void setParent(IAVLNode node) {
			this.parent = node; // to be replaced by student code
		}

		/****
		 * public IAVLNode getParent()
		 * 
		 * returns the node's parent precondition: none postcondition: returns the
		 * node's parent Complexity: O(1)
		 * 
		 */
		public IAVLNode getParent() {
			return this.parent; // to be replaced by student code
		}

		/**
		 * public boolean isRealNode()
		 * 
		 * return true iff this.getHeight!=-1 precondition: type(this.rank)==int
		 * postcondition: returns true if node is real, else returns false Complexity:
		 * O(1)
		 */
		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode() {
			if (this.getHeight() != -1) {
				return true;
			}
			return false;
		}

		/****
		 * public int getSize()
		 * 
		 * returns the node's size, how many nodes are in its subtree precondition: none
		 * postcondition: returns the node's size (type = int) Complexity: O(1)
		 * 
		 */
		public int getSize() {
			return this.size;
		}

		/****
		 * public void SetSize()
		 * 
		 * sets the node's size to be the node's left subtree size + right subtree size
		 * + 1 precondition: none postcondition: updates the node's size accordingly
		 * Complexity: O(1)
		 * 
		 */
		public void setSize() {
			this.size = 1;
			int leftSize = 0;
			int rightSize = 0;
			if (this.getLeft().getKey() != -1) {
				leftSize = this.getLeft().getSize();
			}
			if (this.getRight().getKey() != -1) {
				rightSize = this.getRight().getSize();
			}
			this.size += leftSize + rightSize;

			/**
			 * this.size = 1; int leftSize = 0; int rightSize = 0; try { leftSize =
			 * this.getLeft().getSize(); this.size += leftSize; } catch
			 * (NullPointerException e) { this.size += leftSize; } try { rightSize =
			 * this.getRight().getSize(); this.size += rightSize; } catch
			 * (NullPointerException e) { this.size += rightSize; } if (this.getSize() >
			 * 990) { System.out.println("actual size is:" + this.getSize()); }
			 */
		}

		/****
		 * public void SetHeight()
		 * 
		 * sets the node's rank to be the parameter height. Remember that in a static
		 * position, the node's height=its rank precondition: type(height)==int
		 * postcondition: updates the node's rank field accordingly Complexity: O(1)
		 * 
		 * @param height
		 * 
		 */
		public void setHeight(int height) {
			this.rank = height; 
		}

		/****
		 * public int getHeight()
		 * 
		 * returns the node's rank field. Remember that in a static position, the node's
		 * height=its rank precondition: none postcondition: returns the node's rank
		 * (type = int) Complexity: O(1)
		 * 
		 */
		public int getHeight() {
			return this.rank; 
		}

		/**
		 * public String searchRec(int k) A recursive method, helps determine if a node
		 * is in a certain tree and find its value precondition: type(k)==int
		 * postcondition: get the info(String) or null if not found
		 * 
		 * @param k
		 * @return the node's value, or null if empty Complexity: O(log(n))
		 */
		public String searchRec(int k) {
			if (this.rank == -1) {
				return null;
			}
			if (k == this.key) {
				return this.val;
			}
			if (k > this.key) {
				AVLNode rightNode = (AVLNode) this.right;
				return rightNode.searchRec(k);
			} else { // k<this.key
				AVLNode leftNode = (AVLNode) this.left;
				return leftNode.searchRec(k);
			}
		}

		/**
		 * public AVLNode insFindNode(int k) A method defined to distinguish if a node
		 * with a key==k exists in the AVLTree. If not, it checks the future parent of
		 * insert's node precondition: type(k)==int, k>0 postcondition: null if node's
		 * key==k, an AVLNode otherwise
		 * 
		 * @param k
		 * @return null if node with key==k exists, a node otherwise (that would be the
		 *         future father of insert's node) Complexity: O(log(n))
		 */
		public AVLNode insFindNode(int k) {
			if (this.getKey() == k) { // There exists a node with this key, nothing to insert
				return null;
			}
			AVLNode node = this;
			AVLNode pivot = new AVLNode();
			while (node != null && node.getKey() != -1) {
				if (k == node.getKey()) {
					return null;
				}
				if (k > node.getKey()) {
					pivot = node;
					node = (AVLNode) node.getRight();
				} else {
					pivot = node;
					node = (AVLNode) node.getLeft();
				}
			}
			return pivot;
		}

		/**
		 * public boolean isLeaf() checks if the node is a leaf or not precondition:
		 * none postcondition: none Complexity: O(1)
		 * 
		 * @return true iff the node is a leaf
		 */
		public boolean isLeaf() {
			if ((this.right.getHeight() == -1) && (this.left.getHeight() == -1)) {
				return true;
			}
			return false;
		}

		/**
		 * public char parentSide()
		 * 
		 * checks if the node is a right child or a left child postcondition: none
		 * postcondition: res=='N' || res=='L' || res=='R' Complexity: O(1)
		 * 
		 * @return the side (a char)
		 */
		public char parentSide() {
			if (this.getParent().getKey() == -1) {
				return 'N';
			}
			if (this.key == this.parent.getRight().getKey()) {
				return 'R';
			}
			return 'L';
		}

		/**
		 * public void updateSize() updates the node's parents sizes up until we get to
		 * the root precondition: none postcondition: none Complexity: O(log(n))
		 */
		public void updateSize() {
			this.setSize();
			if (this.checkRoot()) {
				return;
			}
			AVLNode parent = (AVLNode) this.parent;
			parent.updateSize();
		}

		/**
		 * public boolean checkRoot() checks if the node is the tree's root
		 * precondition: none postcondition: none Complexity: O(1)
		 * 
		 * @return true iff this node is the root
		 */
		public boolean checkRoot() {
			return (this.getParent().getHeight() == -1);
		}

		/**
		 * AVLNode delFindNode(int k)
		 * 
		 * returns the node we'd like to delete (with key==k), or null if node isn't in
		 * the tree precondition: k>0&&type(k)==int postcondition: returns an AVLNode or
		 * null Complexity: O(log(n))
		 * 
		 * @param k
		 * @return null iff node not in tree, else return node
		 */
		public AVLNode delFindNode(int k) {
			if (this.key == k) {
				AVLNode node = this;
				return node;
			}
			if (k > this.key) {
				if (this.right == null) {
					return null;
				}
				AVLNode rightChild = (AVLNode) this.right;
				return rightChild.delFindNode(k);
			} else {// k<this.key
				if (this.left == null) {
					return null;
				}
				AVLNode leftChild = (AVLNode) this.left;
				return leftChild.delFindNode(k);
			}
		}

		/**
		 * public AVLNode delUnaryLeft()
		 * 
		 * A method executing the removal of the node from the tree, including
		 * connecting its parent to its only child precondition: node.parent!=null and
		 * node is left child postcondition: type(res)==AVLNode Complexity: O(1)
		 * 
		 * @return the node's parent
		 */
		public AVLNode delUnaryLeft() {
			char side = this.parentSide();
			if (side == 'L') {
				this.parent.setLeft(this.getLeft());
			} else { // side == 'R', 'N' cannot happen
				this.parent.setRight(this.getLeft());
			}
			this.left.setParent(this.parent);
			AVLNode parent = (AVLNode) this.parent;
			this.parent = null;
			this.left = null;
			return parent;
		}

		/**
		 * public AVLNode delUnaryRight()
		 * 
		 * A method executing the removal of the node from the tree, including
		 * connecting its parent to its only child precondition: node.parent!=null and
		 * node is right child postcondition: type(res)==AVLNode Complexity: O(1)
		 * 
		 * @return the node's parent
		 */
		public AVLNode delUnaryRight() {
			char side = this.parentSide();
			if (side == 'R') {
				this.parent.setRight(this.getRight());
			} else {
				this.parent.setLeft(this.getRight());
			}
			this.right.setParent(this.parent);
			AVLNode parent = (AVLNode) this.parent;
			this.parent = null;
			this.left = null;
			return parent;
		}

	}

}
