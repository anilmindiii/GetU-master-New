package getu.app.com.getu.chat.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by abc on 02/01/2018.
 */

public class ChatHistory implements Serializable {

    public String message = "";
    public Object timeStamp;
    public String firebaseId = "";
    public String deviceToken = "";
    public String uid = "";
    public String profilePic = "";
    public String name = "";
    public String deleteby = "";

}
