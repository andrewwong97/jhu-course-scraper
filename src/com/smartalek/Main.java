import com.jaunt.*;
import com.jaunt.component.*;
import java.io.*;


public class Main{
    public static void main(String[] args) throws IOException{
        try{
            FileWriter outFile = new FileWriter("output.txt");

            String courseName = "EN.600.226";
            UserAgent userAgent = new UserAgent();
            userAgent.visit("https://isis.jhu.edu/classes/");
            Document doc = userAgent.doc;

            Form form = doc.getForm(0);
            form.setCheckBox("ctl00$content$ucSchoolList$rptSchools$ctl03$cbSchool", true); // KSAS
            form.setCheckBox("ctl00$content$ucSchoolList$rptSchools$ctl11$cbSchool", true); // WSE
            form.setTextField("ctl00$content$txtCourseNumber", courseName);
            form.setSelect("ctl00$content$lbLevel", "Lower Level Undergraduate"); // Lower Level Undergraduate
            form.setSelect("ctl00$content$lbLevel", "Upper Level Undergraduate"); // Upper Level Undergraduate
            form.setSelect("ctl00$content$lbTerms", "Spring 2016"); // Current Semester
            doc.submit("Search");

            doc = userAgent.doc; // 2nd page
            form = doc.getForm(0);
            Elements tables = doc.findEach("<table>");       //find non-nested tables
            for(Element table : tables) {
                System.out.println(table.outerHTML() + "\n----\n");      //print each element and its contents
                outFile.write(table.outerHTML() + "\n----\n");
            }
            outFile.close();
        }
        catch(JauntException e){
            System.out.println(e);
        }
    }
}