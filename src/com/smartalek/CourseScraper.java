package com.smartalek;

import com.jaunt.*;
import com.jaunt.component.*;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;


public class CourseScraper{

    public static void main(String[] args) {
        createCourseDB("EN.600.463");
    }

    /**
     * Creates a map of each course and its prerequisite courses
     * @param courseName e.g. EN.600.463
     */
    private static void createCourseDB(String courseName) {
        try{
            Map<String,ArrayList<String>> MainMap = new HashMap();

            ArrayList<String> allIDs = retrieveIDs(courseName);


            for (int i = 0;i<allIDs.size();i++) {
                MainMap.put(courseName,getPrereqs(allIDs.get(i)));
            }
            System.out.println(MainMap.get(courseName));

        }
        catch(JauntException e){
            System.out.println(e);
        }
    }

    /**
     * @param courseName
     * @return get list of course object IDs for the course specified
     */
    private static ArrayList retrieveIDs(String courseName) throws ResponseException, SearchException {
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

        ArrayList<String> allIDs = getIdNums(odd_trs);
        allIDs.addAll(getIdNums(even_trs));
        return allIDs;
    }

    /**
     * Helper method for retrieveIDs
     * @param list list of unstructured table rows
     * @return array of IDs
     */
    private static ArrayList getIdNums(Elements list) {
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

    /**
     * @param id the object ID (e.g. 477464)
     * @return ArrayList of Prerequisite Course Numbers
     * @throws ResponseException
     * @throws NotFound
     */
    private static ArrayList getPrereqs(String id) throws ResponseException, NotFound {
        ArrayList<String> prereqs = new ArrayList<String>();
        UserAgent u = new UserAgent();
        String url = "https://isis.jhu.edu/classes/ClassDetails.aspx?id=" + id;
        u.visit(url);
        Document doc = u.doc;
        String courseDesc = doc.findFirst("<span id=sectionDetails_lblDescription").innerHTML();
        int start = courseDesc.indexOf("Recommended Course Background: ");
        courseDesc = courseDesc.substring(start+31);

        Pattern p = Pattern.compile("(AS|EN).[0-9]{3}.[0-9]{3}");
        Matcher m = p.matcher(courseDesc);

        while (m.find()) {
            prereqs.add(m.group(0));
        }

        return prereqs;
    }
}