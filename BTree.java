/*
 * CS7280 Special Topics in Database Management
 * Project 1: B-tree implementation.
 *
 * You need to code for the following functions in this program
 *   1. Lookup(int value) -> nodeLookup(int value, int node)
 *   2. Insert(int value) -> nodeInsert(int value, int node)
 *   3. Display(int node)
 *
 * SUBMISSION BY:-
 * NAME:- MISHA MODY
 * NEUID:- 002195418
 *
 *
 */



/**
 * CLASS REPRESENTS THE BALANCED TREE
 */
public class BTree {

    // T is the degree of the node
    private int T;

    // Number of currently used values.
    private int cntValues;

    /**
     * BTree constuctor
     *
     * @param t The degree of each node
     */
    public BTree(int t) {
        //Initialize the degree
        T = t;
        // Initialize the root
        root = new Node();
        // Root will initially have 0 entries
        root.n = 0;
        // Root will also be leaf as it does not have children in the start
        root.leaf = true;
        //Initially tree has no values
        cntValues = 0;
    }

    // Save the root node as a BTree attribute
    private Node root;

    public boolean Lookup(int value) {
        return nodeLookup(root, value);
    }

    public void display() {
        display(root);
    }

    /*
     * Insert(int value)
     *    - If -1 is returned, the value is inserted and increase cntValues.
     *    - If -2 is returned, the value already exists.
     */
    public void Insert(int value) {
        if (nodeInsert(value) == -1) cntValues++;
    }


    /*********** B tree functions for Public ******************/


    /**
     * @param x   current node that is being searched
     * @param key the value we need to search for
     * @return True if the key in found in the tree else it returns false
     */
    private boolean nodeLookup(Node x, int key) {
        // starting index
        int i = 0;

        // if current node is null then return false as value cannot be found
        if (x == null)
            return false;

        // traverse through all the values in the node
        for (i = 0; i < x.n; i++) {

            // find the value that is the first bigger element than key
            if (key < x.key[i]) {
                break;
            }

            // if value is found in the node then we can return true
            if (key == x.key[i]) {
                return true;
            }
        }

        // if value is not found in x and x is a leaf node then return false
        if (x.leaf) {
            return false;
        } else {

            // if key is not a lead node then the value might be in the corresponding child
            // holding values less than or equal to key
            return nodeLookup(x.child[i], key);
        }
    }


    /**
     * Function for inserting a value inside the proper node in tree
     *
     * @param key the value to be inserted
     */
    public int nodeInsert(int key) {

        //check to see if value already exists
        if (nodeLookup(root, key) == true) {
            //if value exists then return -2
            return -2;
        }

        // temp node r is initialized to root
        Node r = root;

        // check to see if the root itself if full
        if (r.n == 2 * T - 1) {

            // create a new node as the tree will increase in height
            Node s = new Node();

            // initialize the new node as the new root
            root = s;

            // this new node will have children which is the old root so it cannot be a leaf node
            s.leaf = false;

            // initially the new node will have no entries
            s.n = 0;

            // the old root becomes the child of this new root
            s.child[0] = r;

            // split the values of the prev root into 2 nodes which will both be children
            // of the new root
            Split(s, 0, r);

            //insert the value at the appropriate location using the same recursive function
            _Insert(s, key);
        } else {

            // if the root is not full then we can directly insert the value in the appropriate location
            _Insert(r, key);
        }
        return -1;
    }


    // Display the tree
    private void display(Node x) {
        assert (x == null);

        for (int i = 0; i < x.n; i++) {
            System.out.print(x.key[i] + " ");
        }

        System.out.println();
        if (!x.leaf) {
            for (int i = 0; i < x.n + 1; i++) {
                display(x.child[i]);
            }
        }
    }

    /** BTREE HELPER FUNCTION FOR INSERT **/

    /**
     * @param x   the node that gets the promoted extra key from the split ( parent of the node to be split)
     * @param pos
     * @param y   the node that needs to be split
     */
    private void Split(Node x, int pos, Node y) {

        // create a new node to split the values into
        Node z = new Node();

        // if the node to be split is a leaf ndoe then this new node will also be a leaf node
        z.leaf = y.leaf;

        // the new node will get last T-1 values of the node to be split
        z.n = T - 1;

        // traverse through the keys of Y to transfer the last T-1 keys to z
        for (int j = 0; j < T - 1; j++) {
            z.key[j] = y.key[j + T];
        }

        // If the node to be split is not a leaf node then half of the children will also need to be transferred
        if (!y.leaf) {

            // Transfer the last T children from y to z
            for (int j = 0; j < T; j++) {
                z.child[j] = y.child[j + T];
            }
        }

        // reduce the number of nodes in y after the split
        y.n = T - 1;

        //Since this node is going to have a new child,
        //create space of new child
        for (int j = x.n; j >= pos + 1; j--) {
            x.child[j + 1] = x.child[j];
        }
        // Link the new child to this node
        x.child[pos + 1] = z;

        //A key of y will move to this node. Find the location of
        //new key and move all greater keys one space ahead
        for (int j = x.n - 1; j >= pos; j--) {
            x.key[j + 1] = x.key[j];
        }

        // Copy the middle key of y to this node
        x.key[pos] = y.key[T - 1];

        // Increment count of keys in this node
        x.n = x.n + 1;
    }


    /**
     * @param x the node where the key should be inserted
     * @param k the key to be inserted
     */
    final private void _Insert(Node x, int k) {

        // check if node x is a leaf node
        if (x.leaf) {
            int i = 0;

            // find the exact position where the key should be placed
            // move the other keys by one place to the right
            for (i = x.n - 1; i >= 0 && k < x.key[i]; i--) {
                x.key[i + 1] = x.key[i];
            }
            //insert the key at index i+1
            x.key[i + 1] = k;

            //increase the number of nodes in x by 1
            x.n = x.n + 1;
        }
        // if it is not a leaf node
        else {
            int i = 0;
            // find the appropriate index to place the node
            for (i = x.n - 1; i >= 0 && k < x.key[i]; i--) {
            }
            ;
            i++;
            // check the child at that index
            Node tmp = x.child[i];

            // if the child node is full
            if (tmp.n == 2 * T - 1) {

                //
                Split(x, i, tmp);
                if (k > x.key[i]) {
                    i++;
                }
            }
            _Insert(x.child[i], k);
        }

    }


    public static void main(String[] args) {
        BTree b = new BTree(3);
        b.Insert(8);
        b.Insert(9);
        b.Insert(10);
        b.Insert(11);
        b.Insert(15);
        b.Insert(20);
        b.Insert(17);
        System.out.println();
        b.display();
    }

    /*
     * CntValues()
     *    - Returns the number of used values.
     */

    public int cntValues() {
        return cntValues;
    }


    /**
     * CLASS THAT REPRESENTS A NODE IN THE BALANCED TREE
     */
    public class Node {
        // n is the number of entries in the node
        int n;

        // array for storing all the values in the node
        int key[] = new int[2 * T - 1];

        // array for storing all the children of the nodes
        Node child[] = new Node[2 * T];

        // identifies the node as leaf or not
        boolean leaf = true;

    }
}