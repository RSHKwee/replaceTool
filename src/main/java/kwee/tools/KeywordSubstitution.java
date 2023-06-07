package kwee.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KeywordSubstitution {
  public static void main(String[] args) {
    /*
     * @formatter:off
     * if (args.length < 3) {
     *  System.out.println("Usage: java KeywordSubstitution <inputFile> <outputFile> <substitutionsFile>"
     * ); 
     * return; }
     *
     * String inputFile = args[0];
     * String outputFile = args[1];
     * String substitutionsFile = args[2];
     */
   // String inputFile = "D:\\misc\\substitute\\Alle_rekeningen_01-05-2023_31-05-2023.csv";
//    String outputFile = "D:\\misc\\substitute\\Alle_rekeningen_out.csv";
    String substitutionsFile = "D:\\misc\\substitute\\Substituties.txt";

    String inputFile = "D:\\misc\\substitute\\Alle_spaarrekeningen_01-05-2023_31-05-2023.csv";
    String outputFile = "D:\\misc\\substitute\\Alle_spaarrekeningen_out.csv";
    
    // Initialize keywordSubstitutions from the substitutions file
    Map<String, String> keywordSubstitutions = readSubstitutionsFromFile(substitutionsFile);

    try {
      // Read the input file
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      StringBuilder content = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
      }
      reader.close();

      // Perform keyword substitutions
      for (Map.Entry<String, String> entry : keywordSubstitutions.entrySet()) {
        String keyword = entry.getKey();
        String substitute = entry.getValue();
        content = new StringBuilder(content.toString().replaceAll(keyword, substitute));
      }

      // Write the modified content to the output file
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
      writer.write(content.toString());
      writer.close();

      System.out.println("Keyword substitution completed. Output file: " + outputFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Map<String, String> readSubstitutionsFromFile(String filename) {
    Map<String, String> substitutions = new HashMap<>();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("=");
        if (parts.length == 2) {
          String keyword = parts[0].trim();
          String substitute = parts[1].trim();
          substitutions.put(keyword, substitute);
        }
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return substitutions;
  }
}
