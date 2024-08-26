import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.Desktop;
import java.io.IOException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;


public class GraphicInterface extends JFrame {

    private final String SPACE_BETWEEN_PARAGRAPHS = "----------------------------------------------------";
    private final int CHECK_BOX_FIRST_LINE_X = 80;
    private final int CHECK_BOX_SECOND_LINE_X = 310;
    private final int CHECK_BOX_THIRD_LINE_X = 530;
    private final int CHECK_BOX_Fourth_LINE_X = 770;
    private final int CHECK_BOX_FIRST_LINE_Y = 280;
    private final int CHECK_BOX_SECOND_LINE_Y = 380;
    private final int CHECK_BOX_THIRD_LINE_Y = 480;
    private ArrayList<String> suspiciousOrders = new ArrayList<>();  // מערך פקודות חשודות
    private String filePath;  // נתיב הקובץ הנבדק
    private String filesStorageDestination;  // נתיב איחסון הממצאים
    private String openingParagraphToFindingsReport; // פיסקת פתיחה לדוח הממצאים
    private String finishingParagraphToFindingsReport; // פיסקת סיום לדוח הממצאים
    private String findingsReportContents;  // תוכן דוח הממצאים
    Font font; // עיצוב טקסט דוח הממצאים
    private int malwarePresent ; // האחוזים לקובץ נגוע לאחר שכלול כלל הפרמטרים
    private String arrivalWay; // דרך הגעת הקובץ למשתמש
    private String arrivalWayDescription; // דרך הגעת הקובץ למשתמש
    private boolean encoded; // האם הקובץ מוצפן



