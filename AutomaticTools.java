import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public  abstract class AutomaticTools {
    private Robot robot;
    public AutomaticTools(){
        try {
            robot = new Robot();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

        //  הזזת עכבר ולחיצה
    public void moveAndPushButton(Point destination , int delay) {
        try {
            this.robot.mouseMove(destination.x , destination.y);
            this.robot.delay(delay);
            this.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            this.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            this.robot.delay(delay);
        } catch (Exception e){
            System.out.println("Mouse moving or clicking on the button failed!");
        }
    }

        //  לקיחת טקסט והדבקתו בתיבת יעד
    public void writePathToDestinationBox (String path) {
        try {
            // העתקת הנתיב המתקבל כפרמטר ללוח
            StringSelection downloadPathSelection = new StringSelection(path);
            Clipboard downloadPathClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            downloadPathClipboard.setContents(downloadPathSelection, null);

            // הדבקת הנתיב מתיבת הלוח לתוך תיבת הטקסט
            this.robot.keyPress(KeyEvent.VK_CONTROL);
            this.robot.keyPress(KeyEvent.VK_V);
            this.robot.keyRelease(KeyEvent.VK_V);
            this.robot.keyRelease(KeyEvent.VK_CONTROL);
            this.robot.delay(1000);
            System.out.println(path);
        } catch (Exception e){
            System.out.println("Write to the destination box failed! \n please check your path again. ");
        }
    }


        //  חיפוש האם סטרינג קיים בקובץ (txt)
        public boolean searchForStringInFile(String filePath, String searchString) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(searchString)){
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        //  יצירת קובץ תוך כתיבת תוכן לתוכו
      public static void createAndWriteToFile(String filePath , String contents) {
          // יצירת אובייקט File
          File file = new File(filePath);

          // כתיבת טקסט לקובץ
          try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
              writer.write(contents);
              System.out.println("Text written to file successfully.");
          } catch (IOException e) {
              System.out.println("The create of the file or saving the file, failed!");
          }
      }

        // מחיקת קובץ
      public void deleteFile(String path) {
        File file = new File(path);
         // בדוק אם הקובץ קיים
        if (file.exists()) {
        // מחוק את הקובץ
        if (file.delete()) {
            System.out.println("הקובץ נמחק בהצלחה.");
        } else {
            System.out.println("לא ניתן היה למחוק את הקובץ.");
        }
    } else {
        System.out.println("הקובץ לא נמצא.");
    }
}


}
