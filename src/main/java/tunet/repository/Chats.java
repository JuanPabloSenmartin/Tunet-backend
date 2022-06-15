package tunet.repository;
import tunet.model.Chat;
import tunet.persistence.EntityManagers;
import tunet.persistence.Transactions;

import javax.persistence.EntityManager;
import java.util.List;

public class Chats {
    private final EntityManager entityManager;

    public Chats(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Chat createChat(String email1, String email2, String initialMessage) {
        final Chat newChat = Chat.create(email1, email2, initialMessage);

        return Transactions.persist(newChat);
    }

    public Chat addChat(String emailME, String emailHIM, String messageME, boolean isMEartist) {
        Chat chat = exists(emailME, emailHIM, isMEartist);
        if (chat == null){
            if (isMEartist){
                return createChat(emailHIM, emailME, "2" + messageME + "~");
            }
            else{
                return createChat(emailME, emailHIM, "1" + messageME + "~");
            }
        }
        String messages = chat.getMessages();
        if (isMEartist){
            chat.setMessages(messages + "2" + messageME + "~");
        }
        else{
            chat.setMessages(messages + "1" + messageME + "~");
        }
        return chat;
    }

    private Chat exists(String emailME, String emailHIM, boolean isMEartist) {
        List<Chat> chat;
        if (isMEartist){
            chat = findByEmail(emailHIM);
            for (Chat value : chat) {
                if (value.getEmail2().equals(emailME)) return value;
            }
        }
        else{
            chat = findByEmail(emailME);
            for (Chat value : chat) {
                if (value.getEmail2().equals(emailHIM)) return value;
            }
        }
        return null;
    }
    private List<Chat> findByEmail(String email1) {
        return Transactions.tx(() -> EntityManagers.currentEntityManager()
                .createQuery("SELECT u FROM Chat u WHERE u.email1 LIKE :email1", Chat.class)
                .setParameter("email1", email1).getResultList());
    }
}
