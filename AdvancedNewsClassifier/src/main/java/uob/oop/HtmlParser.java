package uob.oop;

public class HtmlParser {
    /***
     * Extract the title of the news from the _htmlCode.
     * @param _htmlCode Contains the full HTML string from a specific news. E.g. 01.htm.
     * @return Return the title if it's been found. Otherwise, return "Title not found!".
     */
    public static String getNewsTitle(String _htmlCode) {
        String titleTagOpen = "<title>";
        String titleTagClose = "</title>";

        int titleStart = _htmlCode.indexOf(titleTagOpen) + titleTagOpen.length();
        int titleEnd = _htmlCode.indexOf(titleTagClose);

        if (titleStart != -1 && titleEnd != -1 && titleEnd > titleStart) {
            String strFullTitle = _htmlCode.substring(titleStart, titleEnd);
            return strFullTitle.substring(0, strFullTitle.indexOf(" |"));
        }

        return "Title not found!";
    }

    /***
     * Extract the content of the news from the _htmlCode.
     * @param _htmlCode Contains the full HTML string from a specific news. E.g. 01.htm.
     * @return Return the content if it's been found. Otherwise, return "Content not found!".
     */
    public static String getNewsContent(String _htmlCode) {
        String contentTagOpen = "\"articleBody\": \"";
        String contentTagClose = " \",\"mainEntityOfPage\":";

        int contentStart = _htmlCode.indexOf(contentTagOpen) + contentTagOpen.length();
        int contentEnd = _htmlCode.indexOf(contentTagClose);

        if (contentStart != -1 && contentEnd != -1 && contentEnd > contentStart) {
            return _htmlCode.substring(contentStart, contentEnd).toLowerCase();
        }

        return "Content not found!";
    }

    public static NewsArticles.DataType getDataType(String _htmlCode) {
        //TODO Task 3.1 - 1.5 Marks
        String typeTagOpen = "<datatype>";
        String typeTagClose = "</datatype>";

        int typeStart = _htmlCode.indexOf(typeTagOpen) + typeTagOpen.length(); // gets index of dataType tags
        int typeEnd = _htmlCode.indexOf(typeTagClose);
        if (typeStart != -1 && typeEnd != -1 && typeEnd > typeStart) { // check if DT tags exists
            if (_htmlCode.substring(typeStart, typeEnd).equals(NewsArticles.DataType.Training.toString())) {
                return NewsArticles.DataType.Training; // returns DT
            }
        }
        return NewsArticles.DataType.Testing;
    }

    public static String getLabel (String _htmlCode) {
        //TODO Task 3.2 - 1.5 Marks
        String labelTagOpen = "<label>";
        String labelTagClose = "</label>";

        int labelStart = _htmlCode.indexOf(labelTagOpen) + labelTagOpen.length(); // gets index of label tags
        int labelEnd = _htmlCode.indexOf(labelTagClose);
        if (labelStart != -1 && labelEnd != -1 && labelEnd > labelStart) {
            if (!_htmlCode.substring(labelStart, labelEnd).equals("")) {
                return _htmlCode.substring(labelStart, labelEnd); // returns label otherwise -1
            }
        }
        return "-1";
    }


}