    // Constructor
    public GraphicInterface() {
        //שדות ברירת מחדל
        malwarePresent = 0;
        encoded = false;
        arrivalWayDescription = "";
        font = new Font("Arial", Font.BOLD, 18);

        // יצירה ועיצוב החלון הגרפי
        setTitle("Cyber File Analyzer");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // הגדרת layout ל-null

        // יצירת פאנל רקע מותאם אישית
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("C:\\Users\\ariel\\Desktop\\פרויקט גמר  תכנות ישומי\\malwareScanner\\src\\main\\java\\G.N.png.jpg"); // נתיב לתמונת הרקע
                g.drawImage(background.getImage(), 0, 0, 1200, 600, this);
            }
        };
        backgroundPanel.setLayout(null); // הגדרת layout של הפאנל ל-null
        backgroundPanel.setBounds(0, 0, 1200, 600); // הגדרת מיקום וגודל הפאנל
        add(backgroundPanel);

        // כפתור העלאת קובץ
        JButton uploadButton = new JButton("Upload a file");
        uploadButton.setFont(font);
        uploadButton.setBounds(520, 80, 150, 30);
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePath = selectedFile.getAbsolutePath();
                    JOptionPane.showMessageDialog(null, "The file chosen is: " + filePath);
                }
            }
        });

        // כפתור בחירת נתיב שמירה לדוח הממצאים
        JButton destinationButton = new JButton("Findings report location");
        destinationButton.setFont(font);
        destinationButton.setBounds(470, 130, 250, 30);
        destinationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile();
                    filesStorageDestination = selectedDirectory.getAbsolutePath() + "\\";
                    JOptionPane.showMessageDialog(null, "The findings report location is: " + filesStorageDestination + "\n");
                }
            }
        });

        // כפתור הפעלה
        JButton runButton = new JButton("Run");
        runButton.setFont(font);
        runButton.setBounds(520, 180, 150, 30);
        runButton.addActionListener((event) -> {
            if (filePath != null && filesStorageDestination != null) {

                // יצירת חלון מודאלי שלא ניתן לסגור
                JDialog dialog = new JDialog();
                dialog.setTitle("Waiting...");
                dialog.setModal(true);  // הופך את החלון למודאלי
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // מניעת סגירת החלון
                dialog.setSize(300, 150);
                dialog.setLocationRelativeTo(null);
                dialog.setLayout(new BorderLayout());

                // הודעת המתנה לסיום סריקת הקובץ ע"י התוכנית
                JLabel messageLabel = new JLabel("The program is scanning your file, please wait...", SwingConstants.CENTER);
                dialog.add(messageLabel, BorderLayout.CENTER);
                new Thread(() -> {
                    try {
                        dialog.setVisible(true);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }).start();

                // הגדרת פסקת הפתיחה לדוח הממצאים
                openingParagraphToFindingsReport =
                        "Welcome to Trojan Tracker engine.\n\n"+
                                " After checking the file, these are the findings found:  \n\n"
                                + SPACE_BETWEEN_PARAGRAPHS + "\n";
                findingsReportContents = openingParagraphToFindingsReport;


                  // Bin Text <- איתור פקודות חשודות
                    BinText binText = new BinText(filePath, filesStorageDestination, suspiciousOrders);

                     // אם קיימות פקודות חשודות
                        malwarePresent += binText.getPresent();



                    // איתור מידע אודות זהותו באנטי וירוסים אחרים -> Virus Total
                    VirusTotal virusTotal = new VirusTotal(this.filePath);
                try { // זמן לטעינת הקובץ טרם שליחת בקשת הAPI
                    Thread.sleep(7500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                virusTotal.startProgram();



 // ---------------------------------------------------------------------------------------------

                // איתור מידע מוצפן

                    // באמצעות PiED (הצפנת UPX)
    // ביצוע הרצת PiED מחוץ ל-Event Dispatch Thread (EDT) (עקב אי סינכרון פעולות בגלל ההמשק הגרפי)
                // יצירת SwingWorker לביצוע הרצת PiED מחוץ ל-EDT
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        findingsReportContents += arrivalWayDescription;
                        Pied pied = new Pied(filePath);  // הרצת Pied
                        if (pied.isHasUpxEncoded()) { // אם סוג הקובץ הוא UPX
                            System.out.println("is UPX encoded: " + pied.isHasUpxEncoded());
                            malwarePresent += 10;
                            encoded = true;

                            // עידכון גוף הדוח ע"פ ממצאי PiED
                            findingsReportContents += pied.getDescription();
                        }
                        return null;
                    }

                    // משום שPiED רץ מחוץ לEDT הוא רץ אחרון. לכן כלל העידכונים לדוח הממצאים יבוצעו פה +
                    // כתיבת דוח הממצאים עצמו עם התוכן המעודכן
                    @Override
                    protected void done() {
                        try {
                            // המתנה לסיום ריצת התוכנית PiED
                            get();


                            //  מציאת הצפנות באמצעות BinText (תווים מיוחדים שאינם מופיעים לרוב)
                            // שים לב אם נמצאה הצפנת UPS לא תתבצע בדיקת הצפנה חוזרת בBimText
                            if (!encoded) {
                                binText.isTheFileEncoded();
                                if (binText.isStrongEncoded()) {
                                    malwarePresent += 10; // מעל 50% תווים מיוחדים
                                } else if (binText.isEncoded()) {
                                    malwarePresent += 5; // מעל 20% תווים מיוחדים
                                }
                            }
                            // מחיקת קובץ הפקודות שנוצר באמצעות binText
                            binText.deleteOrderFail();

                            // עידכון גוף הדוח ע"פ ממצאי BinText
                            findingsReportContents += binText.getDescriptionOrder();

                            // עידכון הדוח ע"פ ממצאי VirusTotal
                            findingsReportContents += virusTotal.getDescription();
                            malwarePresent += virusTotal.getPresent();

                                    {
                                // TODO: הרחבה עתידית* - עידכון תוכן דוח הממצאים בשאר ממצאי התוכניות*
                            }

                            // נירמול/ולידציה לאחוזי המצאות נוזקה בקובץ
                            if (malwarePresent >= 95){
                                malwarePresent = 95;
                            }

                            // הוספת ההסתברות הכוללת לנוזקה (באחוזים) + פסקת סיום לדוח הממצאים
                            finishingParagraphToFindingsReport =
                                    "\n\n" +
                                            SPACE_BETWEEN_PARAGRAPHS + "\n" +
                                            "After calculating all the findings, Trojan Tracker engine found that the possibility that your file is damaged is about: "+ malwarePresent + " percent!" + "\n\n" +
                                            SPACE_BETWEEN_PARAGRAPHS + "\n" +
                                            " **Note, \n" +
                                            "In front of you are the findings, you must verify them against the type of file you downloaded. \n" +
                                            "We give an indication of suspicious things, if so there are legitimate files that do these actions. \n" +
                                            "Therefore, you should check the findings against the nature of the file that was shown to you when you downloaded it.\n";

                            
                            findingsReportContents += finishingParagraphToFindingsReport;

                            // כעת מבצעים את כתיבת הדוח לאחר ש-findingReportContents עודכן במלואו

                            Document reportFile = new Document();
                            try {
                                // יצירת קובץ ה-PDF
                                PdfWriter.getInstance(reportFile, new FileOutputStream(filesStorageDestination+"Findings_report.pdf"));
                                reportFile.open();
                                reportFile.add(new Paragraph(findingsReportContents));
                            } catch (DocumentException | IOException e) {
                                e.printStackTrace();
                            } finally {
                                reportFile.close();

                                // הודעה למשתמש על סיום ריצת התוכנית
                                dialog.dispose();
                                JOptionPane.showMessageDialog(null, "Process completed successfully.\n Report saved to: " + filesStorageDestination+"Findings_report.pdf");
                            }
                            // פתיחת דוח הממצאים למשתמש עם סיום התוכנית

                            openPDF(filesStorageDestination+"Findings_report.pdf");


                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "An error occurred during the analysis: " + e.getMessage());
                        }
                    }
                };

                worker.execute(); // SwingWorker -הפעלת ה
//

                {
                    // TODO: הרחבה עתידית* - פונקציות הריצה של כל שאר התוכניות*
                }

            }
            // במידה ולא נבחר נתיב שמירה לדוח הממצאים
            else {
                JOptionPane.showMessageDialog(null, "Please select both a file and a destination.");
            }
        });

        // הוספת הכפתורים לפאנל
        backgroundPanel.add(uploadButton);
        backgroundPanel.add(destinationButton);
        backgroundPanel.add(runButton);

        // הוראות סימון צ'ק בוקס

         JTextArea checkBoxDescription = new JTextArea ("                                     Please mark how the file got to your computer "+"\n(this information may make the analysis of the file more than 60% more accurate)");
        checkBoxDescription.setBounds(250,220,800,40);
        checkBoxDescription.setFont(font);
        checkBoxDescription.setOpaque(false);
        checkBoxDescription.setForeground(Color.WHITE);
        backgroundPanel.add(checkBoxDescription);

        // הוספה ועיצוב צ'קבוקסים

        JCheckBox option1 = new JCheckBox("From a known website");
        option1.setBounds(CHECK_BOX_FIRST_LINE_X, CHECK_BOX_FIRST_LINE_Y, 200, 30);
        option1.setOpaque(false);
        option1.addActionListener(boxChecked);
        option1.setForeground(Color.WHITE);

        JCheckBox option2 = new JCheckBox("From a unknown website");
        option2.setBounds(CHECK_BOX_FIRST_LINE_X, CHECK_BOX_SECOND_LINE_Y, 200, 30);
        option2.setOpaque(false);
        option2.addActionListener(boxChecked);
        option2.setForeground(Color.WHITE);

        JCheckBox option3 = new JCheckBox("Email from unknown person");
        option3.setBounds(CHECK_BOX_FIRST_LINE_X, CHECK_BOX_THIRD_LINE_Y, 200, 30);
        option3.setOpaque(false);
        option3.addActionListener(boxChecked);
        option3.setForeground(Color.WHITE);

        JCheckBox option4 = new JCheckBox("Unpredictable email from known person");
        option4.setBounds(CHECK_BOX_SECOND_LINE_X, CHECK_BOX_FIRST_LINE_Y, 200, 30);
        option4.setOpaque(false);
        option4.addActionListener(boxChecked);
        option4.setForeground(Color.WHITE);

        JCheckBox option5 = new JCheckBox("From external drive");
        option5.setBounds(CHECK_BOX_SECOND_LINE_X, CHECK_BOX_SECOND_LINE_Y, 200, 30);
        option5.setOpaque(false);
        option5.addActionListener(boxChecked);
        option5.setForeground(Color.WHITE);

        JCheckBox option6 = new JCheckBox("From social networks");
        option6.setBounds(CHECK_BOX_SECOND_LINE_X, CHECK_BOX_THIRD_LINE_Y, 200, 30);
        option6.setOpaque(false);
        option6.addActionListener(boxChecked);
        option6.setForeground(Color.WHITE);

        JCheckBox option7 = new JCheckBox("\"Repair tool\" for detected virus");
        option7.setBounds(CHECK_BOX_THIRD_LINE_X, CHECK_BOX_FIRST_LINE_Y, 250, 30);
        option7.setOpaque(false);
        option7.addActionListener(boxChecked);
        option7.setForeground(Color.WHITE);

        JCheckBox option8 = new JCheckBox("Incompatible file type");
        option8.setBounds(CHECK_BOX_THIRD_LINE_X, CHECK_BOX_SECOND_LINE_Y, 200, 30);
        option8.setOpaque(false);
        option8.addActionListener(boxChecked);
        option8.setForeground(Color.WHITE);

        JCheckBox option9 = new JCheckBox("URL received from a SMS message");
        option9.setBounds(CHECK_BOX_THIRD_LINE_X, CHECK_BOX_THIRD_LINE_Y, 250, 30);
        option9.setOpaque(false);
        option9.addActionListener(boxChecked);
        option9.setForeground(Color.WHITE);

        JCheckBox option10 = new JCheckBox("Pop-up window from unknown website");
        option10.setBounds(CHECK_BOX_Fourth_LINE_X, CHECK_BOX_FIRST_LINE_Y, 300, 30);
        option10.setOpaque(false);
        option10.addActionListener(boxChecked);
        option10.setForeground(Color.WHITE);

        JCheckBox option11 = new JCheckBox("Downloaded automatically while browsing a web site");
        option11.setBounds(CHECK_BOX_Fourth_LINE_X, CHECK_BOX_SECOND_LINE_Y, 400, 30);
        option11.setOpaque(false);
        option11.addActionListener(boxChecked);
        option11.setForeground(Color.WHITE);

        JCheckBox option12 = new JCheckBox("URL that wasn't supposed to download any file");
        option12.setBounds(CHECK_BOX_Fourth_LINE_X, CHECK_BOX_THIRD_LINE_Y, 300, 30);
        option12.setOpaque(false);
        option12.addActionListener(boxChecked);
        option12.setForeground(Color.WHITE);

        option1.setAlignmentX(Component.CENTER_ALIGNMENT);
        option2.setAlignmentX(Component.CENTER_ALIGNMENT);
        option3.setAlignmentX(Component.CENTER_ALIGNMENT);
        option4.setAlignmentX(Component.CENTER_ALIGNMENT);
        option5.setAlignmentX(Component.CENTER_ALIGNMENT);
        option6.setAlignmentX(Component.CENTER_ALIGNMENT);
        option10.setAlignmentX(Component.CENTER_ALIGNMENT);
        option11.setAlignmentX(Component.CENTER_ALIGNMENT);
        option12.setAlignmentX(Component.CENTER_ALIGNMENT);

        //  וידוא שניתן יהיה לבחור אפשרות אחת בלבד

        ButtonGroup group = new ButtonGroup();
        group.add(option1);
        group.add(option2);
        group.add(option3);
        group.add(option4);
        group.add(option5);
        group.add(option6);
        group.add(option7);
        group.add(option8);
        group.add(option9);
        group.add(option10);
        group.add(option11);
        group.add(option12);

        // הוספת הצ'ק בוקסים לפאנל

        backgroundPanel.add(option1);
        backgroundPanel.add(option2);
        backgroundPanel.add(option3);
        backgroundPanel.add(option4);
        backgroundPanel.add(option5);
        backgroundPanel.add(option6);
        backgroundPanel.add(option7);
        backgroundPanel.add(option8);
        backgroundPanel.add(option9);
        backgroundPanel.add(option10);
        backgroundPanel.add(option11);
        backgroundPanel.add(option12);

        setVisible(true);
    }

    //  פונקציות:
    // פונקציה לפתיחת דוח הממצאים
    private void openReportFile(File reportFile) {
        try {
            Desktop.getDesktop().open(reportFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error opening report: " + e.getMessage());
        }
    };

        // עידכון הערך האחוזית לנוזקה ע"פ דרך הגעת הקובץ(הסימון בצ'ק בוקס)
        ActionListener boxChecked = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if (source.isSelected()) {
                    arrivalWay = source.getText();
                    switch (arrivalWay){
                        case "From a known website":
                            malwarePresent = 3;
                            arrivalWayDescription = "- Files downloaded from trusted sources, such as official websites, are generally safe.\n However, there is always a small risk of a fake file or intrusion from third parties.\n";
                            break;
                        case "From a unknown website":
                            malwarePresent = 30;
                            arrivalWayDescription = "- Downloading from unknown sites, file-sharing sites, or links distributed by email or social networks can pose a very high risk,\n especially if proper security measures are not in place.\n";
                            break;
                        case "Email from unknown person":
                            malwarePresent = 50;
                            arrivalWayDescription = "- Files that arrive in unidentified email messages are among the most common means of spreading viruses or malware.\n Be especially wary of files attached to these messages.\n";
                            break;
                        case "Unpredictable email from known person":
                            malwarePresent = 20;
                            arrivalWayDescription = "- It may be a message infected with a virus from the known person's email box or a targeted attack.\n Always check with the sender if he really intended to send the file.\n";
                            break;
                        case "From external drive":
                            malwarePresent = 10;
                            arrivalWayDescription = "- External drives can be infected, especially if they have been swapped between different computers.\n The risk varies depending on the source and condition of the drive.\n";
                            break;
                        case "From social networks":
                            malwarePresent = 30;
                            arrivalWayDescription = "- These services are often used to distribute malware,\n especially if the file was received from an unknown person or as part of an unexpected message.\n";
                            break;
                        case "\"Repair tool\" for detected virus":
                            malwarePresent = 80;
                            arrivalWayDescription = "- Messages announcing computer viruses and offering to download repair tools are often scams aimed at infecting the computer with malicious software.\n";
                            break;
                        case "Incompatible file type":
                            malwarePresent = 80;
                            arrivalWayDescription = "- When the downloaded file type does not match the expected type (for example, downloading an .exe file instead of a .jpg file), the risk is almost certain to be malware.\n Malware may disguise itself as innocent files in order to trick users into running them.\n";
                            break;
                        case "URL received from a SMS message":
                            malwarePresent = 70;
                            arrivalWayDescription = "- Text messages containing download links are a common way to spread malware,\n especially when the message looks suspicious or unfamiliar.\n";
                            break;
                        case "Pop-up window from unknown website":
                            malwarePresent = 70;
                            arrivalWayDescription = "- Pop-ups on unknown or untrusted websites are often a way to spread malware.\n If the file was downloaded as a result of clicking on such a pop-up, the risk that it is infected is very high.\n";
                            break;
                        case "Downloaded automatically while browsing a web site":
                            malwarePresent = 65;
                            arrivalWayDescription = "- A file that is automatically downloaded without user permission while browsing the site can be very dangerous.\n Malicious websites may exploit browser weaknesses or secretly download files to infect your computer with malware.\n";
                            break;
                        case "URL that wasn't supposed to download any file":
                            malwarePresent = 70;
                            arrivalWayDescription = "- Clicking on an image or link that wasn't supposed to download a file, but did, is a clear sign of something wrong.\n It may be an attempt to infect the computer by exploiting an unexpected click.\n";
                            break;

                        default:
                            malwarePresent = 0;
                    }
                    System.out.println(arrivalWay);
                    System.out.println(malwarePresent);
                }
            }
    };

     // פונקציות:
    // פתיחת דוח הממצאים
    public void openPDF(String filePath) {
         try {
             File pdfFile = new File(filePath);
             if (pdfFile.exists()) {
                 if (Desktop.isDesktopSupported()) {
                     Desktop.getDesktop().open(pdfFile);
                 } else {
                     System.out.println("Desktop is not supported on this system.");
                 }
             } else {
                 System.out.println("The file does not exist.");
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

    // Get || Set
    public ArrayList<String> getSuspiciousOrders() {
        return suspiciousOrders;
    }

    public void setSuspiciousOrders(ArrayList<String> suspiciousOrders) {
        this.suspiciousOrders = suspiciousOrders;
    }

    public String getFilesStorageDestination() {
        return filesStorageDestination;
    }

    public void setFilesStorageDestination(String filesStorageDestination) {
        this.filesStorageDestination = filesStorageDestination;
    }

    public String getFindingsReportContents() {
        return findingsReportContents;
    }

    public void setFindingsReportContents(String findingsReportContents) {
        this.findingsReportContents = findingsReportContents;
    }
}



