package com.hv.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shanush on 1/2/2018.
 */
public class DataParser {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';


    public static Map<String, String> getTestData(String pathToDataFile, String testName) {
        List<String> headers = null;
        List<String> values = null;

        Map<String, String> dataForTests = new ConcurrentHashMap<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(pathToDataFile));
            List<String> rows = new ArrayList<>();
            while (scanner.hasNext()) {
                rows.add(scanner.nextLine());
            }
            scanner.close();

            for (int i = 0; i < rows.size(); i++) {
                ArrayList<String> tempList = (ArrayList<String>) parseLine(rows.get(i));
                if (tempList.toString().contains("TestName")) {
                    headers = tempList;
                } else if (tempList.get(0).toString().equals(testName)) {
                    values = tempList;
                    break;
                }
            }
            for (int i = 0; i < headers.size(); i++) {
                if (!values.get(i).isEmpty() || !values.get(i).equals("")) {
                    dataForTests.put(headers.get(i), values.get(i));
                }
            }
            System.out.println(dataForTests.toString());
            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataForTests;
    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }
}

