package dev.lld.practice;

import java.util.*;

public class WordCountDataStructure {

    // Node class for doubly linked list to maintain count frequencies
    private class CountNode {
        int count;
        Set<String> words;  // Words with this count
        CountNode prev, next;

        CountNode(int count) {
            this.count = count;
            this.words = new HashSet<>();
            this.prev = null;
            this.next = null;
        }
    }

    // Maps word to its current count
    private Map<String, Integer> wordToCount;

    // Maps count to its corresponding node in the linked list
    private Map<Integer, CountNode> countToNode;

    // Dummy head and tail for the doubly linked list
    private CountNode head, tail;

    public WordCountDataStructure() {
        wordToCount = new HashMap<>();
        countToNode = new HashMap<>();

        // Initialize dummy head and tail
        head = new CountNode(0);
        tail = new CountNode(0);
        head.next = tail;
        tail.prev = head;
    }

    // Helper method to add a new count node after a given node
    private CountNode addNodeAfter(CountNode prevNode, int count) {
        CountNode newNode = new CountNode(count);
        CountNode nextNode = prevNode.next;

        prevNode.next = newNode;
        newNode.prev = prevNode;
        newNode.next = nextNode;
        nextNode.prev = newNode;

        countToNode.put(count, newNode);
        return newNode;
    }

    // Helper method to remove a count node from the linked list
    private void removeNode(CountNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        countToNode.remove(node.count);
    }

    // Increments the count of String s by one - O(1)
    public void inc(String s) {
        int currentCount = wordToCount.getOrDefault(s, 0);
        int newCount = currentCount + 1;

        wordToCount.put(s, newCount);

        // Remove word from current count node (if exists)
        if (currentCount > 0) {
            CountNode currentNode = countToNode.get(currentCount);
            currentNode.words.remove(s);

            // If current node becomes empty and it's not count 0, remove it
            if (currentNode.words.isEmpty()) {
                removeNode(currentNode);
            }
        }

        // Add word to new count node
        CountNode newNode = countToNode.get(newCount);
        if (newNode == null) {
            // Find the correct position to insert new count node
            CountNode prevNode = (currentCount == 0) ? head :
                    countToNode.getOrDefault(currentCount, head);

            // If currentCount node was removed, find the node with count currentCount - 1
            if (currentCount > 0 && !countToNode.containsKey(currentCount)) {
                prevNode = head;
                CountNode temp = head.next;
                while (temp != tail && temp.count < newCount) {
                    prevNode = temp;
                    temp = temp.next;
                }
            }

            newNode = addNodeAfter(prevNode, newCount);
        }

        newNode.words.add(s);
    }

    // Decrements the count of String s by one - O(1)
    public void dec(String s) {
        if (!wordToCount.containsKey(s)) {
            return; // Word doesn't exist
        }

        int currentCount = wordToCount.get(s);
        int newCount = currentCount - 1;

        // Remove word from current count node
        CountNode currentNode = countToNode.get(currentCount);
        currentNode.words.remove(s);

        if (newCount == 0) {
            // Remove word completely
            wordToCount.remove(s);
        } else {
            // Update word count
            wordToCount.put(s, newCount);

            // Add word to new count node
            CountNode newNode = countToNode.get(newCount);
            if (newNode == null) {
                // Insert new node before current node
                newNode = addNodeAfter(currentNode.prev, newCount);
            }
            newNode.words.add(s);
        }

        // Remove current node if it becomes empty
        if (currentNode.words.isEmpty()) {
            removeNode(currentNode);
        }
    }

    // Returns the String with the maximum count - O(1)
    public String getMax() {
        if (tail.prev == head) {
            return ""; // No words exist
        }

        CountNode maxNode = tail.prev;
        return maxNode.words.iterator().next(); // Return any word with max count
    }

    // Returns the String with the minimum count - O(1)
    public String getMin() {
        if (head.next == tail) {
            return ""; // No words exist
        }

        CountNode minNode = head.next;
        return minNode.words.iterator().next(); // Return any word with min count
    }

    // Helper method to display current state (for debugging)
    public void displayState() {
        System.out.println("Current state:");
        CountNode current = head.next;
        while (current != tail) {
            System.out.println("Count " + current.count + ": " + current.words);
            current = current.next;
        }
        System.out.println("Word to Count mapping: " + wordToCount);
        System.out.println();
    }

    // Test the data structure
    public static void main(String[] args) {
        WordCountDataStructure ds = new WordCountDataStructure();

        System.out.println("Testing Word Count Data Structure:");
        System.out.println("==================================");

        // Test increments
        ds.inc("hello");
        ds.inc("world");
        ds.inc("hello");
        ds.inc("java");
        ds.inc("hello");

        System.out.println("After increments:");
        ds.displayState();

        System.out.println("Max: " + ds.getMax()); // Should be "hello" (count 3)
        System.out.println("Min: " + ds.getMin()); // Should be "world" or "java" (count 1)

        // Test decrements
        ds.dec("hello");
        ds.dec("world");

        System.out.println("\nAfter decrements:");
        ds.displayState();

        System.out.println("Max: " + ds.getMax()); // Should be "hello" (count 2)
        System.out.println("Min: " + ds.getMin()); // Should be "java" (count 1)

        // Test edge cases
        ds.dec("java");
        System.out.println("\nAfter removing java:");
        ds.displayState();

        ds.inc("new");
        ds.inc("new");
        ds.inc("new");
        ds.inc("new");

        System.out.println("After adding 'new' 4 times:");
        ds.displayState();

        System.out.println("Max: " + ds.getMax()); // Should be "new" (count 4)
        System.out.println("Min: " + ds.getMin()); // Should be "hello" (count 2)
    }
}