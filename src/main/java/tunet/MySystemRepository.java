package tunet;

import tunet.repository.*;

import javax.persistence.EntityManager;

public class MySystemRepository {
    private final EntityManager entityManager;
    private final Users users;
    private final ArtistLists artistLists;
    private final Posts posts;
    private final Chats chats;
    private final Images images;
    private final Songs songs;
    private final Notifications notifications;

    public MySystemRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.users = new Users(entityManager);
        this.artistLists = new ArtistLists(entityManager);
        this.posts = new Posts(entityManager);
        this.chats = new Chats(entityManager);
        this.images = new Images(entityManager);
        this.songs = new Songs(entityManager);
        this.notifications = new Notifications(entityManager);
    }

    public static MySystemRepository create(EntityManager entityManager) {
        return new MySystemRepository(entityManager);
    }

    public Users users() {
        return users;
    }
    public ArtistLists artistLists(){return artistLists;}
    public Posts posts(){return posts;}
    public Chats chats(){return chats;}
    public Images images(){return images;}
    public Songs songs(){return songs;}
    public Notifications notifications() {
        return notifications;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
