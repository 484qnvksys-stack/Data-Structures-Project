/**
 * Generic Binary Search Tree (the BST from class, with a generic key).
 * Keys are unique and are compared only with compareTo.
 * n = number of keys, h = height of the tree.
 */
public class BST<K extends Comparable<K>, T> implements IBST<K, T> {

    private BSTNode<K, T> root, current;
    private int count;          // number of keys, updated by insert and removeKey
    private boolean removed;    // set to true by remove_aux when the key is found

    public BST() {
        root = current = null;
        count = 0;
    }

    // O(1)
    public boolean empty() {
        return root == null;
    }

    // O(1)
    public int size() {
        return count;
    }

    // Data of the current node
    public T retrieve() {
        return current.data;
    }

    // Searches for the key. If found, current = its node. O(h)
    public boolean findKey(K tkey) {
        BSTNode<K, T> p = root, q = root;

        if (empty())
            return false;

        while (p != null) {
            q = p;
            if (tkey.compareTo(p.key) == 0) {
                current = p;
                return true;
            } else if (tkey.compareTo(p.key) < 0)
                p = p.left;
            else
                p = p.right;
        }

        current = q;    // last node visited (the parent of the new key in insert)
        return false;
    }

    // Inserts the key with its data; false if the key already exists. O(h)
    public boolean insert(K k, T val) {
        BSTNode<K, T> p, q = current;

        if (findKey(k)) {
            current = q;    // findKey changed current, put it back
            return false;
        }

        p = new BSTNode<K, T>(k, val);
        if (empty()) {
            root = current = p;
        } else {
            // current is the parent found by findKey
            if (k.compareTo(current.key) < 0)
                current.left = p;
            else
                current.right = p;
            current = p;
        }
        count++;
        return true;
    }

    // Removes the key; false if it does not exist. O(h)
    public boolean removeKey(K tkey) {
        removed = false;
        root = remove_aux(tkey, root);
        current = root;
        if (removed)
            count--;
        return removed;
    }

    private BSTNode<K, T> remove_aux(K key, BSTNode<K, T> p) {
        BSTNode<K, T> q, child = null;

        if (p == null)
            return null;

        if (key.compareTo(p.key) < 0)
            p.left = remove_aux(key, p.left);       // go left
        else if (key.compareTo(p.key) > 0)
            p.right = remove_aux(key, p.right);     // go right
        else {
            // key found
            removed = true;
            if (p.left != null && p.right != null) {
                // two children: copy the smallest key of the right subtree, then delete it
                q = find_min(p.right);
                p.key = q.key;
                p.data = q.data;
                p.right = remove_aux(q.key, p.right);
            } else {
                // one child or no children
                if (p.right == null)
                    child = p.left;
                else if (p.left == null)
                    child = p.right;
                return child;
            }
        }
        return p;
    }

    private BSTNode<K, T> find_min(BSTNode<K, T> p) {
        if (p == null)
            return null;
        while (p.left != null)
            p = p.left;
        return p;
    }

    // Data of all nodes in increasing key order. O(n)
    public LinkedList<T> inOrder() {
        LinkedList<T> result = new LinkedList<T>();
        inOrder_aux(root, result);
        return result;
    }

    private void inOrder_aux(BSTNode<K, T> p, LinkedList<T> result) {
        if (p == null)
            return;
        inOrder_aux(p.left, result);
        result.insert(p.data);      // insert puts it after the last added element
        inOrder_aux(p.right, result);
    }

    // Data of the nodes with low <= key <= high, in increasing key order. O(h + k)
    public LinkedList<T> rangeSearch(K low, K high) {
        LinkedList<T> result = new LinkedList<T>();
        rangeSearch_aux(root, low, high, result);
        return result;
    }

    private void rangeSearch_aux(BSTNode<K, T> p, K low, K high, LinkedList<T> result) {
        if (p == null)
            return;
        if (low.compareTo(p.key) < 0)           // smaller keys can still be in range
            rangeSearch_aux(p.left, low, high, result);
        if (low.compareTo(p.key) <= 0 && high.compareTo(p.key) >= 0)
            result.insert(p.data);
        if (high.compareTo(p.key) > 0)          // larger keys can still be in range
            rangeSearch_aux(p.right, low, high, result);
    }

    // Number of nodes on the longest root-to-leaf path (0 if empty). O(n)
    public int height() {
        return height_aux(root);
    }

    private int height_aux(BSTNode<K, T> p) {
        if (p == null)
            return 0;
        int leftHeight = height_aux(p.left);
        int rightHeight = height_aux(p.right);
        if (leftHeight > rightHeight)
            return leftHeight + 1;
        return rightHeight + 1;
    }

    // Number of nodes visited when searching for the key. Does not change current. O(h)
    public int nbKeyComp(K tkey) {
        BSTNode<K, T> p = root;
        int comparisons = 0;

        while (p != null) {
            comparisons++;
            if (tkey.compareTo(p.key) == 0)
                return comparisons;
            else if (tkey.compareTo(p.key) < 0)
                p = p.left;
            else
                p = p.right;
        }
        return comparisons;
    }
}
