package cz.zcu.kiv.nlp.ir.trec.utils;

import cz.zcu.kiv.nlp.ir.trec.config.Config;

import java.io.*;
import java.util.*;

/**
 * @author tigi
 *
 * IOUtils, existující metody neměňte.
 */
public class IOUtils {

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return list of lines
     */
    public static List<String> readLines(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot locate stream");
        }
        try {
            List<String> result = new ArrayList<String>();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line.trim());
                }
            }

            inputStream.close();

            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream stream
     * @return text
     */
    public static String readFile(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot locate stream");
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();

            return sb.toString().trim();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param list lines of text to save
     */
    public static void saveFile(File file, Collection<String> list) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(file), true, "UTF-8");

            for (String text : list) {
                printStream.println(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file file to save
     * @param text text to save
     */
    public static void saveFile(File file, String text) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(file), true, "UTF-8");

            printStream.println(text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }
    }

    public static Set<String> getStopWords(){
        Set<String> stopwords = new HashSet<String>();
        String line;

        try {
            BufferedReader  reader = new BufferedReader(new FileReader(new File(Config.getStopWordsDataPath())));
            while((line = reader.readLine()) != null) {
                stopwords.add(line);
            }
            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return stopwords;
    }

}
