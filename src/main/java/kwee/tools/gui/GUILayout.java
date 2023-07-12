package kwee.tools.gui;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;
import kwee.library.AboutWindow;
import kwee.library.ShowPreferences;
import kwee.logger.MyLogger;
import kwee.logger.TextAreaHandler;
import kwee.tools.main.Main;
import kwee.tools.main.UserSetting;

/**
 * Define GUI for Garmin trip summary tool.
 * 
 * @author rshkw
 *
 */
public class GUILayout extends JPanel implements ItemListener {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  private static final long serialVersionUID = 1L;
  static final String c_CopyrightYear = "2023";
  private static String c_reponame = "ReplaceTool";

  // Loglevels: OFF SEVERE WARNING INFO CONFIG FINE FINER FINEST ALL
  static final String[] c_levels = { "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL" };
  static final String[] c_LogToDisk = { "Yes", "No" };

  // Replace "path/to/help/file" with the actual path to your help file
  static final String m_HelpFile = "tools.chm";

  private JTextArea output;

  // Variables
  private JTextArea keywordTextArea;
  private JTextArea inputTextArea;
  private JButton selectInputButton;
  private JButton selectOutputButton;
  private JButton substituteButton;
  private JButton selectSubstitutionsButton;

  // Preferences
  private UserSetting m_param = Main.m_param;

  private boolean m_toDisk = false;
  private Level m_Level = Level.INFO;
  private File m_OutputFolder;
  private String m_LogDir = "c:\\";

  private File m_Substitute_file;
  private File m_InputFile;
  private File m_OutputFile;

