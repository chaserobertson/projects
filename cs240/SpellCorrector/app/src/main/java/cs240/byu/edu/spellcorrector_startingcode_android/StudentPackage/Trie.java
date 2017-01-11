package cs240.byu.edu.spellcorrector_startingcode_android.StudentPackage;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Created by chaserobertson on 5/12/16.
 */
public class Trie implements ITrie {
    private int nodeCount = 1;
    private int wordCount = 0;
    Node[] children = new Node[26];
    TreeSet<String> library = new TreeSet<>();

    public class Node implements ITrie.INode, Comparable {
        protected int frequency = 0;
        protected String wordValue;
        protected Node[] children = new Node[26];

        @Override
        public int getValue() {
            return frequency;
        }

        public String toString() {
            return wordValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (frequency != node.frequency) return false;
            if (wordValue != null ? !wordValue.equals(node.wordValue) : node.wordValue != null)
                return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(children, node.children);

        }

        @Override
        public int hashCode() {
            int result = frequency;
            result = 31 * result + (wordValue != null ? wordValue.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(children);
            return result;
        }

        @Override
        public int compareTo(Object another) {
            return wordValue.compareTo(another.toString());
        }
    }

    @Override
    public INode find(String word) {
        if("".equals(word)) return null;
        int current = word.charAt(0) - 'a';
        if(children[current] == null) return null;

        return findRecurse(word, children[current], 0);
    }

    public INode findRecurse(String word, Node currentNode, int index) {
        if(index == word.length()-1) {
            if(currentNode.frequency == 0) return null;
            else return currentNode;
        }
        else {
            index++;
            int current = word.charAt(index) - 'a';

            if (currentNode.children[current] == null) return null;

            return findRecurse(word, currentNode.children[current], index);
        }
    }

    @Override
    public void add(String word) {
        int current = word.charAt(0) - 'a';
        if(children[current] == null) {
            children[current] = new Node();
            nodeCount++;
        }

        addRecurse(word, children[current], 0);
    }

    private void addRecurse(String word, Node currentNode, int index) {
        if(index == word.length()-1) {
            if(currentNode.frequency == 0) wordCount++;

            currentNode.frequency++;
            currentNode.wordValue = word;
            library.add(word);
        }
        else {
            index++;
            int current = word.charAt(index) - 'a';

            if (currentNode.children[current] == null) {
                currentNode.children[current] = new Node();
                nodeCount++;
            }

            addRecurse(word, currentNode.children[current], index);
        }
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public int getWordCount() {
        return wordCount;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        Iterator< String > iterator = library.iterator();
        while(iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            stringBuilder.append("\n");
        }
        //stringBuilder.deleteCharAt(stringBuilder.length()-1);
        //stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trie trie = (Trie) o;

        if (nodeCount != trie.nodeCount) return false;
        if (wordCount != trie.wordCount) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(children, trie.children)) return false;
        return library != null ? library.equals(trie.library) : trie.library == null;
    }

    @Override
    public int hashCode() {
        int result = nodeCount;
        result = 31 * result + wordCount;
        result = 31 * result + Arrays.hashCode(children);
        result = 31 * result + (library != null ? library.hashCode() : 0);
        return result;
    }
}
