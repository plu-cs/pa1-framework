package rttr.gui;

public class TextUtils {

    public static String pluralize( int value, String singular ) {
        String str = singular;
        if( value != 1 ) {
            str = singular + "s";
        }
        return String.format("%,d %s", value, str);
    }
}
