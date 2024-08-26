import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Pied extends AutomaticTools {

    private final int X_CHOOSE_FILE_BUTTON_COORDINATES = 828;
    private final int Y_CHOOSE_FILE_BUTTON_COORDINATES = 263;
    private final int X_OPEN_BUTTON_COORDINATES = 903;
    private final int Y_OPEN_BUTTON_COORDINATES = 616;
    private final int X_TYPE_LABEL_START_COORDINATES = 733;
    private final int Y_TYPE_LABEL_START_COORDINATES = 303;
    private final int X_TYPE_LABEL_END_COORDINATES = 783;
    private final int Y_TYPE_LABEL_END_COORDINATES = 305;
    private final int X_CLOSE_PROGRAM_BUTTON_COORDINATES = 823;
    private final int Y_CLOSE_PROGRAM_BUTTON_COORDINATES = 233;
    private int SHORT_DELAY_TIME = 500;
    private int LONG_DELAY_TIME = 1000;
    private Point chooseFaileButtonCoordinates = new Point(X_CHOOSE_FILE_BUTTON_COORDINATES , Y_CHOOSE_FILE_BUTTON_COORDINATES);
    private Point openButtonCoordinates = new Point(X_OPEN_BUTTON_COORDINATES , Y_OPEN_BUTTON_COORDINATES);
    private Point typeLabelCoordinates = new Point(X_TYPE_LABEL_START_COORDINATES, Y_TYPE_LABEL_START_COORDINATES);
    private Point closeProgramButtonCoordinates = new Point(X_CLOSE_PROGRAM_BUTTON_COORDINATES,Y_CLOSE_PROGRAM_BUTTON_COORDINATES);

    private String filePath; // נתיב הקובץ הנבדק
    private String type; // סוג הקובץ
    private boolean hasUpxEncoded; // האם קיימת הצפנת UPX
    private Robot robot;
    private String description =""; // תיאור ממצאים

    public Pied(String filePath){
        this.filePath = filePath;
        try {
            this.robot = new Robot();
            this.fileType();
            System.out.println("type is now: " + type);
        } catch (Exception e) {
            System.out.println("Something wrong with the automation procedure");
        }
        // סגירת התוכנית
        this.moveAndPushButton(closeProgramButtonCoordinates, LONG_DELAY_TIME);
    }

    // בדיקת סוג הקובץ
    public void fileType() {
        try {
            // פתיחת התוכנית PiED
            Runtime.getRuntime().exec("C:\\Users\\ariel\\Desktop\\שנה ג\\סייבר רפאל- גיא\\PiED\\PEiD.exe");
        } catch (Exception e) {
            System.out.println("Running the PiED feature failed!");
        }
        // הזנת נתיב הקובץ הנבדק
        this.moveAndPushButton(this.chooseFaileButtonCoordinates, LONG_DELAY_TIME);
        this.writePathToDestinationBox(this.filePath);
        this.moveAndPushButton(this.openButtonCoordinates, LONG_DELAY_TIME);
        // הזזת העכבר לתיאור סוג הטקסט
        this.moveAndPushButton(typeLabelCoordinates , SHORT_DELAY_TIME);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        // סימון (השחרה) טקסט סוג הקובץ
        robot.mouseMove(X_TYPE_LABEL_END_COORDINATES, Y_TYPE_LABEL_END_COORDINATES);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        // העתקה של התוכן שמופיע בסוג הטקסט
        this.robot.keyPress(KeyEvent.VK_CONTROL);
        this.robot.keyPress(KeyEvent.VK_C);
        this.robot.keyRelease(KeyEvent.VK_C);
        this.robot.keyRelease(KeyEvent.VK_CONTROL);

        try {
            // להמתין מעט יותר כדי לוודא שהלוח מעודכן
            Thread.sleep(1500);

            // קבלת תוכן הלוח
            this.type = getClipboardContents();

            // בדיקה שהטקסט שהתקבל הוא אכן התוכן המצופה
            if (type == null || type.isEmpty()) {
                System.out.println("Failed to retrieve the expected text from the clipboard.");
                return;
            }
            System.out.println("type is now: " + type);

            // בדיקה האם סוג הקובץ הוא UPX
            if (this.type.contains("UPX")) {
                this.hasUpxEncoded = true;
                // דריסה ועידכון הממצאים בהתאם
                this.description = "- Your file is encrypted with UPX encryption.\n" +
                        "Encrypted files raise the level of suspicion because many attackers use encryption to make it difficult to analyze the file.\n";
            }

        } catch (Exception e) {
            System.out.println("Something went wrong during the automation procedure");
        }
        System.out.println(hasUpxEncoded);
    }

    private String getClipboardContents() {
        try {
            // הדבקת התוכן השמור
            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) t.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Get methods
    public boolean isHasUpxEncoded() {
        return hasUpxEncoded;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }
}
