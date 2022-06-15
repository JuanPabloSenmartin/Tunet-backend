package tunet.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CHATS")
public class Chat {
    @Id
    private String email1;

    private String email2;

    private String messages;

    public Chat(){}

    public Chat(String email1, String email2, String messages) {
        this.email1 = email1;
        this.email2 = email2;
        this.messages = messages;
    }
    public static Chat create(String email1,String email2, String messages) {
        return new Chat(email1,email2, messages);
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