  /**
   * @wbp.parser.entryPoint
   */
  public GUILayout(JFrame frame) {
    Container pane = frame.getContentPane();
    pane.setLayout(new MigLayout("fill"));

    // Initialize parameters
    JLabel lblOutputFolder = new JLabel("");
    if (!m_param.get_OutputFolder().isBlank()) {
      m_OutputFolder = new File(m_param.get_OutputFolder());
      lblOutputFolder.setText(m_OutputFolder.getAbsolutePath());
    }

    m_Level = m_param.get_Level();
    m_toDisk = m_param.is_toDisk();
    m_LogDir = m_param.get_LogDir();

    m_Substitute_file = new File(m_param.get_Substitutes_file());
    m_InputFile = new File(m_param.get_InputFile());
    m_OutputFile = new File(m_param.get_OutputFile());

    // GUI items
    JMenuBar menuBar = new JMenuBar();
    JMenuItem mntmLoglevel = new JMenuItem("Loglevel");

    // Define Setting menu in menu bar:
    JMenu mnSettings = new JMenu("Settings");
    menuBar.add(mnSettings);

    // Option log level
    mntmLoglevel.setHorizontalAlignment(SwingConstants.LEFT);
    mntmLoglevel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Loglevel");
        String level = "";
        level = (String) JOptionPane.showInputDialog(frame, "Loglevel?", "INFO", JOptionPane.QUESTION_MESSAGE, null,
            c_levels, m_Level.toString());
        if (level != null) {
          m_Level = Level.parse(level.toUpperCase());
          m_param.set_Level(m_Level);
          MyLogger.changeLogLevel(m_Level);
        }
      }
    });
    mnSettings.add(mntmLoglevel);

    // Add item Look and Feel
    JMenu menu = new JMenu("Look and Feel");
    menu.setHorizontalAlignment(SwingConstants.LEFT);
    mnSettings.add(menu);

    // Get all the available look and feel that we are going to use for
    // creating the JMenuItem and assign the action listener to handle
    // the selection of menu item to change the look and feel.
    UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
    for (UIManager.LookAndFeelInfo lookAndFeelInfo : lookAndFeels) {
      JMenuItem item = new JMenuItem(lookAndFeelInfo.getName());
      item.addActionListener(event -> {
        try {
          // Set the look and feel for the frame and update the UI
          // to use a new selected look and feel.
          UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
          SwingUtilities.updateComponentTreeUI(this);
          m_param.set_LookAndFeel(lookAndFeelInfo.getClassName());
        } catch (Exception e) {
          LOGGER.log(Level.WARNING, e.getMessage());
        }
      });
      menu.add(item);
      frame.setJMenuBar(menuBar);
    }

    // Option Logging to Disk
    JCheckBoxMenuItem mntmLogToDisk = new JCheckBoxMenuItem("Create logfiles");
    mntmLogToDisk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = mntmLogToDisk.isSelected();
        if (selected) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fileChooser.setSelectedFile(new File(m_LogDir));
          int option = fileChooser.showOpenDialog(GUILayout.this);
          if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            LOGGER.log(Level.INFO, "Log folder: " + file.getAbsolutePath());
            m_LogDir = file.getAbsolutePath() + "\\";
            m_param.set_LogDir(m_LogDir);
            m_param.set_toDisk(true);
            m_toDisk = selected;
          }
        } else {
          m_param.set_toDisk(false);
          m_toDisk = selected;
        }
        try {
          MyLogger.setup(m_Level, m_LogDir, m_toDisk);
        } catch (IOException es) {
          LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + es.toString());
          es.printStackTrace();
        }
      }
    });
    mnSettings.add(mntmLogToDisk);

    // Option Preferences
    JMenuItem mntmPreferences = new JMenuItem("Preferences");
    mntmPreferences.setName("Preferences");
    mntmPreferences.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ShowPreferences showpref = new ShowPreferences(UserSetting.NodePrefName);
        showpref.showAllPreferences();
      }
    });
    mnSettings.add(mntmPreferences);

    // Option All preferences
    JMenuItem mntmAllPreferences = new JMenuItem("All preferences");
    mntmAllPreferences.setName("All preferences");
    mntmAllPreferences.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ShowPreferences showpref = new ShowPreferences();
        showpref.showAllPreferences();
      }
    });
    mnSettings.add(mntmAllPreferences);

    // ? item
    JMenu mnHelpAbout = new JMenu("?");
    mnHelpAbout.setHorizontalAlignment(SwingConstants.RIGHT);
    menuBar.add(mnHelpAbout);

    // Help
    JMenuItem mntmHelp = new JMenuItem("Help");
    mntmHelp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File helpFile = new File(m_HelpFile);

        if (helpFile.exists()) {
          try {
            // Open the help file with the default viewer
            Desktop.getDesktop().open(helpFile);
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        } else {
          LOGGER.log(Level.INFO, "Help file not found " + helpFile.getAbsolutePath());
        }
      }
    });
    mnHelpAbout.add(mntmHelp);

    // About
    JMenuItem mntmAbout = new JMenuItem("About");
    mntmAbout.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        AboutWindow l_window = new AboutWindow(c_reponame, Main.m_creationtime, c_CopyrightYear);
        l_window.setVisible(true);

        // @formatter:off
        /*
        
        JFrame frame = new JFrame("About");
        String l_message = "GarminSummary version " + Main.m_creationtime + "\n\nCopyright © " + c_CopyrightYear;
        JOptionPane.showMessageDialog(frame, l_message, "About", JOptionPane.PLAIN_MESSAGE);
                */
        // @formatter:on
      }
    });
    mnHelpAbout.add(mntmAbout);

    // Do Layout
    keywordTextArea = new JTextArea(10, 30);
    JScrollPane keywordScrollPane = new JScrollPane(keywordTextArea);
    keywordScrollPane.setBorder(BorderFactory.createTitledBorder("Keyword Substitutions"));
    pane.add(keywordScrollPane, "grow, wrap");

    inputTextArea = new JTextArea(10, 30);
    JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
    inputScrollPane.setBorder(BorderFactory.createTitledBorder("Input Text"));
    pane.add(inputScrollPane, "grow, wrap");

    selectSubstitutionsButton = new JButton("Select Substitutions File");
    selectSubstitutionsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectSubstitutionsFile();
      }
    });
    pane.add(selectSubstitutionsButton, "split 2");

    selectInputButton = new JButton("Select Input File");
    selectInputButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectInputFile();
      }
    });
    pane.add(selectInputButton, "wrap");

    substituteButton = new JButton("Substitute Keywords");
    substituteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performKeywordSubstitution();
      }
    });
    pane.add(substituteButton, "span, wrap");

    selectOutputButton = new JButton("Select Output File");
    selectOutputButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectOutputFile();
      }
    });
    pane.add(selectOutputButton, "wrap");

    // Build output area.
    output = new JTextArea(10, 10);
    try {
      MyLogger.setup(m_Level, m_LogDir, m_toDisk);
    } catch (IOException es) {
      LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + es.toString());
      es.printStackTrace();
    }
    Logger rootLogger = Logger.getLogger("");
    for (Handler handler : rootLogger.getHandlers()) {
      if (handler instanceof TextAreaHandler) {
        TextAreaHandler textAreaHandler = (TextAreaHandler) handler;
        output = textAreaHandler.getTextArea();
      }
    }

    output.setEditable(false);
    output.setTabSize(4);

    JScrollPane outputPane = new JScrollPane(output);
    outputPane.setPreferredSize(new Dimension(60, 120)); // Widht Height
    pane.add(outputPane, "grow, wrap");
  }

  private void selectInputFile() {
    JFileChooser fileChooser = new JFileChooser(m_InputFile);
    fileChooser.setDialogTitle("Inputfile");
    fileChooser.setSelectedFile(m_InputFile);
    int result = fileChooser.showOpenDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      try {
        m_param.set_InputFile(selectedFile);
        BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line).append("\n");
        }
        reader.close();
        inputTextArea.setText(content.toString());
      } catch (IOException e) {
        LOGGER.log(Level.INFO, "Inputfile: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private void selectOutputFile() {
    JFileChooser fileChooser = new JFileChooser(m_OutputFile);
    fileChooser.setDialogTitle("Outputfile");
    fileChooser.setSelectedFile(m_OutputFile);
    int result = fileChooser.showSaveDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      File outputFile = fileChooser.getSelectedFile();
      m_param.set_OutputFile(outputFile);
      performKeywordSubstitution();
      try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(inputTextArea.getText());
        writer.close();
      } catch (IOException e) {
        LOGGER.log(Level.INFO, "Outputfile: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private void selectSubstitutionsFile() {
    JFileChooser fileChooser = new JFileChooser(m_Substitute_file);
    fileChooser.setDialogTitle("Substition file");
    fileChooser.setSelectedFile(m_Substitute_file);
    int result = fileChooser.showOpenDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      try {
        m_param.set_Substitutes_file(selectedFile);
        ;
        BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line).append("\n");
        }
        reader.close();
        keywordTextArea.setText(content.toString());
      } catch (IOException e) {
        LOGGER.log(Level.INFO, "Substitionfile: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private void performKeywordSubstitution() {
    String inputText = inputTextArea.getText();
    String keywordText = keywordTextArea.getText();

    Map<String, String> keywordSubstitutions = parseKeywordSubstitutions(keywordText);
    LOGGER.log(Level.INFO, "Start keyword substition.");

    for (Map.Entry<String, String> entry : keywordSubstitutions.entrySet()) {
      String keyword = entry.getKey();
      String substitute = entry.getValue();
      inputText = inputText.replaceAll(keyword, substitute);
    }

    inputTextArea.setText(inputText);
    LOGGER.log(Level.INFO, "Substition performned.");
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

  /**
   * Must be overridden..
   */
  @Override
  public void itemStateChanged(ItemEvent e) {
    LOGGER.log(Level.INFO, "itemStateChanged");
  }
}
