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
        int lastId = getLastId();
        String newID = String.valueOf(lastId + 1);
        final Chat newChat = Chat.create(email1, email2, initialMessage, newID);

        return Transactions.persist(newChat);
    }
    private int getLastId(){
        List<Chat> list = getChatList();
        if (list.isEmpty()) return 0;
        Chat chat = list.get(list.size()-1);
        return Integer.parseInt(chat.getId());
    }

    private List<Chat> getChatList() {
        return entityManager.createQuery("SELECT u FROM Chat u", Chat.class)
                .getResultList();
    }

    public Chat addChat(String emailME, String emailHIM, String messageME, boolean isMEartist) {
        Chat chat = exists(emailME, emailHIM, isMEartist);
        if (chat == null){
            if (isMEartist){
                return createChat(emailHIM, emailME, "2" + messageME);
            }
            else{
                return createChat(emailME, emailHIM, "1" + messageME);
            }
        }
        String messages = chat.getMessages();
        if (isMEartist){
            chat.setMessages(messages + "~2" + messageME);
        }
        else{
            chat.setMessages(messages + "~1" + messageME);
        }
        return chat;
    }

    private Chat exists(String emailME, String emailHIM, boolean isMEartist) {
        List<Chat> chats;
        if (isMEartist){
            chats = findChatsByLocalEmail(emailHIM);
            for (Chat chat : chats) {
                if (chat.getEmail2().equals(emailME)) return chat;
            }
        }
        else{
            chats = findChatsByLocalEmail(emailME);
            for (Chat chat : chats) {
                if (chat.getEmail2().equals(emailHIM)) return chat;
            }
        }
        return null;
    }
    private List<Chat> findChatsByLocalEmail(String email1) {
        return Transactions.tx(() -> EntityManagers.currentEntityManager()
                .createQuery("SELECT u FROM Chat u WHERE u.email1 LIKE :email1", Chat.class)
                .setParameter("email1", email1).getResultList());
    }
    private List<Chat> findChatsByArtistEmail(String email2) {
        return Transactions.tx(() -> EntityManagers.currentEntityManager()
                .createQuery("SELECT u FROM Chat u WHERE u.email2 LIKE :email2", Chat.class)
                .setParameter("email2", email2).getResultList());
    }

    public List<Chat> chatsFromMail(String mail, boolean isArtistME) {
        if (isArtistME){
            return findChatsByArtistEmail(mail);
        }
        return findChatsByLocalEmail(mail);
    }

    public Chat certainChat(String emailME, String emailHIM, boolean isArtistME) {
        List<Chat> chats;
        if (isArtistME){
            chats = findChatsByArtistEmail(emailME);
            for (Chat chat : chats) {
                if (chat.getEmail1().equals(emailHIM)) return chat;
            }
        }
        else{
            chats = findChatsByLocalEmail(emailME);
            for (Chat chat : chats) {
                if (chat.getEmail2().equals(emailHIM)) return chat;
            }
        }
        return null;
    }
}
