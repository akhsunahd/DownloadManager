import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

//import.Download;
 
// The Download Manager.
public class DownloadManager extends JFrame
        implements Observer {
     
    // Add download text field.
    private JTextField addTextField;
     
    // Download table's data model.
    private DownloadsTableModel tableModel;
     
    // Table listing downloads.
    private JTable table;
     
    // These are the buttons for managing the selected download.
    private JButton pauseButton, resumeButton;
    private JButton cancelButton, clearButton;
     
    // Currently selected download.
    private Download selectedDownload;
    private JLabel saveFileLabel = new JLabel();
    // Flag for whether or not table selection is being cleared.
    private boolean clearing;
     
    // Constructor for Download Man ager.
    public DownloadManager() {
        // Set application title.
        setTitle("Download Manager");
         
        // Set window size.
        setSize(1000, 600);
         
        // Handle window closing events.
        addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
        JFrame proxyset = new JFrame("Proxy Settings");
     //   proxyset.setSize(100, 100);
        proxyset.setBounds(200, 200, 400, 200);
        
       JButton saveproxy = new JButton ("Save");
       JButton cancelproxy = new JButton ("Cancel");
       saveproxy.setBounds(300, 300, 20, 10);
       cancelproxy.setBounds(400, 300, 20, 10);
     //  saveproxy.setPreferredSize(new Dimension(10,10));
      // cancelproxy.setPreferredSize(new Dimension(10,10));
    //   proxyset.add(saveproxy);
     //  proxyset.add(cancelproxy);
        // Set up file menu.
        JMenuBar menuBar = new JMenuBar();
        
        JTextField userInput = new JTextField(10);
        JMenu fileMenu = new JMenu("File");
        JMenu optionMenu=new JMenu("Options");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("Exit",
                KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        JMenuItem proxysettingsMenuItem = new JMenuItem("Proxy",
                KeyEvent.VK_X);
        
        proxysettingsMenuItem.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
             //   actionExit();
            	proxyset.setVisible(true);
            }
        });
        
        
        
        fileMenu.add(fileExitMenuItem);
        optionMenu.add(proxysettingsMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(optionMenu);
        
        setJMenuBar(menuBar);
         
        // Set up add panel.
        JPanel addPanel = new JPanel();
        
        
        addTextField = new JTextField(30);
        addPanel.add(addTextField);
        
        
        /*
        JButton addButton = new JButton("Add Download");
        addButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                actionAdd();
            }
        });
        addPanel.add(addButton);
        
        JPanel destinationPanel = new JPanel(new BorderLayout());
        saveFileLabel.setText("File:");
        destinationPanel.add(saveFileLabel, BorderLayout.WEST);
        
        JButton saveFileButton = new JButton("Download To");
        saveFileButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
     //  actionSaveTo();
        }
        });
        destinationPanel.add(saveFileButton, BorderLayout.EAST);
        
        
        */
        
        JPanel targetPanel = new JPanel(new BorderLayout());
        targetPanel.add(addTextField, BorderLayout.WEST);
        JButton addButton = new JButton("Add Download");
        addButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        actionAdd();
        }
        });

        targetPanel.add(addButton, BorderLayout.EAST);

        JPanel destinationPanel = new JPanel(new BorderLayout());
        //saveFileLabel.setText("/home/danushka/download test");
        saveFileLabel.setText("File :");
        destinationPanel.add(saveFileLabel, BorderLayout.WEST);

        JButton saveFileButton = new JButton("Download To");
        saveFileButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        actionSaveTo();
        }
        });
        destinationPanel.add(saveFileButton, BorderLayout.EAST);
        addPanel.add(destinationPanel, BorderLayout.NORTH);
        addPanel.add(targetPanel, BorderLayout.SOUTH);
        
        
        
        // Set up Downloads table.
        tableModel = new DownloadsTableModel();
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(new
                ListSelectionListener() {
            @Override
			public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged();
            }
        });
        // Allow only one row at a time to be selected.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         
        // Set up ProgressBar as renderer for progress column.
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true); // show progress text
        table.setDefaultRenderer(JProgressBar.class, renderer);
         
        // Set table's row height large enough to fit JProgressBar.
        table.setRowHeight(
                (int) renderer.getPreferredSize().getHeight());
         
        // Set up downloads panel.
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setBorder(
                BorderFactory.createTitledBorder("Downloads"));
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table),
                BorderLayout.CENTER);
         
        // Set up buttons panel.
        JPanel buttonsPanel = new JPanel();
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                actionPause();
            }
        });
        pauseButton.setEnabled(false);
        buttonsPanel.add(pauseButton);
        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                actionResume();
            }
        });
        resumeButton.setEnabled(false);
        buttonsPanel.add(resumeButton);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                actionCancel();
            }
        });
        cancelButton.setEnabled(false);
        buttonsPanel.add(cancelButton);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                actionClear();
            }
        });
        clearButton.setEnabled(false);
        buttonsPanel.add(clearButton);
         
        // Add panels to display.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(addPanel, BorderLayout.NORTH);
        getContentPane().add(downloadsPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }
     
    // Exit this program.
    private void actionExit() {
        System.exit(0);
    }
    //file saving position
    private void actionSaveTo()
    {

    JFileChooser jfchooser = new JFileChooser();

    jfchooser.setApproveButtonText("OK");
    jfchooser.setDialogTitle("Save To");
    jfchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int result = jfchooser.showOpenDialog(this);
    File newZipFile = jfchooser.getSelectedFile();
    System.out.println("importProfile:" + newZipFile);
    this.saveFileLabel.setText(newZipFile.getPath());

    }
    // Add a new download.
    private void actionAdd() {
        URL verifiedUrl = verifyUrl(addTextField.getText());
        if (verifiedUrl != null) {
           // tableModel.addDownload(new Download(verifiedUrl, saveFileLabel.getText()));
            tableModel.addDownload(new Download(verifiedUrl, saveFileLabel.getText()));
            addTextField.setText(""); // reset add text field
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid Download URL", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
     
    // Verify download URL.
    private URL verifyUrl(String url) {
        // Only allow HTTP URLs.
        if (!url.toLowerCase().startsWith("http://"))
            return null;
         
        // Verify format of URL.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }
         
        // Make sure URL specifies a file.
        if (verifiedUrl.getFile().length() < 2)
            return null;
         
        return verifiedUrl;
    }
     
    // Called when table row selection changes.
    private void tableSelectionChanged() {
    /* Unregister from receiving notifications
       from the last selected download. */
        if (selectedDownload != null)
            selectedDownload.deleteObserver(DownloadManager.this);
         
    /* If not in the middle of clearing a download,
       set the selected download and register to
       receive notifications from it. */
        if (!clearing) {
            selectedDownload =
                    tableModel.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(DownloadManager.this);
            updateButtons();
        }
    }
     
    // Pause the selected download.
    private void actionPause() {
        selectedDownload.pause();
        updateButtons();
    }
     
    // Resume the selected download.
    private void actionResume() {
        selectedDownload.resume();
        updateButtons();
    }
     
    // Cancel the selected download.
    private void actionCancel() {
        selectedDownload.cancel();
        updateButtons();
    }
     
    // Clear the selected download.
    private void actionClear() {
        clearing = true;
        tableModel.clearDownload(table.getSelectedRow());
        clearing = false;
        selectedDownload = null;
        updateButtons();
    }
     
  /* Update each button's state based off of the
     currently selected download's status. */
    private void updateButtons() {
        if (selectedDownload != null) {
            int status = selectedDownload.getStatus();
            switch (status) {
                case Download.DOWNLOADING:
                    pauseButton.setEnabled(true);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.PAUSED:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    clearButton.setEnabled(false);
                    break;
                case Download.ERROR:
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(true);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
                    break;
                default: // COMPLETE or CANCELLED
                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    clearButton.setEnabled(true);
            }
        } else {
            // No download is selected in table.
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            cancelButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
    }
     
  /* Update is called when a Download notifies its
     observers of any changes. */
    @Override
	public void update(Observable o, Object arg) {
        // Update buttons if the selected download has changed.
        if (selectedDownload != null && selectedDownload.equals(o))
            updateButtons();
    }
     
    // Run the Download Manager.
    public static void main(String[] args) {
        DownloadManager manager = new DownloadManager();
        manager.show();
    }
}