package cs240.byu.edu.evilhangman_android.StudentPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

/**
 * Created by chaserobertson on 5/12/16.
 */
public class MyEvilHangmanGame implements StudentEvilHangmanGameController {
    TreeSet<Character> usedLetters = new TreeSet<>();
    TreeSet<String> currentDictionary = new TreeSet<>();
    private int numGuesses;

    @Override
    public void startGame(InputStreamReader dictionary, int wordLength) {
        BufferedReader bufferedReader = new BufferedReader(dictionary);
        try {
            while (bufferedReader.ready()) {
                String nextLine = bufferedReader.readLine();
                if(nextLine != null && !"".equals(nextLine)) {
                    nextLine = nextLine.trim();
                    nextLine = nextLine.toLowerCase();
                    if(nextLine.length() == wordLength) currentDictionary.add(nextLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Key implements Comparable {
        public String keyString;
        public int keyInt;
        public int numChars;
        public ArrayList<Integer> charIndexes;

        public Key(String keyString, int keyInt, int numChars, ArrayList<Integer> charIndexes) {
            this.keyString = keyString;
            this.keyInt = keyInt;
            this.numChars = numChars;
            this.charIndexes = charIndexes;
        }

        public String toString() {
            return keyString;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (keyInt != key.keyInt) return false;
            if (numChars != key.numChars) return false;
            if (keyString != null ? !keyString.equals(key.keyString) : key.keyString != null)
                return false;
            return charIndexes != null ? charIndexes.equals(key.charIndexes) : key.charIndexes == null;

        }

        @Override
        public int hashCode() {
            return keyInt;
        }

        @Override
        public int compareTo(Object another) {
            return keyString.compareTo(another.toString());
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if(usedLetters.contains(guess)) throw new GuessAlreadyMadeException();

        if(!Character.isLetter(guess)) throw new GuessAlreadyMadeException();
        else guess = Character.toLowerCase(guess);

        ConcurrentHashMap<Key, TreeSet<String>> newSets = partition(guess);
        TreeSet<String> newDictionary = bestOf(newSets);
        if(newDictionary == null) throw new GuessAlreadyMadeException();
        currentDictionary = newDictionary;
        usedLetters.add(guess);
        numGuesses--;

        return currentDictionary;
    }

    private ConcurrentHashMap<Key, TreeSet<String>> partition(char guess) {
        ConcurrentHashMap<Key, TreeSet<String>> output = new ConcurrentHashMap<>();

        Iterator<String> iterator = currentDictionary.iterator();
        while(iterator.hasNext()) {
            String currentWord = iterator.next();
            StringBuilder stringBuilder = new StringBuilder();
            int keyInt = 0;
            int numChars = 0;
            ArrayList<Integer> charIndexes = new ArrayList<>();

            for(int i = 0; i < currentWord.length(); i++) {
                if(currentWord.charAt(i) == guess) {
                    stringBuilder.append(guess);
                    keyInt += Math.pow(2, i); //keyInt is largest for rightmost chars
                    numChars++;
                    charIndexes.add(i);
                }
                else {
                    stringBuilder.append("-");
                }
            }

            String newString = stringBuilder.toString();
            Key newKey = new Key(newString, keyInt, numChars, charIndexes);
            if(!output.containsKey(newKey)) {
                output.put(newKey, new TreeSet<String>());
            }

            output.get(newKey).add(currentWord);
        }

        return output;
    }

    private TreeSet<String> bestOf(ConcurrentHashMap< Key, TreeSet<String> > newSets) {

        Set<Entry<Key, TreeSet<String>>> entrySet = newSets.entrySet();
        Iterator<Entry<Key, TreeSet<String>>> iterator = entrySet.iterator();

        //HashMap<Key, TreeSet<String>> newMap = new HashMap<>();

        {// this is the biggest set size segment of partition optimization
            int biggestSetSize = 0;

            while (iterator.hasNext()) {
                Entry<Key, TreeSet<String>> currentEntry = iterator.next();
                int currentEntrySize = currentEntry.getValue().size();
                if (currentEntrySize > biggestSetSize) biggestSetSize = currentEntrySize;
            } // find biggest set size

            iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<Key, TreeSet<String>> currentEntry = iterator.next();
                int currentEntrySize = currentEntry.getValue().size();
                if (currentEntrySize != biggestSetSize) {
                    newSets.remove(currentEntry.getKey());
                }
            } // narrow map to only biggest sets
        }

        if (newSets.size() == 1) return newSets.values().iterator().next();
        else entrySet = newSets.entrySet();

        {
            int leastCharCount = 200;

            iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<Key, TreeSet<String>> currentEntry = iterator.next();
                int currentEntryCharCount = currentEntry.getKey().numChars;
                if (currentEntryCharCount < leastCharCount) leastCharCount = currentEntryCharCount;
            } // find least number of chars

            iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<Key, TreeSet<String>> currentEntry = iterator.next();
                int currentEntryCharCount = currentEntry.getKey().numChars;
                if (currentEntryCharCount != leastCharCount) {
                    newSets.remove(currentEntry.getKey());
                }
            }// narrow newSets to only sets with least number of guessed chars
        }

        if (newSets.size() == 1) return newSets.values().iterator().next();
        else entrySet = newSets.entrySet();

        {// this is the rightmost char segment of partition optimization
            int biggestKeyInt = 0;

            iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<Key, TreeSet<String>> currentEntry = iterator.next();
                int currentKeyInt = currentEntry.getKey().keyInt;
                if(currentKeyInt > biggestKeyInt) biggestKeyInt = currentKeyInt;
            } // find rightmost char location

            iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<Key, TreeSet<String>> currentEntry = iterator.next();
                int currentKeyInt = currentEntry.getKey().keyInt;
                if(currentKeyInt != biggestKeyInt) {
                    newSets.remove(currentEntry.getKey());
                }
            } // narrow new map to only rightmost char
        }

        return newSets.values().iterator().next();
    }

    @Override
    public String getCurrentWord() {
        if(currentDictionary.size() < 1) return null;
        StringBuilder stringBuilder = new StringBuilder();

        String currentWord = currentDictionary.first();
        for(int i = 0; i < currentWord.length(); i++) {
            char currentChar = currentWord.charAt(i);
            if(usedLetters.contains(currentChar)) stringBuilder.append(currentChar);
            else stringBuilder.append('-');
        }

        return stringBuilder.toString();
        //return currentDictionary.toString();
    }

    @Override
    public int getNumberOfGuessesLeft() {
        return numGuesses;
    }

    @Override
    public void setNumberOfGuesses(int numberOfGuessesToStart) {
        numGuesses = numberOfGuessesToStart;
    }

    @Override
    public Set<Character> getUsedLetters() {
        return usedLetters;
    }

    @Override
    public GAME_STATUS getGameStatus() {
        if(currentDictionary.size() == 1) {
            String current = currentDictionary.first();
            boolean winner = true;
            for(int i = 0; i < current.length(); i++) {
                if(!usedLetters.contains(current.charAt(i))) winner = false;
            }

            if(winner) return GAME_STATUS.PLAYER_WON;
        }

        if(numGuesses == 0) return GAME_STATUS.PLAYER_LOST;

        return GAME_STATUS.NORMAL;
    }

}
