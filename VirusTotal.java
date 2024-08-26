import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import org.json.JSONObject;
import java.util.List;




public class VirusTotal extends AutomaticTools {
    private String antiVirusPositiveAns; // מספר מנועי האנטי וירוס שזיהו את הקובץ כוירוס
    private String sumOfAntiVirus; // מספר מנועי האנטי וירוס שאינם זיהו את הקובץ כוירוס
    private String description; // תיאור ממצאים
    private File file; // הקובץ הנבדק
    private final String API_KEY = "8e14d9c052dbb2045564ff002a3843f6fac3bbe4d16a98c18dc79636ec147cea"; // מפתח הAPI
    private String fileHash; // המזהה שאתר וירוס טוטל נותן לקובץ
    private String fileReport; // משתנה עזר - דוח הממצאים שמתקבל מהאתר
    private JSONObject jsonResponse; // דוח הממצאים שמתקבל מהאתר - בתצורת JSON
    private int totalAntiViruses; // כמות האנטי וירוס שהקובץ נבדק מולם
    private int present; // אחוזי המצאות נוזקה בקובץ ע"פ ממצאי וירוס טוטל בלבד

    // מערך שמות הקטגוריות של הנוזקות
    private static final String[] malwareCategories = {
            "Trojan", "Worm", "Virus", "Adware", "Spyware", "Ransomware",
            "Backdoor", "Rootkit", "Downloader", "Exploit", "Heuristic", "PUA/PUP",
            "Riskware", "Dialer", "Hoax", "Tool", "Hacktool", "Keylogger", "Phishing",
            "Bot", "Rogue", "Trojan-Spy", "Trojan-Dropper", "Trojan-Downloader",
            "Trojan-Ransom", "Trojan-Banker", "Trojan-Backdoor", "Trojan-Clicker",
            "Trojan-Proxy", "Trojan-GameThief", "Trojan-SMS", "Trojan-DDoS",
            "Trojan-RAT", "Trojan-Crypt", "Trojan-FakeAV"
    };

    // מפת תיאורי הקטגוריות
    private static final Map<String, String> categoryDescriptions = new HashMap<>();
    private Map<String, Integer> malwareMap = new HashMap<>();


    public VirusTotal (String filePath) {
    antiVirusPositiveAns = "";
    sumOfAntiVirus = "";
    description = "";
    fileReport = "";
    present = 0;
    totalAntiViruses = 0;
    jsonResponse = null;
    this.file = new File(filePath);

        // הוספה למפה את שם הנוזקה ופעולה
        categoryDescriptions.put("Trojan", "Malicious program that pretends to be legitimate software.");
        categoryDescriptions.put("Worm", "Self-replicating malware that spreads across networks.");
        categoryDescriptions.put("Virus", "Malware that attaches itself to legitimate programs and spreads.");
        categoryDescriptions.put("Adware", "Software that displays unwanted ads.");
        categoryDescriptions.put("Spyware", "Software that spies on user activities.");
        categoryDescriptions.put("Ransomware", "Malware that locks files and demands payment.");
        categoryDescriptions.put("Backdoor", "Hidden method to bypass authentication.");
        categoryDescriptions.put("Rootkit", "Malware designed to hide its presence.");
        categoryDescriptions.put("Downloader", "Malware that downloads other malicious files.");
        categoryDescriptions.put("Exploit", "Code that takes advantage of software vulnerabilities.");
        categoryDescriptions.put("Heuristic", "Detection method that identifies suspicious behavior.");
        categoryDescriptions.put("PUA/PUP", "Potentially Unwanted Applications or Programs.");
        categoryDescriptions.put("Riskware", "Legitimate software that poses a security risk.");
        categoryDescriptions.put("Dialer", "Software that makes unauthorized phone calls.");
        categoryDescriptions.put("Hoax", "False information spread as a prank.");
        categoryDescriptions.put("Tool", "Software used to perform specific tasks, sometimes malicious.");
        categoryDescriptions.put("Hacktool", "Tools used to exploit or hack systems.");
        categoryDescriptions.put("Keylogger", "Software that records keystrokes.");
        categoryDescriptions.put("Phishing", "Fraudulent attempt to obtain sensitive information.");
        categoryDescriptions.put("Bot", "Malware that turns a device into a bot for a botnet.");
        categoryDescriptions.put("Rogue", "Fake security software that tricks users.");
        categoryDescriptions.put("Trojan-Spy", "Trojan designed to spy on users.");
        categoryDescriptions.put("Trojan-Dropper", "Trojan that drops other malicious files.");
        categoryDescriptions.put("Trojan-Downloader", "Trojan that downloads other malware.");
        categoryDescriptions.put("Trojan-Ransom", "Trojan that demands ransom.");
        categoryDescriptions.put("Trojan-Banker", "Trojan targeting banking information.");
        categoryDescriptions.put("Trojan-Backdoor", "Trojan that installs a backdoor.");
        categoryDescriptions.put("Trojan-Clicker", "Trojan that clicks on ads or other content.");
        categoryDescriptions.put("Trojan-Proxy", "Trojan that turns a device into a proxy.");
        categoryDescriptions.put("Trojan-GameThief", "Trojan targeting online game accounts.");
        categoryDescriptions.put("Trojan-SMS", "Trojan that sends unauthorized SMS messages.");
        categoryDescriptions.put("Trojan-DDoS", "Trojan used to perform DDoS attacks.");
        categoryDescriptions.put("Trojan-RAT", "Remote Access Trojan for unauthorized control.");
        categoryDescriptions.put("Trojan-Crypt", "Trojan that encrypts files.");
        categoryDescriptions.put("Trojan-FakeAV", "Fake antivirus Trojan.");
    try {
        this.startProgram();
    } catch (Exception virusTotalEx){
        System.out.println("There is a problem with virus total Program");
    }
    }

