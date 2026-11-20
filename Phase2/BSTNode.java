/**
 * A node of the generic BST: a key, its data, and the left and right children.
 */
public class BSTNode<K extends Comparable<K>, T> {

    public K key;
    public T data;
    public BSTNode<K, T> left, right;

    public BSTNode(K key, T data) {
        this.key = key;
        this.data = data;
        left = right = null;
    }

    public BSTNode(K key, T data, BSTNode<K, T> left, BSTNode<K, T> right) {
        this.key = key;
        this.data = data;
        this.left = left;
        this.right = right;
    }
}
