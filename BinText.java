import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException;


//   הרצת תוכנת binText לאיתור פקודות חשודות
public class BinText extends AutomaticTools {

    //  ערכי הקורדינטות של כפתורי התוכנית.
    //  שים לב: ערכים אלו נכונים לרזולוציה של 1280X720
    private final int X_BROWSE_BUTTON_COORDINATES = 950;
    private final int Y_BROWSE_BUTTON_COORDINATES = 261;
    private final int X_OPEN_BUTTON_COORDINATES = 838;
    private final int Y_OPEN_BUTTON_COORDINATES = 643;
    private final int X_GO_BUTTON_COORDINATES = 1045;
    private final int Y_GO_BUTTON_COORDINATES = 260;
    private final int X_SIMPLIFY_TO_ONE_STRING_BUTTON_COORDINATES = 435;
    private final int Y_SIMPLIFY_TO_ONE_STRING_BUTTON_COORDINATES = 302;
    private final int X_SAVE_BUTTON_COORDINATES = 1069;
    private final int Y_SAVE_BUTTON_COORDINATES = 612;
    private final int X_SAVE_AS_A_FILE_BUTTON_COORDINATES = 838;
    private final int Y_SAVE_AS_A_FILE_BUTTON_COORDINATES = 643;
    private final int X_CLOSE_PROGRAM_BUTTON_COORDINATES = 1100;
    private final int Y_CLOSE_PROGRAM_BUTTON_COORDINATES = 177;

        //  זמני ההמתנה שהפעולה תתבצע לפני הרצת הפעולה הבאה
    private int SHORT_DELAY_TIME = 500;
    private int LONG_DELAY_TIME = 1000;



    // יצירת נקודות לפי ערכי הx והy
    private Point browseButtonCoordinates = new Point(X_BROWSE_BUTTON_COORDINATES,Y_BROWSE_BUTTON_COORDINATES);
    private Point openButtonCoordinates = new Point(X_OPEN_BUTTON_COORDINATES,Y_OPEN_BUTTON_COORDINATES);
    private Point goButtonCoordinates = new Point(X_GO_BUTTON_COORDINATES,Y_GO_BUTTON_COORDINATES);
    private Point simplifyToOneStringButtonCoordinates = new Point(X_SIMPLIFY_TO_ONE_STRING_BUTTON_COORDINATES,Y_SIMPLIFY_TO_ONE_STRING_BUTTON_COORDINATES);
    private Point saveButtonCoordinates = new Point(X_SAVE_BUTTON_COORDINATES,Y_SAVE_BUTTON_COORDINATES);
    private Point saveAsFileButtonCoordinates = new Point(X_SAVE_AS_A_FILE_BUTTON_COORDINATES,Y_SAVE_AS_A_FILE_BUTTON_COORDINATES);
    private Point closeProgramButtonCoordinates = new Point(X_CLOSE_PROGRAM_BUTTON_COORDINATES,Y_CLOSE_PROGRAM_BUTTON_COORDINATES);

    //  שדות:
    //  הפקודות שיגרמו לנו לחשוד בקובץ
    private ArrayList<String> suspiciousOrders;

    // מערך תווים מיוחדים
    char [] specialSymbols = {
            '!', '"', '#', '$', '%', '&', '(', ')', '*', '+', ',', '-', '.', '/',

            // סימני הפסקה וההדגשה
            ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~',

            //תווים לא נראים / תווי שליטה
            '\n', '\r', '\t',

            // תווים מיוחדים נוספים
            '©', '®', '™',

            // סימנים מתמטיים
            '±', '÷', '×',

            // סימנים מתקדמים
            '←', '→', '↑', '↓',

            // סימנים מיוחדים אחרים
            '€', '£', '¥'
    };
    private String filePath; // נתיב הקובץ הנבדק
    private String destinationFilesPath; // נתיב כללי מהמשתמש לאיחסון הקבצים
    private String descriptionOrder; // תיאור ממצאים
    private String orderFailPath; // נתיב קובץ העזר שמכיל את פקודות הקובץ
    private int present;  // האחוזים לנוזקה ע"פ ממצאי BinText
    private boolean isSuspiciousOrders; // האם יש פקודות חשודות
    private boolean isStrongEncoded; // האם הקובץ מוצפן - בסיכוי גבוה (50% תוים מיוחדים)
    private boolean isEncoded; // האם הקובץ מוצפן - הערכה בינונית (20% תוים מיוחדים)