    // פונקציות

    // הפעלת כלל הפונקציות של התוכנית
    public void startProgram(){
        this.getFileHash();
        try {
            this.getFileReport();
        } catch (Exception e){
            e.printStackTrace();
        }
        this.updateDescription();
        this.getSha256_Signature();
        System.out.println("\n ********************************************* \n"+this.description);
    }

    // שליחת בקשת API לקבלת ID לקובץ
    public void getFileHash(){
        // נתיב לקובץ שאתה רוצה לסרוק
        String apiUrl = "https://www.virustotal.com/api/v3/files";

        //  חיבור הHTTP
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //  אובייקט שמייצג את בקשת ה-POST
            HttpPost uploadFile = new HttpPost(apiUrl);
            //  הוספת מפתח ה-API לכותרת
            uploadFile.setHeader("x-apikey", API_KEY);

            // בניית הבקשה עם MultipartEntityBuilder כדי לשלוח את הקובץ בצורה נכונה
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());

            // הוספת תוכן הקובץ לבקשת ה-POST
            uploadFile.setEntity(builder.build());

            // שליחת הבקשה ל-VirusTotal
            try (CloseableHttpResponse response = httpClient.execute(uploadFile)){

                // המרת תגובת ה-HTTP למחרוזת
                String result = EntityUtils.toString(response.getEntity());

                System.out.println("Response JSON: " + result);

                //תשובת בקשת הAPI בתצורת (JSON)
                JSONObject jsonResponse = new JSONObject(result);

                // גישה לאובייקט data
                JSONObject dataObject = jsonResponse.getJSONObject("data");

                // שליפת הערכים מתוך האובייקט
                String id = dataObject.getString("id");
                this.fileHash = id; // השמה בid

            } catch (Exception e) {
                throw new RuntimeException("Error parsing JSON response", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //  שליחת בקשת API לדוח הקובץ ע"י ID של הקובץ
    public void getFileReport () throws IOException {
        // נתיב ל-API לקבלת דוח על הקובץ
        String apiUrl = "https://www.virustotal.com/api/v3/analyses/" + fileHash;
        System.out.println("-----------------------------------------------------------");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            request.setHeader("x-apikey", API_KEY);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String result = EntityUtils.toString(response.getEntity());
                this.fileReport = result;
            //     השמת התוצאה בJSON התשובה
                this.jsonResponse = new JSONObject(result);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // הדפסת ה-JSON שהתקבל
            System.out.println("Response JSON: " + jsonResponse.toString(2));
        }
    }



    //  עדכון הממצאים המחלקה ע"פ Json התוצאה
    public void updateDescription () {
        // תיאור כמות האנטי וירוסים שזיהו את הקובץ כנוזקה
        try {
            // גישה לנתונים במבנה JSON
            JSONObject dataObject = jsonResponse.getJSONObject("data");
            JSONObject attributesObject = dataObject.getJSONObject("attributes");
            JSONObject statsObject = attributesObject.getJSONObject("stats");

            // שליפת הערכים
            int malicious = statsObject.getInt("malicious");
            int undetected = statsObject.getInt("undetected");
            totalAntiViruses = malicious + undetected;
            float percentage = (float) malicious / totalAntiViruses;

            //  עידכון האחוזים לנוזקה ע"פ יחס מנועי האנטי וירוס שזיהו אותה כנוזקה לעומת אילו שלא
            if (totalAntiViruses > 0) {
                // בין 30 ל60 אחוז מהמנועים זיהו כוירוס
                if (percentage >= 0.3 && percentage < 0.6) {
                    this.present = 5;
                }
                // בין 60 ל 80 אחוז מהמנועים זיהו כוירוס
                else if (percentage >= 0.6 && percentage < 0.8) {
                    this.present = 15;
                }
                // בין 80 ל100 אחוז מהמנועים זיהו כוירוס
                else if (percentage >= 0.8 && percentage < 1) {
                    this.present = 20;
                }
            }

//            // שליחת בקשה לדוח נוזקה פעמיים כי לפעמים הקשה לא עוברת בפעם הראשונה
//            else {
//                this.getFileReport();
//            }

            System.out.println("------------------present = "+ present +" ----------------");

            // בניית התיאור
            this.description = "From: " + totalAntiViruses + " Anti-Virus tools. Your file has been identified as malware by " + malicious + " Anti-Viruses!\n";
            System.out.println("\n" + description);
            this.processReport();

        } catch (org.json.JSONException e) {
            // טיפול בשגיאות קריאה מה-JSON
            System.err.println("Error parsing JSON response: " + e.getMessage());
        }
        System.out.println("\n======================================\n" + this.description);
    }

    //  קבלת שלושת קטוגוריות הנוזקה הפוצים ביותר לקובץ
    public  void processReport() {

        // שליפת הנתונים שבוקדים האם הקובץ הוגדר כנוזקה אל מול כל מנוע אנטי וירוס בדוח
        JSONObject dataObject = jsonResponse.getJSONObject("data");
        JSONObject attributesObject = dataObject.getJSONObject("attributes");
        JSONObject resultsObject = attributesObject.getJSONObject("results");

        // בדיקה האם הקובץ הוגדר כנוזקה
        for (String key : resultsObject.keySet()) {
            JSONObject engineResult = resultsObject.getJSONObject(key);
            String category = engineResult.getString("category");

            // אם כן קבל את תיאור הנוזקה
            if ("malicious".equalsIgnoreCase(category)) {
                String result = engineResult.getString("result");

                // התאמה חלקית: הוספת התוצאה למפה
                malwareMap.put(result, malwareMap.getOrDefault(result, 0) + 1);
            }
        }

        // הדפסת התוצאות
        System.out.println("Result counts: " + malwareMap);

        Map<String, Integer> categoryCount = new HashMap<>();

        // ספירת ההופעות של כל קטגוריה במפת המאלוור
        for (String category : malwareCategories) {
            for (String key : malwareMap.keySet()) {
                if (key.contains(category)) {
                    categoryCount.put(category, categoryCount.getOrDefault(category, 0) + malwareMap.get(key));
                }
            }
        }

        // מציאת שלושת הקטגוריות הנפוצות ביותר
        List<String> topCategories = categoryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // בניית תיאור של שלושת הקטגוריות הנפוצות ביותר
        this.description += "According to most antivirus engines, your file has been detected as one of the following types of malware:\n";
        for (String category : topCategories) {
            this.description += " -> " + category + " : " + categoryDescriptions.get(category) +"\n";
        }
    }

    // קבלת חתימת הקובץ בפורמט Sha256
    public void getSha256_Signature () {

        // גישה לנתונים במבנה JSON
        JSONObject metaObject = jsonResponse.getJSONObject("meta");
        JSONObject fileInfoObject = metaObject.getJSONObject("file_info");

        // שליפת ערך החתימה של הקובץ
        String shaSignature = fileInfoObject.getString("sha256");

        //  עידכון החתימה בממצאי הדוח
        this.description += "The file signature (in SHA-256 format) is: " + shaSignature + " .\n";
    }

    // GET


    public String getDescription() {
        return description;
    }

    public int getPresent() {
        return present;
    }
}


