package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NewsItemParser {

    private ArrayList<NewsItem> newsItems;
    private NewsItem newsItem;
    private String text;

    public NewsItemParser() {
        newsItems = new ArrayList<NewsItem>();
    }

    public ArrayList<NewsItem> parse(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            // Set the input to the XML file passed into the constructor
            parser.setInput(is, null);

            int eventType = parser.getEventType();

            // While there are still things to parse
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // Start of the news item, create a new item
                        if(tagname.equalsIgnoreCase("item")) {
                            newsItem = new NewsItem();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        // End of the news item, add the new item to the list
                        if(tagname.equalsIgnoreCase("item")) {
                            newsItems.add(newsItem);
                        } else if(tagname.equalsIgnoreCase("title")) {
                            newsItem.setTitle(text);
                        } else if(tagname.equalsIgnoreCase("pubDate")) {
                            newsItem.setDate(text);
                        } else if(tagname.equalsIgnoreCase("description")) {
                            newsItem.setDescription(text);
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return newsItems;
    }

}
