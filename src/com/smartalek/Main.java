import com.jaunt.*;
import com.jaunt.component.*;
import java.io.*;
import java.lang.String;


public class Main{
    public static void main(String[] args) throws IOException{
        try{
            // FileWriter outFile = new FileWriter("output.txt");

            String courseName = "EN.600.463"; // placeholder Course
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
            Elements odd_trs = doc.findEach("<table>").findEach("<td>").findEach("<tr class=odd>");
            Elements even_trs = doc.findEach("<table>").findEach("<td>").findEach("<tr class=even>");

            for (item : odd_trs) {
                getPrereqs(getID(odd_trs));
            }


        }
        catch(JauntException e){
            System.out.println(e);
        }
    }

    private static String getID(Elements list) {
        String s = "";
        for (Element td : list) {
            String idBlock = null;
            try {
                idBlock = td.findFirst("<td class=icon-none-16-right>").innerHTML();
            } catch (NotFound notFound) {
                notFound.printStackTrace();
            }
            int place = idBlock.indexOf("blah_");
            return idBlock.substring(place+5,place+11);
        }
        return s;
    }

    private static void getPrereqs(String id) throws ResponseException, NotFound {
        UserAgent u = new UserAgent();
        String url = "https://isis.jhu.edu/classes/ClassDetails.aspx?id=" + id;
        u.visit(url);
        Document doc = u.doc;
        String courseDesc = doc.findFirst("<span id=sectionDetails_lblDescription").innerHTML();
        int start = courseDesc.indexOf("Recommended Course Background: ");
        System.out.println(courseDesc.substring(start+31));
    }
}