    // constructor
    public BinText(String filePath, String destinationFile, ArrayList<String> suspiciousOrders){
        this.filePath = filePath;
        this.destinationFilesPath = destinationFile;
        this.suspiciousOrders = suspiciousOrders;
        this.present = 0;
        this.isSuspiciousOrders = false;
        this.isStrongEncoded = false;
        this.isEncoded = false;

        // הוספת הפקודות המוגדרות כחשודות

        this.suspiciousOrders.add("CreateProcess");
        this.suspiciousOrders.add("TerminateProcess");
        this.suspiciousOrders.add("Kernel32.dll");
        this.suspiciousOrders.add("C:\\Windows\\System32\\Kernel32.dll");
        this.suspiciousOrders.add("VirtualAlloc");
        this.suspiciousOrders.add("NtAllocateVirtualMemory");
        this.suspiciousOrders.add("VirtualProtect");
        this.suspiciousOrders.add("GetComputerName");
        this.suspiciousOrders.add("GetUserName");
        this.suspiciousOrders.add("GetSystemInfo");
        this.suspiciousOrders.add("GlobalMemoryStatusEx");
        this.suspiciousOrders.add("GetDC");
        this.suspiciousOrders.add("GetDeviceCaps");
        this.suspiciousOrders.add("BitBlt");
        this.suspiciousOrders.add("GetPixel");
        this.suspiciousOrders.add("Sleep");
        this.suspiciousOrders.add("GetTickCount");
        this.suspiciousOrders.add("RDTSC");
        this.suspiciousOrders.add("NtDelayExecution");
        this.suspiciousOrders.add("CPUID");
        this.suspiciousOrders.add("Rdtsc");
        this.suspiciousOrders.add("GetSystemInfo");
        this.suspiciousOrders.add("CreateFile");
        this.suspiciousOrders.add("WriteFile");
        this.suspiciousOrders.add("ShellExecute");
        this.suspiciousOrders.add("WinExec");
        this.suspiciousOrders.add("FILE_ATTRIBUTE_HIDDEN");
        this.suspiciousOrders.add("IShellLink");
        this.suspiciousOrders.add("CreateLink");
        this.suspiciousOrders.add("ShellExecute");
        this.suspiciousOrders.add("EnumProcesses");
        this.suspiciousOrders.add("GetModuleFileName");
        this.suspiciousOrders.add("QueryFullProcessImageName");
        this.suspiciousOrders.add("WMI");
        this.suspiciousOrders.add("SetWindowsHookEx");
        this.suspiciousOrders.add("GetAsyncKeyState");
        this.suspiciousOrders.add("GetKeyState");
        this.suspiciousOrders.add("CryptEncrypt");
        this.suspiciousOrders.add("MoveFile");
        this.suspiciousOrders.add("CopyFile");

        try {
            this.runBinTextProgram();
            this.orderFailPath = this.destinationFilesPath + "file_orders.txt";
            this.searchSuspiciousOrders();
        } catch (Exception binTextEx){
            System.out.println("There is a problem with BinText Program");
        }
    }



    // הפונקציה האחראית להרצת תוכנת  binText , לקיחת הפקודות שקיימות בקובץ והשוואתן למערך פקודות חשודות
    public  void runBinTextProgram() {
        try {  // פתיחת תוכנת binText
            Runtime.getRuntime().exec("C:\\Users\\ariel\\Desktop\\שנה ג\\סייבר רפאל- גיא\\Bintext\\bintext");
        } catch (Exception e) {
            System.out.println("Running the Bin Text feature failed!");
        }
        // לחיצה על כפתור browse
        this.moveAndPushButton(this.browseButtonCoordinates, SHORT_DELAY_TIME);

        // העתקת הנתיב הקובץ המתקבל כפרמטר ללוח
        this.writePathToDestinationBox(this.filePath);

        //  לחיצה על open - הזנת הנתיב המבוקש בשורת החיפוש בתוכנה binText
        this.moveAndPushButton(this.openButtonCoordinates, LONG_DELAY_TIME);

        // לחיצה על הרצה (go)
        this.moveAndPushButton(this.goButtonCoordinates, LONG_DELAY_TIME);

        // פישוט הפקודות למחרוזת
        this.moveAndPushButton(simplifyToOneStringButtonCoordinates, SHORT_DELAY_TIME);

        // שמירת הפקודות כקובץ txt
        this.moveAndPushButton(saveButtonCoordinates, LONG_DELAY_TIME);

        // העתקת הנתיב המתקבל כפרמטר ללוח
        this.writePathToDestinationBox(this.destinationFilesPath + "file_orders.txt");

        //  קורדינטות הכפתור save(בתיבת השמירה)
        this.moveAndPushButton(saveAsFileButtonCoordinates, LONG_DELAY_TIME);
        this.orderFailPath = this.destinationFilesPath + "file_orders.txt";

        // סגירת התוכנית BinText
        this.moveAndPushButton(closeProgramButtonCoordinates, SHORT_DELAY_TIME);
    }


