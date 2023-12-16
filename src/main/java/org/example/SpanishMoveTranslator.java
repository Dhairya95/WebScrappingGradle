package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SpanishMoveTranslator {
    private static final Map<String, Translation> TRANSLATION_MAP = new HashMap<String, Translation>();
    private static final String mainUrl = "https://www.pokexperto.net/index2.php?seccion=nds/movimientos_pokemon";
    private static final List<String[]> DATA_LIST = new ArrayList<>();
    private static boolean headersPresent = true;

    static void syncTranslation() {
        try {
            Document document = Jsoup.connect(mainUrl).get();
            String englishName = "";
            String spanishName = "";
            String spanishDescription = "";
            Elements links = document.select("tr.check3.bazul");
            for (Element link : links) {
                Elements aTags = link.select("a.nav6c");
                Elements desc = link.select("td.justify");

                for (int i = 0; i < aTags.size(); i += 2) {
                    spanishName = aTags.get(i).text();
                    englishName = aTags.get(i + 1).text();
                }
                spanishDescription = desc.text();

                Translation translation = new Translation(englishName, spanishName, spanishDescription);
                TRANSLATION_MAP.put(englishName, translation);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public static void main(String[] args) {
        syncTranslation();
        readData();
        writeData();
    }

    static void readData() {
        String csvFilePath = "translated_moves.csv";
        try (InputStreamReader isr = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(csvFilePath), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(isr, CSVFormat.DEFAULT)) {
            for (CSVRecord csvRecord : csvParser) {

                String data[] = new String[7];
                for (int i = 0; i < 5; i++) {
                    data[i] = csvRecord.get(i);
                }
                DATA_LIST.add(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void writeData() {
        String newCsvFilePath = "src/main/resources/new_translated_moves.csv";
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(newCsvFilePath, true), StandardCharsets.UTF_8); // Append mode
             CSVPrinter csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT)) {

            String header[] = {"ID","Name","Description (Short)","Name (Chinese)","Description (Chinese Short)","Name (Spanish)","Description (Spanish Short)"};
           csvPrinter.printRecord(header);
            for (String[] string : DATA_LIST) {

             if(headersPresent) {
                    headersPresent = false;
                    continue;
                }

                Translation translation = spanishTranslation(string[1]);
                if (translation != null) {
                    string[5] = translation.getSpanishName();
                    string[6] = translation.getSpanishDescription();
                }
                else if(string[0].startsWith("hiddenpower")||string[0].startsWith("magikarpsrevenge"))
                {
                    string[5] = "";
                    string[6] = "";
                }
                else
                {
                    string[5] = string[1];
                    string[6] = string[2];
                }
                csvPrinter.printRecord((Object[]) string);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Translation spanishTranslation(String move) {
        return TRANSLATION_MAP.get(move);
    }
}