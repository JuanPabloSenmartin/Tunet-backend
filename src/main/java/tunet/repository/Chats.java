package tunet.repository;
import tunet.model.Chat;
import tunet.model.Post;
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
        int lastId = getMaxId();
        String newID = String.valueOf(lastId + 1);
        final Chat newChat = Chat.create(email1, email2, initialMessage, newID);

        return Transactions.persistNewChat(newChat, entityManager);
    }
    private int getMaxId(){
        List<Chat> list = getChatList();
        int max = 1;
        for (int i = list.size()-1; i >= 0; i--) {
            int num = Integer.parseInt(list.get(i).getId());
            if (num > max){
                max = num;
            }
        }
        return max;
    }
    private List<Chat> getChatList() {
        return
//                EntityManagers.currentEntityManager()
        entityManager
                        .createQuery("SELECT u FROM Chat u", Chat.class)
                .getResultList();
    }
    private List<Chat> findChatsByLocalEmail(String email1) {
//        return tx(() ->
                        //EntityManagers.currentEntityManager()
        return
                entityManager
                .createQuery("SELECT u FROM Chat u WHERE u.email1 LIKE :email1", Chat.class)
                .setParameter("email1", email1).getResultList()
//        )
        ;
    }
    private List<Chat> findChatsByArtistEmail(String email2) {
//        return tx(() ->
                        //EntityManagers.currentEntityManager()
                return entityManager
                .createQuery("SELECT u FROM Chat u WHERE u.email2 LIKE :email2", Chat.class)
                .setParameter("email2", email2).getResultList();
//        );
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
//    public Chat updateChat(Chat chat, String message){
//        return Transactions.updateChat(chat, message, EntityManagers.currentEntityManager());
//    }

}
