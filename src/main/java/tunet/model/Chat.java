package tunet.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CHATS")
public class Chat {
    @Id
    private String id;

    private String email1;//local

    private String email2;//artist

    private String messages;//1messageLocal~2messageArtist~

    public Chat(){}

    public Chat(String email1, String email2, String messages, String id) {
        this.email1 = email1;
        this.email2 = email2;
        this.messages = messages;
        this.id = id;
    }
    public static Chat create(String email1,String email2, String messages, String id) {
        return new Chat(email1,email2, messages, id);
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

    public String getId() {
        return id;
    }
}
