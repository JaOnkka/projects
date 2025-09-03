/*  Jake Onkka CSE450 HW3
//  This program uses a scapegoat tree, or an avl tree
//  You must run with two command line arguments,
//  the first argument must be the type of tree you want to use.
//  The second argument must be the file to build the tree with.
//  It reads from stdinput to search through the tree so you can redirect an input file for this.
//  Example:    java Main scapegoat griefers.dat < input.txt > output.txt
//  Or:         java Main avl griefers.dat < input.txt > output.txt
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.io.File;

public class Main{
    public static void main(String[] args){
        long startTime = System.nanoTime(); //start timer
        String tree_to_use = null;
        String file = null;
        if(args.length > 0){
            tree_to_use = args[0];
            file = args[1];
        }
        else{
            System.out.println("Please provide arguments");
            return;
        }
        Scanner sc;
        // System.out.println(tree_to_use);
       // System.out.println(file);

        if(Objects.equals(tree_to_use, "avl")){
            AVLTree tree = new AVLTree();
            try{
                sc = new Scanner(new File(file));

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] tokens = line.split(" ");
                    tree.insert(tokens[0], Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]));
                    //System.out.println("insert");
                }

            } catch(Exception e){
                System.out.println("File cannot be read");
                return;
            }
            sc.close();
            //System.out.println("You want tree: " + tree_to_use + " File: " + file);
            //read from standard input, this will be our list of griefers to search for
            Scanner scan = new Scanner(System.in);
            // System.out.println("Standard Input");
            while(scan.hasNextLine()) {
                String input = scan.nextLine();
                tree.searchAndPrint(input);
                //   System.out.println(input);
            }
        }
        else if(Objects.equals(tree_to_use, "scapegoat")) {
            ScapeGoatTree tree = new ScapeGoatTree(0.8);    //alpha .8 seems good
            try{
                sc = new Scanner(new File(file));

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] tokens = line.split(" ");
                    tree.insert(tokens[0], Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]));
                    // tree.insert(username, serverID, banTime);
                }
                //tree.printInOrderTraversal();

            } catch(Exception e){
                System.out.println("File cannot be read");
                return;
            }
            sc.close();
            //System.out.println("You want tree: " + tree_to_use + " File: " + file);
            //read from standard input, this will be our list of griefers to search for
            Scanner scan = new Scanner(System.in);
            // System.out.println("Standard Input");
            while(scan.hasNextLine()) {
                String input = scan.nextLine();
                tree.searchAndPrint(input);
                //   System.out.println(input);
            }
        }

        long endTime = System.nanoTime();   //end timer
        System.out.println("total time in microseconds: " + ((endTime - startTime) / 1000));    //nano / 1000 = micro

    }
}
class Node{ //node class, both trees use the same node just for simplicity
    String playerName;
    int latestBanTime, balanceFactor;   //balance factor only used for avl
    List<String> bannedServers; //save every server as a list of strings, before adding, check list if server is already there, if it isn't insert, this keeps the list of only unique servers
    Node left;
    Node right;
    int size;   //used for height/depth
    public Node(String playerName, int latestBanTime){
        this.playerName = playerName;
        this.latestBanTime = latestBanTime;
        this.bannedServers = new ArrayList<>();
        this.left = null;
        this.right = null;
        this.size = 1;
        this.balanceFactor = 0;
    }
}
class AVLTree {
    private Node root;

    public AVLTree() {
        this.root = null;
    }

    public void insert(String playerName, int serverID, int banTime) {
        root = insert(root, playerName, serverID, banTime);
    }

    private Node insert(Node node, String playerName, int serverID, int banTime) {
        if (node == null) {
            Node newNode = new Node(playerName, banTime);
            newNode.bannedServers.add(String.valueOf(serverID));
            return newNode;
        }

        int cmp = playerName.compareTo(node.playerName);

        if (cmp < 0) {
            node.left = insert(node.left, playerName, serverID, banTime);
        } else if (cmp > 0) {
            node.right = insert(node.right, playerName, serverID, banTime);
        } else {
            if (!node.bannedServers.contains(String.valueOf(serverID))) {
                node.bannedServers.add(String.valueOf(serverID));
            }
            if (banTime > node.latestBanTime) {
                node.latestBanTime = banTime;
            }
        }

        node.balanceFactor = getSize(node.right) - getSize(node.left);
        if (node.balanceFactor > 1) {
            if (playerName.compareTo(node.right.playerName) > 0) {  //left rotate
                node = leftRotate(node);
            } else {        //right left rotate
                node.right = rightRotate(node.right);
                node = leftRotate(node);
            }
        } else if (node.balanceFactor < -1) {
            if (playerName.compareTo(node.left.playerName) < 0) {   //right rotate
                node = rightRotate(node);
            } else {       //left right rotate
                node.left = leftRotate(node.left);
                node = rightRotate(node);
            }
        }

        return node;
    }


    private Node rightRotate(Node node) {
        Node oldRoot = node;
        Node leftChild = node.left; //left child becomes new parent
        Node leftChildRightSubtree = leftChild.right;

        leftChild.right = oldRoot;
        oldRoot.left = leftChildRightSubtree;

        oldRoot.size = 1 + getSize(oldRoot.left) + getSize(oldRoot.right);
        leftChild.size = 1 + getSize(leftChild.left) + getSize(leftChild.right);

        return leftChild;
    }


    private Node leftRotate(Node node) {
        Node oldRoot = node;
        Node rightChild = node.right;   //right child becomes new parent
        Node rightChildLeftSubtree = rightChild.left;

        rightChild.left = oldRoot;
        oldRoot.right = rightChildLeftSubtree;

        oldRoot.size = 1 + getSize(oldRoot.left) + getSize(oldRoot.right);
        rightChild.size = 1 + getSize(rightChild.left) + getSize(rightChild.right);

        return rightChild;
    }

    private int getSize(Node node) {
        if (node == null){
            return 0;
        }
        else{
            return node.size;
        }
    }

    public void searchAndPrint(String playerName) {
        Node result = search(root, playerName);
        if (result != null) {
            int numServersBanned = result.bannedServers.size();
            int latestBanTime = result.latestBanTime;
            System.out.println(playerName + " was banned from " + numServersBanned + " servers. most recently on: " + latestBanTime);
        } else {
            System.out.println(playerName + " is not currently banned from any servers.");
        }
    }

    private Node search(Node node, String playerName) {
        if (node == null) { //person does not exist, ie has not been banned
            return null;
        }
        int cmp = playerName.compareTo(node.playerName);
        if (cmp < 0) {
            return search(node.left, playerName);
        } else if (cmp > 0) {
            return search(node.right, playerName);
        } else {
            return node; //return who was found
        }
    }
}

class ScapeGoatTree {
    private Node root;
    private int size;
    private double alpha;

    public ScapeGoatTree(double alpha) {
        this.root = null;
        this.size = 0;
        this.alpha = alpha;
    }

    public void insert(String playerName, int serverID, int banTime) {  //main calls this, kickstarts recursive calls to search through tree to find insertion point
        root = recurse_insert(root, playerName, serverID, banTime);
        size++;
    }

    private Node recurse_insert(Node node, String playerName, int serverID, int banTime) {
        if (node == null) {     //base case, once a null node is found, insert there
            Node newNode = new Node(playerName, banTime);
            newNode.bannedServers.add(String.valueOf(serverID));
            return newNode;
        }
        int cmp = playerName.compareTo(node.playerName);
        if (cmp < 0) {
            node.left = recurse_insert(node.left, playerName, serverID, banTime);
        } else if (cmp > 0) {
            node.right = recurse_insert(node.right, playerName, serverID, banTime);
        } else {
            //check if server id is unique
            if (!node.bannedServers.contains(String.valueOf(serverID))) {
                node.bannedServers.add(String.valueOf(serverID));
            }   //only save the most recent time
            if (banTime > node.latestBanTime) {
                node.latestBanTime = banTime;
            }
        }
        //root size increase
        node.size = 1 + getSize(node.left) + getSize(node.right);
        //after inserting, check balance
        if (isUnbalanced(node)) {
          //  System.out.println("Rebalancing");
            //node = rebuild(node);
            Node scapegoat = findUnbalancedAncestor(node);  //find our scapegoat and rebuild off of that
            node = rebuild(scapegoat);
        }

        return node;
    }

    private int getSize(Node node) {
        if (node == null){
            return 0;
        }
        else{
            return node.size;
        }
    }

    private boolean isUnbalanced(Node node) {
        if (node == null) {
            return false;
        }
        int leftSize = getSize(node.left);
        int rightSize = getSize(node.right);
        if(leftSize > alpha * node.size || rightSize > alpha * node.size){
            return true;
        }
        return false;
    }
    private Node findUnbalancedAncestor(Node node) {    //the 2nd approach, find the highest unbalanced node, top down
        if (isUnbalanced(node)) {   //base case: return the current node because it is unbalanced
            return node;
        }
        //recurse down both sides of tree until finding the first unbalanced node
        if (node.left != null && isUnbalanced(node.left)) {
            return findUnbalancedAncestor(node.left);
        }

        if (node.right != null && isUnbalanced(node.right)) {
            return findUnbalancedAncestor(node.right);
        }
        return null;
    }

    private Node rebuild(Node node) {       //start rebuilding the tree
        List<Node> nodes = new ArrayList<>();   //nodes becomes sorted list of values
        LNR(node, nodes);       //in order traversal saves to nodes
        return rebalance(nodes, 0, nodes.size());   //begin recursive rebalancing
    }

    private void LNR(Node node, List<Node> nodes) {
        if (node == null) {
            return;
        }
        LNR(node.left, nodes);
        nodes.add(node);
        LNR(node.right, nodes);
    }


    //median value becomes root, median of left becomes left root, median of right becomes right root
    //recurse over this ^^^ to get a perfectly balanced tree
    private Node rebalance(List<Node> nodes, int start, int end) {
        if (start >= end) {
            return null;
        }
        int mid = (start + end) / 2;
        Node newNode = nodes.get(mid);
        newNode.left = rebalance(nodes, start, mid);
        newNode.right = rebalance(nodes, mid + 1, end);
        newNode.size = 1 + getSize(newNode.left) + getSize(newNode.right);

        return newNode;
    }

    public void printTree() {   //prints tree in in-order traversal
        printLNR(root);
    }
    private void printLNR(Node node) {
        if (node != null) {
            printLNR(node.left);
            System.out.println("Player: " + node.playerName + ", Latest Ban Time: " + node.latestBanTime);
            printLNR(node.right);
        }
    }
    public void searchAndPrint(String playerName) {
        Node result = search(root, playerName);
        if (result != null) {
            int numServersBanned = result.bannedServers.size();
            int latestBanTime = result.latestBanTime;
            System.out.println(playerName + " was banned from " + numServersBanned + " servers. most recently on: " + latestBanTime);
        } else {
            System.out.println(playerName + " is not currently banned from any servers.");
        }
    }

    private Node search(Node node, String playerName) {
        if (node == null) { //person does not exist, ie has not been banned
            return null;
        }
        int cmp = playerName.compareTo(node.playerName);
        if (cmp < 0) {
            return search(node.left, playerName);
        } else if (cmp > 0) {
            return search(node.right, playerName);
        } else {
            return node; //return who was found
        }
    }

}