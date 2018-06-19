package cs240.byu.edu.spellcorrector_startingcode_android.StudentPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by chaserobertson on 5/12/16.
 */
public class MySpellCorrector implements ISpellCorrector {
    private Trie myTrie = new Trie();

    @Override
    public void useDictionary(InputStreamReader dictionaryFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(dictionaryFile);

        while(bufferedReader.ready()) {
            String word = bufferedReader.readLine();

            if(word != null) {
                word = word.trim();

                if(!"".equals(word)) {
                    word = word.toLowerCase();
                    myTrie.add(word);
                }
            }
        }

        bufferedReader.close();
    }

    @Override
    public String suggestSimilarWord(String inputWord) throws NoSimilarWordFoundException {
        if(inputWord == null || "".equals(inputWord)) throw new NoSimilarWordFoundException();
        else {
            inputWord = inputWord.trim();
            inputWord = inputWord.toLowerCase();
        }

        ITrie.INode found = myTrie.find(inputWord);
        if(found != null) return found.toString();

        TreeSet<String> nonExisting = new TreeSet<>();
        String foundString;
        foundString = bestOfSet(oneEditDistance(inputWord, nonExisting));
        if(foundString != null) return foundString;

        foundString = bestOfSet(twoEditDistance(nonExisting));
        if(foundString != null) return foundString;

        throw new NoSimilarWordFoundException();
    }

    private String bestOfSet(TreeSet<ITrie.INode> editDistanceSet) {
        TreeSet<String> bests = new TreeSet<>();

        int maxFrequency = 0;
        Iterator<ITrie.INode> iterator = editDistanceSet.iterator();
        while(iterator.hasNext()) {
            int thisFrequency = iterator.next().getValue();
            if(thisFrequency > maxFrequency) maxFrequency = thisFrequency;
        } //find number that most frequent word(s) occur

        iterator = editDistanceSet.iterator();
        while(iterator.hasNext()) {
            ITrie.INode thisNode = iterator.next();
            int thisFrequency = thisNode.getValue();
            if(thisFrequency == maxFrequency) bests.add(thisNode.toString());
        } //add most frequent word(s) to new set

        if(bests.size() > 0) return bests.first();
        else return null;
    }

    private TreeSet<ITrie.INode> oneEditDistance(String inputWord, TreeSet<String> nonExisting) {
        TreeSet<ITrie.INode> outputSet = new TreeSet<>();

        //deletion distance
        for(int i = 0; i < inputWord.length(); i++) {
            String newWord;
            if(i == 0) newWord = inputWord.substring(1);
            else if (i == inputWord.length() - 1) newWord = inputWord.substring(0,inputWord.length() - 1);
            else newWord = inputWord.substring(0, i) + inputWord.substring(i+1, inputWord.length());

            ITrie.INode newNode = myTrie.find(newWord);
            if(newNode != null) outputSet.add(newNode);
            else nonExisting.add(newWord);
        }
        //transposition distance
        for(int i = 1; i < inputWord.length(); i++) {
            char[] newWordArray = inputWord.toCharArray();

            char temp = newWordArray[i];
            newWordArray[i] = newWordArray[i-1];
            newWordArray[i-1] = temp;

            String newWord = new String(newWordArray);
            ITrie.INode newNode = myTrie.find(newWord);

            if(newNode != null) outputSet.add(newNode);
            else nonExisting.add(newWord);
        }
        //alteration distance
        for(int i = 0; i < inputWord.length(); i++) {
            for(int j = 0; j < 26; j++) {
                char[] newWordArray = inputWord.toCharArray();
                newWordArray[i] = (char)(j + 'a');

                String newWord = new String(newWordArray);
                ITrie.INode newNode = myTrie.find(newWord);

                if(newNode != null) outputSet.add(newNode);
                else nonExisting.add(newWord);
            }
        }
        //insertion distance
        for(int i = 0; i <= inputWord.length(); i++) {
            for(int j = 0; j < 26; j++) {
                String newWord;
                char newChar = (char)(j + 'a');

                if(i == 0) newWord = newChar + inputWord;
                else if(i == inputWord.length()) newWord = inputWord + newChar;
                else newWord = inputWord.substring(0,i) + newChar + inputWord.substring(i, inputWord.length());

                ITrie.INode newNode = myTrie.find(newWord);

                if(newNode != null) outputSet.add(newNode);
                else nonExisting.add(newWord);
            }
        }

        return outputSet;
    }

    private TreeSet<ITrie.INode> twoEditDistance(TreeSet<String> nonExisting) {
        TreeSet<ITrie.INode> outputSet = new TreeSet<>();
        TreeSet<String> dummySet = new TreeSet<>();

        Iterator<String> iterator = nonExisting.iterator();
        while(iterator.hasNext()) {
            String current = iterator.next();
            outputSet.addAll(oneEditDistance(current, dummySet));
        }

        return outputSet;
    }

}
