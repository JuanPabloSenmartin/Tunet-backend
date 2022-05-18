package tunet;

import tunet.repository.ArtistLists;
import tunet.repository.Posts;
import tunet.repository.Users;

import javax.persistence.EntityManager;

public class MySystemRepository {
    private final Users users;
    private final ArtistLists artistLists;
    private final Posts posts;

    public MySystemRepository(EntityManager entityManager) {
        this.users = new Users(entityManager);
        this.artistLists = new ArtistLists(entityManager);
        this.posts = new Posts(entityManager);
    }

    public static MySystemRepository create(EntityManager entityManager) {
        return new MySystemRepository(entityManager);
    }

    public Users users() {
        return users;
    }
    public ArtistLists artistLists(){return artistLists;}
    public Posts posts(){return posts;}
}
