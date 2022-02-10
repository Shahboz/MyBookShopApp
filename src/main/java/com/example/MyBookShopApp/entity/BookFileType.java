package com.example.MyBookShopApp.entity;

public enum BookFileType {

    PDF(".pdf"), EPUB(".epub"), FB2(".fb2");

    private final String fileExtentionString;

    BookFileType(String fileExtentionString) {
        this.fileExtentionString = fileExtentionString;
    }

    public static String getExtentionStringByTypeId(Integer typeId) {
        switch (typeId) {
            case 1 : return BookFileType.PDF.fileExtentionString;
            case 2 : return BookFileType.EPUB.fileExtentionString;
            case 3 : return BookFileType.FB2.fileExtentionString;
            default: return "";
        }
    }
}