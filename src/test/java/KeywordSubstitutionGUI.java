

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
  private JButton selectSubstitutionsButton;
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
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    pane.add(keywordScrollPane, c);

    inputTextArea = new JTextArea(10, 30);
    JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
    inputScrollPane.setBorder(BorderFactory.createTitledBorder("Input Text"));
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    pane.add(inputScrollPane, c);

    selectInputButton = new JButton("Select Input File");
    selectInputButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectInputFile();
      }
    });
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    pane.add(selectInputButton, c);

    selectOutputButton = new JButton("Select Output File");
    selectOutputButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectOutputFile();
      }
    });
    c.gridx = 1;
    c.gridy = 2;
    pane.add(selectOutputButton, c);

    selectSubstitutionsButton = new JButton("Select Substitutions File");
    selectSubstitutionsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectSubstitutionsFile();
      }
    });
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 2;
    pane.add(selectSubstitutionsButton, c);

    substituteButton = new JButton("Substitute Keywords");
    substituteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performKeywordSubstitution();
      }
    });
    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 2;
    pane.add(substituteButton, c);
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

  private void selectSubstitutionsFile() {
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
        keywordTextArea.setText(content.toString());
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
