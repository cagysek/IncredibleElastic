package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.Index.Index;
import cz.zcu.kiv.nlp.ir.trec.Index.loader.LoaderFactory;
import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.stemmer.CzechStemmerLight;
import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.tokenizer.AdvancedTokenizer;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearcherType;
import cz.zcu.kiv.nlp.ir.trec.data.Topic;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.search.searcher.Searcher;
import cz.zcu.kiv.nlp.ir.trec.search.searcher.SearcherFactory;
import cz.zcu.kiv.nlp.ir.trec.utils.IOUtils;
import cz.zcu.kiv.nlp.ir.trec.utils.SerializedDataHelper;
import org.apache.log4j.*;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;

import java.io.*;
import java.util.*;


/**
 * @author tigi
 *
 * Třída slouží pro vyhodnocení vámi vytvořeného vyhledávače
 *
 */
public class TestTrecEval {

    static Logger log = Logger.getLogger(TestTrecEval.class);
    static final String OUTPUT_DIR = "./TREC";

    protected static void configureLogger() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();

        File results = new File(OUTPUT_DIR);
        if (!results.exists()) {
            results.mkdir();
        }

        try {
            Appender appender = new WriterAppender(new PatternLayout(), new FileOutputStream(new File(OUTPUT_DIR + "/" + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + " - " + ".log"), false));
            BasicConfigurator.configure(appender);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.getRootLogger().setLevel(Level.INFO);
    }

    /**
     * Metoda vytvoří objekt indexu, načte data, zaindexuje je provede předdefinované dotazy a výsledky vyhledávání
     * zapíše souboru a pokusí se spustit evaluační skript.
     *
     * Na windows evaluační skript pravděpodbně nebude možné spustit. Pokud chcete můžete si skript přeložit a pak
     * by mělo být možné ho spustit.
     *
     * Pokud se váme skript nechce překládat/nebo se vám to nepodaří. Můžete vygenerovaný soubor s výsledky zkopírovat a
     * spolu s přiloženým skriptem spustit (přeložit) na
     * Linuxu např. pomocí vašeho účtu na serveru ares.fav.zcu.cz
     *
     * Metodu není třeba měnit kromě řádků označených T O D O  - tj. vytvoření objektu třídy {@link Index} a
     */
    public static void main(String args[]) throws IOException {
       // configureLogger();
        long time1 = System.currentTimeMillis();
        Preprocessing preprocessing = new Preprocessing(new CzechStemmerLight(),
                                                        new AdvancedTokenizer(),
                                                        IOUtils.getStopWords(),
                             false,
                              true,
                                            true,
                                     true
        );

        Index index = new Index(preprocessing);
        index.setDataType(ELoaderType.CZECH);

        List<Document> documents = new LoaderFactory().getLoader(ELoaderType.CZECH).loadDocuments();


        index.index(documents, ELoaderType.CZECH);
        long time2 = System.currentTimeMillis();

        System.out.println("Index time: " + ((time2 - time1) * 0.001));


        Searcher searcher = new SearcherFactory().getSearcher(ESearcherType.BOOLEAN, preprocessing, index.getInvertedIndex());

        time1 = System.currentTimeMillis();

       // List<Result> results = searcher.search("Relevantní dokumenty mohou hovořit buď o zdravotních rizicích, nebo o skutečném propuknutí choroby či onemocnění způsobených kontaminovanou vodou.");
        time2 = System.currentTimeMillis();

        System.out.println("Search time: " + ((time2 - time1) * 0.001));

      //  printResults(results);

        System.out.println("Konec");



        List<Topic> topics = SerializedDataHelper.loadTopic(new File(OUTPUT_DIR + "/topicData.bin"));

        File serializedData = new File(OUTPUT_DIR + "/czechData.bin");

        List<String> lines = new ArrayList<>();

        for (Topic t : topics) {
            List<Result> resultHits;
            try
            {
               resultHits = searcher.search(t.getTitle() + " " + t.getDescription() + " " + t.getNarrative());
            }
            catch (QueryNodeException e)
            {
                System.out.println("Zkontroluj Boolean query dotaz");
                return;
            }
            catch (Exception e)
            {
                System.out.println("Ops");
                e.printStackTrace();
                return;
            }

            Comparator<Result> cmp = new Comparator<Result>() {
                public int compare(Result o1, Result o2) {
                    if (o1.getScore() > o2.getScore()) return -1;
                    if (o1.getScore() == o2.getScore()) return 0;
                    return 1;
                }
            };

            Collections.sort(resultHits, cmp);
            for (Result r : resultHits) {
                final String line = r.toString(t.getId());
                lines.add(line);
            }
            if (resultHits.size() == 0) {
                lines.add(t.getId() + " Q0 " + "abc" + " " + "99" + " " + 0.0 + " runindex1");
            }

        }
     //   final File outputFile = new File(OUTPUT_DIR + "/results 2020-05-29_16_46_860.txt");
        final File outputFile = new File(OUTPUT_DIR + "/results " + SerializedDataHelper.SDF.format(System.currentTimeMillis()) + ".txt");
        IOUtils.saveFile(outputFile, lines);
        //try to run evaluation
        try {
            runTrecEval(outputFile.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String runTrecEval(String predictedFile) throws IOException {

        String commandLine = "./trec_eval.8.1/./trec_eval" +
                " ./trec_eval.8.1/czech" +
                " " + predictedFile;

        System.out.println(commandLine);
        Process process = Runtime.getRuntime().exec(commandLine);

        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String trecEvalOutput;
        StringBuilder output = new StringBuilder("TREC EVAL output:\n");
        for (String line; (line = stdout.readLine()) != null; ) output.append(line).append("\n");
        trecEvalOutput = output.toString();
        System.out.println(trecEvalOutput);

        int exitStatus = 0;
        try {
            exitStatus = process.waitFor();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println(exitStatus);

        stdout.close();
        stderr.close();

        return trecEvalOutput;
    }

    private static void printResults(List<Result> results)
    {
        for (Result r : results) {
            System.out.println(r.toString());
        }
    }
}
