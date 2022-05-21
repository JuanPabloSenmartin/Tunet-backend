package tunet.repository;

import tunet.model.ArtistListInPost;
import tunet.model.Post;
import tunet.repository.ArtistLists;
import tunet.persistence.Transactions;
import tunet.persistence.EntityManagers;
import javax.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static tunet.persistence.Transactions.tx;

public class ArtistLists {
    private final EntityManager entityManager;


    public ArtistLists(EntityManager entityManager) {
        this.entityManager = entityManager;
    }



    public ArtistListInPost createArtistList(String postID, String artistEmail) {
        int lastId = getLastId();
        String newID = String.valueOf(lastId + 1);

        final ArtistListInPost newArtistList = ArtistListInPost.create(newID, postID, artistEmail);

        if (exists(newArtistList.getId())) throw new IllegalStateException("Post id already exists.");

        return Transactions.persist(newArtistList);
    }

    private int getLastId(){
        List<ArtistListInPost> list = getList();
        ArtistListInPost artist = list.get(list.size()-1);
        return Integer.parseInt(artist.getId());
    }

    private List<ArtistListInPost> getList() {
        return entityManager.createQuery("SELECT u FROM ArtistListInPost u", ArtistListInPost.class)
                .getResultList();
    }

    public boolean exists(String id) {
        return findByID(id).isPresent();
    }

    public Optional<ArtistListInPost> findByID(String id) {
        return Transactions.tx(() -> EntityManagers.currentEntityManager()
                .createQuery("SELECT u FROM ArtistListInPost u WHERE u.id LIKE :id", ArtistListInPost.class)
                .setParameter("id", id).getResultList()).stream()
                .findFirst();
    }

    public List<ArtistListInPost> listFromPostID(String postID) {
        return entityManager.createQuery("SELECT u FROM ArtistListInPost u WHERE u.postID LIKE :postID", ArtistListInPost.class)
                .setParameter("postID", postID)
                .getResultList();
    }
}
