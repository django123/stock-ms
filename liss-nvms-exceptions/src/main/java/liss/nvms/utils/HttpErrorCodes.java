package liss.nvms.utils;

public enum HttpErrorCodes {  
    
	INTERNAL_SERVER_ERROR(500),
	CONFLITS(409),
	NO_CONTENT_DATA(204),
	BAD_FORMAT_DATA(208),
	CREATE(201),
	SUCCESS(200);

    private int code;

    HttpErrorCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    
    public static HttpErrorCodes fromId(int id) {
        for (HttpErrorCodes type : values()) {
            if (type.getCode() == id) {
                return type;
            }
        }
        return null;
    }
    
    
}