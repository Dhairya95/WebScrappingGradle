package org.example;

public class Translation {
String englishName;
String spanishName;
String spanishDescription;

    public Translation(String englishName, String spanishName, String spanishDescription) {
        this.englishName = englishName;
        this.spanishName = spanishName;
        this.spanishDescription = spanishDescription;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getSpanishName() {
        return spanishName;
    }

    public String getSpanishDescription() {
        return spanishDescription;
    }
}
