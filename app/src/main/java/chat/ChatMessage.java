package chat;

/**
 * Created by MyPC on 13/05/2016.
 */
public class ChatMessage {
//    public boolean left;
    public String imgUserChat;
    public String imgUserChat_2;
    public String userEmail;
    public String message;
    public String fullName;
    public String fullName_2;


    public ChatMessage(){};
    public ChatMessage(String imgUserChat, String message, String userEmail, String fullName, String imgUserChat_2, String fullName_2) {
        this.imgUserChat = imgUserChat;
        this.message = message;
        this.userEmail = userEmail;
        this.fullName = fullName;
        this.imgUserChat_2 = imgUserChat_2;
        this.fullName_2 = fullName_2;
    }
}