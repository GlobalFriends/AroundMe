package com.globalfriends.com.aroundme.protocol;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by vishal on 11/19/2015.
 */
public class PlacesSupportedLanguages extends HashMap {
    private static final TreeMap<String, String> mSupportedLanguages;

    static {
        mSupportedLanguages = new TreeMap<String, String>();
        mSupportedLanguages.put("Arabic", "ar");
        mSupportedLanguages.put("Bulgarian", "bg");
        mSupportedLanguages.put("Bengali", "bn");
        mSupportedLanguages.put("Catalan", "ca");
        mSupportedLanguages.put("Czech", "cs");
        mSupportedLanguages.put("Danish", "da");
        mSupportedLanguages.put("German", "de");
        mSupportedLanguages.put("Greek", "el");
        mSupportedLanguages.put("English", "en");
        mSupportedLanguages.put("English (Australian)", "en-AU");
        mSupportedLanguages.put("English (Great Britain)", "en-GB");
        mSupportedLanguages.put("Spanish", "es");
        mSupportedLanguages.put("Basque", "eu");
        mSupportedLanguages.put("Farsi", "fa");
        mSupportedLanguages.put("Finnish", "fi");
        mSupportedLanguages.put("Filipino", "fil");
        mSupportedLanguages.put("French", "fr");
        mSupportedLanguages.put("Galician", "gl");
        mSupportedLanguages.put("Gujarati", "gu");
        mSupportedLanguages.put("Hindi", "hi");
        mSupportedLanguages.put("Croatian", "hr");
        mSupportedLanguages.put("Hungarian", "hu");
        mSupportedLanguages.put("Indonesian", "id");
        mSupportedLanguages.put("Italian", "it");
        mSupportedLanguages.put("Hebrew", "iw");
        mSupportedLanguages.put("Japanese", "ja");
        mSupportedLanguages.put("Kannada", "kn");
        mSupportedLanguages.put("Korean", "ko");
        mSupportedLanguages.put("Lithuanian", "lt");
        mSupportedLanguages.put("Latvian", "lv");
        mSupportedLanguages.put("Malayalam", "ml");
        mSupportedLanguages.put("Marathi", "mr");
        mSupportedLanguages.put("Dutch", "nl");
        mSupportedLanguages.put("Norwegian", "no");
        mSupportedLanguages.put("Polish", "pl");
        mSupportedLanguages.put("Portuguese", "pt");
        mSupportedLanguages.put("Portuguese (Brazil)", "pt-BR");
        mSupportedLanguages.put("Portuguese (Portugal)", "pt-PT");
        mSupportedLanguages.put("Romanian", "ro");
        mSupportedLanguages.put("Slovak", "sk");
        mSupportedLanguages.put("Slovenian", "sl");
        mSupportedLanguages.put("Serbian", "sr");
        mSupportedLanguages.put("Swedish", "sv");
        mSupportedLanguages.put("Tamil", "ta");
        mSupportedLanguages.put("Telugu", "te");
        mSupportedLanguages.put("Thai", "th");
        mSupportedLanguages.put("Tagalog", "tl");
        mSupportedLanguages.put("Turkish", "tr");
        mSupportedLanguages.put("Ukrainian", "uk");
        mSupportedLanguages.put("Vietnamese", "vi");
        mSupportedLanguages.put("Chinese (Simplified)", "zh-CN");
        mSupportedLanguages.put("Chinese (Traditional)", "zh-TW");
    }

    public static Set<String> getListOfLanguages() {
        return mSupportedLanguages.keySet();
    }

    public static String getLanguage(final String key) {
        return mSupportedLanguages.get(key);
    }
}
