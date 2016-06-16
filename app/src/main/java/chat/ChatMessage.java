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
    public String time;
    public String userEmail_2;
    public String whoSend;

    public ChatMessage() {
    }

    public ChatMessage(ChatMessage ob) {
        this.imgUserChat = ob.imgUserChat;
        this.message = ob.message;
        this.userEmail = ob.userEmail;
        this.fullName = ob.fullName;
        this.imgUserChat_2 = ob.imgUserChat_2;
        this.fullName_2 = ob.fullName_2;
        this.time = ob.time;
        this.userEmail_2 = ob.userEmail_2;
        this.whoSend = ob.whoSend;
    }


    public ChatMessage(String imgUserChat, String message, String userEmail, String fullName, String imgUserChat_2, String fullName_2, String time,String userEmail_2, String whoSend) {
        this.imgUserChat = imgUserChat;
        this.message = message;
        this.userEmail = userEmail;
        this.fullName = fullName;
        this.imgUserChat_2 = imgUserChat_2;
        this.fullName_2 = fullName_2;
        this.time = time;
        this.userEmail_2 = userEmail_2;
        this.whoSend = whoSend;
    }
}