    // בדיקת מערך הפקודות החשודות אל מול קובץ הפקודות שנשמר
    public boolean searchSuspiciousOrders() {
            boolean thereIsSuspiciousOrder = false;
        this.descriptionOrder = "- We didnt find any suspicious order in your file\n" ;
            for (int i=0; i<suspiciousOrders.size(); i++) {
                try (BufferedReader reader = new BufferedReader(new FileReader(orderFailPath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        if (line.contains(suspiciousOrders.get(i))) {
                            thereIsSuspiciousOrder = true;
                            this.descriptionOrder = "There is a suspicious order in your file! \n";

                            // עידכון הממצאים בהתאם לסוג הפקודה

                            switch (suspiciousOrders.get(i)) {
                                case "CreateProcess" :
                                    this.descriptionOrder += " - There is a command that runs files from the operating system was detected in the file. This action is not common." + "\n Please examine the file you have. Does this command match the file you have? \n";
                                    this.present += 8;

                                case "Kernel32.dll":
                                    this.descriptionOrder += " - There is a suspicious command was detected in the file. The command attempts to access the operating system \n";
                                    this.present += 10;

                                case "C:\\Windows\\System32\\Kernel32.dll":
                                    this.descriptionOrder += " - There is a suspicious command was detected in the file. someone try to access the operating system from Administrator mode. That means there is an attempt by the program to raise access privileges to your system! \n";
                                    this.present += 20;

                                case "VirtualProtect" :
                                case "NtAllocateVirtualMemory" :
                                case "VirtualAlloc" :
                                    this.descriptionOrder += " - There is a suspicious command used to allocate memory in the system and to set read, write, and run permissions for the allocated memory.\n" +
                                            "  Malicious actors may use this type of memory allocation to load malicious code into memory and run it directly from memory (a process known as \"DLL Injection\" or \"Code Injection\").";
                                    this.present += 20;

                                case "GetSystemInfo" :
                                case "GetUserName" :
                                case "GetComputerName" :
                                    this.descriptionOrder += " - There is a suspicious command used to collect information about the computer name, user name and system properties.\n" +
                                            " Malware may check the computer or user name to identify whether they are running in a lab environment or virtual machine used for malware analysis.";
                                    this.present += 5;

                                case "GlobalMemoryStatusEx" :
                                    this.descriptionOrder += " - There is a suspicious command used to obtain information about the amount of memory in the system and the characteristics of the processor.\n" +
                                            " Malicious agents use these functions to check whether they are running in an environment with limited resources,\n a feature that characterizes virtual machines or analysis environments.";
                                    this.present += 5;

                                case "TerminateProcess" :
                                    this.descriptionOrder += " - There is a suspicious command crash Processes in your file. that crash unexpectedly may indicate that the attacker is trying to take over them or cause them to perform malicious actions.";
                                    this.present += 15;

                                case "GetDC" :
                                case "GetDeviceCaps" :
                                case "BitBlt" :
                                case "GetPixel" :
                                    this.descriptionOrder += " - There is a suspicious command used to get data about the screen display and its pixels.\n" +
                                            "Vulnerable users use the screen test to identify if they are running in an analysis environment,\n such as a sandbox, that does not change the screen very often.";
                                    this.present += 5;

                                case "Sleep" :
                                case "GetTickCount" :
                                case "RDTSC" :
                                case "NtDelayExecution" :
                                    this.descriptionOrder += " - There is a suspicious command used to delay the running of the damaged.\n" +
                                            "Victims use these delays to evade detection during sandbox tests, which tend to run for a short time.";
                                    this.present += 10;

                                case "CPUID" :
                                case "Rdtsc" :
                                    this.descriptionOrder += " - There is a suspicious command used to read information about the processor and physical memory.\n" +
                                            "Malware may use these tests to detect whether they are running on a VM,\n based on CPU and memory characteristics.";
                                    this.present += 5;

                                case "CreateFile" :
                                case "WriteFile" :
                                case "ShellExecute" :
                                case "WinExec" :
                                    this.descriptionOrder += " - There is a suspicious command used to create new files in the system and activate them.\n" +
                                            "Malware creates executable files to run additional malicious code or replicate themselves on the system.";
                                    this.present += 5;

                                case "FILE_ATTRIBUTE_HIDDEN" :
                                    this.descriptionOrder += " - There is a suspicious command used to create files with hidden property.\n" +
                                            "Malware uses hidden files to hide their activity from the user and the antivirus.";
                                    this.present += 15;

                                case "CreateLink" :
                                case "IShellLink" :
                                    this.descriptionOrder += " - There is a suspicious command used to create shortcuts on the desktop or in other locations.\n" +
                                            "Malware uses shortcuts to launch themselves or redirect users to malicious files.";
                                    this.present += 5;

                                case "EnumProcesses" :
                                case "GetModuleFileName" :
                                case "QueryFullProcessImageName" :
                                case "WMI" :
                                    this.descriptionOrder += " - There is a suspicious command used to collect information about the processes and applications running in the system.\n" +
                                            "Harmful users may check the list of applications to discover antivirus or other tools that may threaten their activity.";
                                    this.present += 10;

                                case "SetWindowsHookEx" :
                                case "GetAsyncKeyState" :
                                case "GetKeyState" :
                                    this.descriptionOrder += " - There is a suspicious command used to track keystrokes.\n" +
                                            "Hackers use keyboard hooks to steal passwords, credit card information, and other personal information.";
                                    this.present += 35;

                                case "CryptEncrypt" :
                                case "MoveFile" :
                                case "CopyFile" :
                                    this.descriptionOrder += " - There is a suspicious command used to encrypt files or move them to another location.\n" +
                                            "Ransomware uses these commands to encrypt the victim's files and demand a ransom in exchange for their release.";
                                    this.present += 90;

                            }
                            System.out.println("\n there is a suspicious order in your file");
                            System.out.println(this.descriptionOrder);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            this.isSuspiciousOrders = thereIsSuspiciousOrder;
            return thereIsSuspiciousOrder;
            }


            // הערכה האם הקובץ מוצפן
    public void isTheFileEncoded() {
        int specialSymbolCounter = 0;
        boolean isEncoded = false;
        int fileCharSum = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(orderFailPath))) {
            int character;
            while ((character = reader.read()) != -1) { // בדיקת כל תו מהקובץ
                fileCharSum++;
                for (int i = 0; i < specialSymbols.length; i++) {
                    if (character == specialSymbols[i]) {
                        specialSymbolCounter++;
                        break;
                    }
                }
            }
            if (specialSymbolCounter >= fileCharSum/2){ // 50% מהתויים הם מיוחדים
                isStrongEncoded = true;
                this.descriptionOrder += "• Over 50% special characters: Your file is probably encrypted.\n" +
                        "Encrypted files raise the level of suspicion because many attackers use encryption to make it difficult to analyze the file.\n";

                System.out.println("strong indication to encoded file!");
            } else if (specialSymbolCounter >= fileCharSum/5){ // 20% מהתויים הם מיוחדים
                isEncoded = true;

                this.descriptionOrder += "• Over 20% special characters: the file may be encrypted.\n"+
                "Encrypted files raise the level of suspicion because many attackers use encryption to make it difficult to analyze the file.\n";
                System.out.println("medium indication to encoded file!");
            }
            else {
                this.descriptionOrder += " \n - We haven't found any other indication of encryption in your file\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("specialSymbolCounter: " + specialSymbolCounter + "/" + "fileCharSum: " + fileCharSum);
    }

    //  מחיקת קובץ העזר שמכיל את פקודות הקובץ
    public void deleteOrderFail() {
        this.deleteFile(orderFailPath);
    }



    //  get || set

    // למערך הפקודות החשודות
    public ArrayList<String> getSuspiciousOrders(){
        return suspiciousOrders;
    }
    public void setSuspiciousOrders(String suspiciousOrder) {
        this.suspiciousOrders.add(suspiciousOrder);
    }

    //  לתיאור פעולת הפקודה החשודה
    public String getDescriptionOrder() {
        return descriptionOrder;
    }
    public void setDescriptionOrder(String descriptionOrder) {
        this.descriptionOrder = descriptionOrder;
    }

    public boolean isSuspiciousOrders() {
        return isSuspiciousOrders;
    }

    public int getPresent() {
        return present;
    }

    public boolean isStrongEncoded() {
        return isStrongEncoded;
    }

    public boolean isEncoded() {
        return isEncoded;
    }

    public void delete(String filePath){
        this.deleteFile(filePath);
    }

}

