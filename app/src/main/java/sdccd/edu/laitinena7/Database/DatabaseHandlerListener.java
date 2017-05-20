package sdccd.edu.laitinena7.Database;

import sdccd.edu.laitinena7.Utils.MessageEnum;

/**
 * Created by Tuulikki Laitinen 5/18/2017.
 */

public interface DatabaseHandlerListener {

    public void databaseCallback(MessageEnum message, Object result);
}
