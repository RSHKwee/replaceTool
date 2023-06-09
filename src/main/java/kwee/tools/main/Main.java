package kwee.tools.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import kwee.library.JarInfo;
import kwee.tools.gui.GUILayout;

/**
 * Main program for ING csv to OFX convertor
 * 
 * @author kweers1
 */

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Class.class.getName());
  static String m_MenuTitel = "Replace text tool, version ";
  static public String m_creationtime;
  static String m_LookAndFeel = "Nimbus";
  public static UserSetting m_param = new UserSetting();
  static boolean m_ConfirmOnExit = false;

  /**
   * Create the GUI and show it. For thread safety, this method should be invoked
   * from the event-dispatching thread.
   */
  private static void createAndShowGUI() {
    // Set the look and feel.
    initLookAndFeel();

    JFrame frame = new JFrame("Keyword Substitution");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.addWindowListener(new WindowListener() {

      @Override
      public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void windowClosing(WindowEvent e) {
        JFrame frame = (JFrame) e.getSource();
        if (m_ConfirmOnExit) {
          int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit the application?",
              "Exit Application", JOptionPane.YES_NO_OPTION);

          if (result == JOptionPane.YES_OPTION) {
            m_param.save();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          }
        } else {
          m_param.save();
          LOGGER.log(Level.FINE, m_param.print());
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
      }

      @Override
      public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub
      }
    });

    GUILayout scenGUI = new GUILayout(frame);
    scenGUI.setOpaque(true);

    frame.pack();
    frame.setVisible(true);

    LOGGER.log(Level.INFO, "Replace tool versie : " + m_creationtime);
  }

  /**
   * Initialiseer Look and Feel
   */
  private static void initLookAndFeel() {
    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        LOGGER.fine(" Look And Feel :" + info.getName());
        if (m_LookAndFeel.equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception e) {
      // If Nimbus is not available, you can set the GUI to another look and feel.
    }
  }

  /**
   * @formatter:off
   * Main start de GUI
   * @param args 0 Confirm on exit "true" or "false" (d)
   *        args 1 Look and feel,  "Nimbus" (d) or "Metal", etc.
   * @formatter:on
   */
  public static void main(String[] argv) {
    // Get the jvm heap size.
    long heapSize = Runtime.getRuntime().totalMemory();

    m_LookAndFeel = m_param.get_LookAndFeel();
    m_creationtime = JarInfo.getProjectVersion(GUILayout.class);
    m_ConfirmOnExit = m_param.is_ConfirmOnExit();

    switch (argv.length) {
    case 1: {
      if (argv[0].toLowerCase().startsWith("t")) {
        m_ConfirmOnExit = true;
        m_param.set_ConfirmOnExit(m_ConfirmOnExit);
      } else {
        m_ConfirmOnExit = false;
        m_param.set_ConfirmOnExit(m_ConfirmOnExit);
      }
      break;
    }
    case 2: {
      if (argv[0].toLowerCase().startsWith("t")) {
        m_ConfirmOnExit = true;
        m_param.set_ConfirmOnExit(true);
      } else {
        m_ConfirmOnExit = false;
        m_param.set_ConfirmOnExit(false);
      }
      m_LookAndFeel = argv[1];
      m_param.set_LookAndFeel(m_LookAndFeel);
    }
    default: {
    }
    }
    // Print the jvm heap size.
    LOGGER.log(Level.INFO, "Heap Size = " + heapSize);

    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        createAndShowGUI();
      }
    });
  }
}
