package sdccd.edu.laitinena7.Utils;

/**
 * Created by Tuulikki Laitinen on 5/19/2017.
 */

public class MyMessage {

    private String id;
    private String timestamp;
    private String text;
    private String bookId;
    private String senderId;
    private String receiverId;
    private boolean isMeSender;

    public MyMessage() {}

    public MyMessage(
            String id,
            String timestamp,
            String text,
            String bookId,
            String senderId,
            String receiverId,
            boolean isMeSender) {

        this.id = id;
        this.timestamp = timestamp; //1495302157860
        this.text = text;
        this.bookId = bookId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isMeSender = isMeSender;
    }

    public boolean getIsMeSender() {
        return isMeSender;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }
}
/*
* import java.util.Calendar

Calendar calendar = Calendar.getInstance();
//int seconds = c.get(Calendar.SECOND);
int timeInMillis = calendar.getTimeInMillis();
public long getTimeInMillis()
Returns this Calendar's time value in milliseconds.
Returns:
the current time as UTC milliseconds from the epoch.
1495302157860
*/
