package kwee.tools;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class KeywordSubstitutionGUI {
  private JTextArea keywordTextArea;
  private JTextArea inputTextArea;
  private JButton selectInputButton;
  private JButton selectOutputButton;
  private JButton substituteButton;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        createAndShowGUI();
      }
    });
  }

  private static void createAndShowGUI() {
    JFrame frame = new JFrame("Keyword Substitution");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    KeywordSubstitutionGUI keywordSubstitutionGUI = new KeywordSubstitutionGUI();
    keywordSubstitutionGUI.addComponentsToPane(frame.getContentPane());

    frame.pack();
    frame.setVisible(true);
  }

  private void addComponentsToPane(Container pane) {
    pane.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);

    keywordTextArea = new JTextArea(10, 30);
    JScrollPane keywordScrollPane = new JScrollPane(keywordTextArea);
    keywordScrollPane.setBorder(BorderFactory.createTitledBorder("Keyword Substitutions"));
    GridBagConstraints c1 = new GridBagConstraints();
    c1.insets = new Insets(5, 5, 5, 5);
    c1.gridx = 0;
    c1.gridy = 0;
    c1.gridwidth = 2;
    pane.add(keywordScrollPane, c1);

    inputTextArea = new JTextArea(10, 30);
    JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
    inputScrollPane.setBorder(BorderFactory.createTitledBorder("Input Text"));
    GridBagConstraints c2 = new GridBagConstraints();
    c2.insets = new Insets(5, 5, 5, 5);
    c2.gridx = 0;
    c2.gridy = 1;
    c2.gridwidth = 2;
    pane.add(inputScrollPane, c2);

    selectInputButton = new JButton("Select Input File");
    selectInputButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectInputFile();
      }
    });
    GridBagConstraints c3 = new GridBagConstraints();
    c3.insets = new Insets(5, 5, 5, 5);
    c3.gridx = 0;
    c3.gridy = 2;
    c3.gridwidth = 1;
    pane.add(selectInputButton, c3);

    selectOutputButton = new JButton("Select Output File");
    selectOutputButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectOutputFile();
      }
    });
    GridBagConstraints c4 = new GridBagConstraints();
    c4.insets = new Insets(5, 5, 5, 5);
    c4.gridx = 1;
    c4.gridy = 2;
    pane.add(selectOutputButton, c4);

    substituteButton = new JButton("Substitute Keywords");
    substituteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performKeywordSubstitution();
      }
    });
    GridBagConstraints c5 = new GridBagConstraints();
    c5.insets = new Insets(5, 5, 5, 5);
    c5.gridx = 0;
    c5.gridy = 3;
    c5.gridwidth = 2;
    pane.add(substituteButton, c5);
  }

  private void selectInputFile() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      try {
        BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line).append("\n");
        }
        reader.close();
        inputTextArea.setText(content.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void selectOutputFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
    int result = fileChooser.showSaveDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      File outputFile = fileChooser.getSelectedFile();
      performKeywordSubstitution();
      try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(inputTextArea.getText());
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void performKeywordSubstitution() {
    String inputText = inputTextArea.getText();
    String keywordText = keywordTextArea.getText();

    Map<String, String> keywordSubstitutions = parseKeywordSubstitutions(keywordText);

    for (Map.Entry<String, String> entry : keywordSubstitutions.entrySet()) {
      String keyword = entry.getKey();
      String substitute = entry.getValue();
      inputText = inputText.replaceAll(keyword, substitute);
    }

    inputTextArea.setText(inputText);
  }

  private Map<String, String> parseKeywordSubstitutions(String keywordText) {
    Map<String, String> keywordSubstitutions = new HashMap<>();

    String[] lines = keywordText.split("\n");
    for (String line : lines) {
      line = line.trim();
      if (!line.isEmpty()) {
        String[] parts = line.split("=");
        if (parts.length == 2) {
          String keyword = parts[0].trim();
          String substitute = parts[1].trim();
          keywordSubstitutions.put(keyword, substitute);
        }
      }
    }

    return keywordSubstitutions;
  }
}
