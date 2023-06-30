package kwee.tools.main;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * User setting persistence.
 * 
 * @author rshkw
 *
 */
public class UserSetting {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

  private String c_Level = "Level";
  private String c_LevelValue = "INFO";

  private String c_ConfirmOnExit = "ConfirmOnExit";
  private String c_toDisk = "ToDisk";
  private String c_LogDir = "LogDir";
  private String c_LookAndFeel = "LookAndFeel";
  private String c_LookAndFeelVal = "Nimbus";
  private String c_InputFile = "InputFile";
  private String c_OutputFile = "OutputFile";
  private String c_Substitutes_file = "SubstitutesFile";

  private String m_Level = c_LevelValue;
  private String m_LookAndFeel;

  private String m_OutputFolder = "";
  private File[] m_CsvFiles = null;
  private String m_LogDir = "";

  private boolean m_ConfirmOnExit = false;
  private boolean m_toDisk = false;

  private String m_Substitutes_file = "";
  private String m_InputFile = "";
  private String m_OutputFile = "";

  private Preferences pref;
  private Preferences userPrefs = Preferences.userRoot();

  /**
   * Constructor Initialize settings
   */
  public UserSetting() {
    // Navigate to the preference node that stores the user setting
    pref = userPrefs.node("kwee.replaceTool");

    m_toDisk = pref.getBoolean(c_toDisk, false);
    m_ConfirmOnExit = pref.getBoolean(c_ConfirmOnExit, false);
    m_LookAndFeel = pref.get(c_LookAndFeel, c_LookAndFeelVal);

    m_InputFile = pref.get(c_InputFile, "");
    m_OutputFile = pref.get(c_OutputFile, "");
    m_Substitutes_file = pref.get(c_Substitutes_file, "");

    m_Level = pref.get(c_Level, c_LevelValue);
    m_LogDir = pref.get(c_LogDir, "");
  }

  public String get_LogDir() {
    return m_LogDir;
  }

  public void set_LogDir(String m_LogDir) {
    this.m_LogDir = m_LogDir;
  }

  public String get_OutputFolder() {
    return m_OutputFolder;
  }

  public File[] get_CsvFiles() {
    return m_CsvFiles;
  }

  public Level get_Level() {
    return Level.parse(m_Level);
  }

  public String get_LookAndFeel() {
    return m_LookAndFeel;
  }

  public String get_Substitutes_file() {
    return m_Substitutes_file;
  }

  public String get_InputFile() {
    return m_InputFile;
  }

  public String get_OutputFile() {
    return m_OutputFile;
  }

  public boolean is_toDisk() {
    return m_toDisk;
  }

  public boolean is_ConfirmOnExit() {
    return m_ConfirmOnExit;
  }

  public void set_Substitutes_file(File a_Substitutes_file) {
    pref.put(c_Substitutes_file, a_Substitutes_file.getAbsolutePath());
    this.m_Substitutes_file = a_Substitutes_file.getAbsolutePath();
  }

  public void set_toDisk(boolean a_toDisk) {
    pref.putBoolean(c_toDisk, a_toDisk);
    this.m_toDisk = a_toDisk;
  }

  public void set_Level(Level a_Level) {
    pref.put(c_Level, a_Level.toString());
    this.m_Level = a_Level.toString();
  }

  public void set_LookAndFeel(String a_LookAndFeel) {
    pref.put(c_LookAndFeel, a_LookAndFeel);
    this.m_LookAndFeel = a_LookAndFeel;
  }

  public void set_ConfirmOnExit(boolean a_ConfirmOnExit) {
    pref.putBoolean(c_ConfirmOnExit, a_ConfirmOnExit);
    this.m_ConfirmOnExit = a_ConfirmOnExit;
  }

  public void set_InputFile(File a_InputFile) {
    pref.put(c_InputFile, a_InputFile.getAbsolutePath());
    this.m_InputFile = a_InputFile.getAbsolutePath();
  }

  public void set_OutputFile(File a_OutputFile) {
    pref.put(c_OutputFile, a_OutputFile.getAbsolutePath());
    this.m_OutputFile = a_OutputFile.getAbsolutePath();
  }

  /**
   * Save all settings
   */
  public void save() {
    try {
      pref.putBoolean(c_toDisk, m_toDisk);

      pref.putBoolean(c_ConfirmOnExit, m_ConfirmOnExit);
      pref.put(c_Level, m_Level);
      pref.put(c_LogDir, m_LogDir);

      pref.put(c_LookAndFeel, m_LookAndFeel);
      pref.put(c_Substitutes_file, m_Substitutes_file);
      pref.put(c_OutputFile, m_OutputFile);
      pref.put(c_InputFile, m_InputFile);

      pref.flush();
    } catch (BackingStoreException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }
  }

  public String print() {
    String l_line = "User setting \n";
    l_line = l_line + "Name: " + pref.name() + "\n";
    l_line = l_line + c_toDisk + ": " + m_toDisk + "\n";
    l_line = l_line + c_ConfirmOnExit + ": " + m_ConfirmOnExit + "\n";

    l_line = l_line + c_LookAndFeel + ": " + m_LookAndFeel + "\n";
    l_line = l_line + c_Substitutes_file + ": " + m_Substitutes_file + "\n";
    l_line = l_line + c_InputFile + ": " + m_InputFile + "\n";
    l_line = l_line + c_OutputFile + ": " + m_OutputFile + "\n";

    l_line = l_line + c_Level + ": " + m_Level + "\n";
    l_line = l_line + c_LogDir + ": " + m_LogDir + "\n";

    return l_line;
  }
}
