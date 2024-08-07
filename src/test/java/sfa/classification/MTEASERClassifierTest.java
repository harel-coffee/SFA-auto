package sfa.classification;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sfa.SFAWordsTest;
import sfa.timeseries.MultiVariateTimeSeries;
import sfa.timeseries.TimeSeries;
import sfa.timeseries.TimeSeriesLoader;


public class MTEASERClassifierTest {

    // The multivariate datasets to use
    public static String[] datasets = new String[]{
        "LP1",
        "LP2",
        "LP3",
        "LP4",
        "LP5",
        "PenDigits",
        "ShapesRandom",
        "DigitShapeRandom",
        "CMUsubject16",
        "ECG",
        "JapaneseVowels",
        "KickvsPunch",
        "Libras",
        "UWave",
        "Wafer",
        "WalkvsRun",
        "CharacterTrajectories",
        "ArabicDigits",
        "AUSLAN",
        "NetFlow",
    };

    @Test
    public void testMultiVariatelassification() throws IOException {

        try {
            // the relative path to the datasets
            ClassLoader classLoader = SFAWordsTest.class.getClassLoader();

            File dir = new File(classLoader.getResource("datasets/multivariate/").getFile());
            //File dir = new File("/Users/bzcschae/workspace/similarity/datasets/multivariate/");

            TimeSeries.APPLY_Z_NORM = false;

            for (String s : datasets) {
                File d = new File(dir.getAbsolutePath() + "/" + s);
                if (d.exists() && d.isDirectory()) {
                    for (File train : d.listFiles()) {
                        if (train.getName().toUpperCase().endsWith("TRAIN3")) {
                            File test = new File(train.getAbsolutePath().replaceFirst("TRAIN3", "TEST3"));

                            if (!test.exists()) {
                                System.err.println("File " + test.getName() + " does not exist");
                                test = null;
                            }

                            Classifier.DEBUG = false;

                            boolean useDerivatives = true;
                            MultiVariateTimeSeries[] trainSamples = TimeSeriesLoader.loadMultivariateDatset(train, useDerivatives);
                            MultiVariateTimeSeries[] testSamples = TimeSeriesLoader.loadMultivariateDatset(test, useDerivatives);

                            MTEASERClassifier teaser = new MTEASERClassifier();
                            MTEASERClassifier.S = 10; // faster processing
                            MUSEClassifier.Score museScore = teaser.eval(trainSamples, testSamples);
                            System.out.println(s + ";" + museScore.toString());
                        }
                    }
                } else{
                    // not really an error. just a hint:
                    System.out.println("Dataset could not be found: " + d.getAbsolutePath() + ".");
                }
            }
        } finally {
            TimeSeries.APPLY_Z_NORM = true; // FIXME static variable breaks some test cases!
        }
    }

}

