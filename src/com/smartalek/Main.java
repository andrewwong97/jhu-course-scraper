import com.jaunt.*;
import com.jaunt.component.*;
import java.io.*;
import java.lang.String;
import java.lang.reflect.Array;
import java.util.ArrayList;


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
            form.setSelect("ctl00$content$lbLevel", "Lower Level Undergraduate");
            form.setSelect("ctl00$content$lbLevel", "Upper Level Undergraduate");
            form.setSelect("ctl00$content$lbTerms", "Spring 2016");
            doc.submit("Search");

            doc = userAgent.doc; // 2nd page
            Elements odd_trs = doc.findEach("<table>").findEach("<td>").findEach("<tr class=odd>");
            Elements even_trs = doc.findEach("<table>").findEach("<td>").findEach("<tr class=even>");

            ArrayList<String> fullData = getID(odd_trs);
            fullData.addAll(getID(even_trs));
            
            for (int i = 0;i<fullData.size();i++) {
                System.out.print(courseName + ": ");
                getPrereqs(fullData.get(i));
            }

        }
        catch(JauntException e){
            System.out.println(e);
        }
    }

    /**
     * @param list list of IDs
     * @return array of IDs
     */
    private static ArrayList getID(Elements list) {
        ArrayList<String> IDs = new ArrayList<String>();
        for (Element td : list) {
            String idBlock = null;
            try {
                idBlock = td.findFirst("<td class=icon-none-16-right>").innerHTML();
            } catch (NotFound e) {
                e.printStackTrace();
            }
            int place = idBlock.indexOf("blah_");
            IDs.add(idBlock.substring(place+5,place+11));
        }
        return IDs;
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