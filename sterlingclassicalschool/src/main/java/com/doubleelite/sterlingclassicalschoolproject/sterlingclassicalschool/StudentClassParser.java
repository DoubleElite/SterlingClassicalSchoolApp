package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class StudentClassParser {

    ArrayList<StudentClass> studentClasses;
    StudentClass studentClass;
    String parserText;

    public StudentClassParser() {
        studentClasses = new ArrayList<StudentClass>();
    }

    public ArrayList<StudentClass> parse(InputStream stream) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            parser = factory.newPullParser();
            parser.setInput(stream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        if(tagName.equalsIgnoreCase("class")) {
                            // Make a new class object
                            studentClass = new StudentClass();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        parserText = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(tagName.equalsIgnoreCase("grade")) {
                            studentClass.grade = Integer.parseInt(parserText);
                        } else if (tagName.equalsIgnoreCase("subject")) {
                            studentClass.subject = parserText;
                        } else if (tagName.equalsIgnoreCase("time")) {
                            studentClass.time = parserText;
                        } else if (tagName.equalsIgnoreCase("room")) {
                            studentClass.room = parserText;
                        } else if (tagName.equalsIgnoreCase("color")) {
                            studentClass.color = parserText;
                        } else if (tagName.equalsIgnoreCase("class")) {
                            studentClasses.add(studentClass);
                        }
                        break;

                    default:
                        break;

                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e ) {
            e.printStackTrace();
        }
        // Return an arraylist of the student classes we made by parsing all the xml
        return studentClasses;
    }

}
