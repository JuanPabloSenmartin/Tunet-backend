package tunet.repository;

import tunet.model.ArtistListInPost;
import tunet.model.Post;
import tunet.persistence.Transactions;
import tunet.persistence.EntityManagers;
import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ArtistLists {
    private final EntityManager entityManager;


    public ArtistLists(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public ArtistListInPost createArtistList(String postID, String artistEmail) {
        int lastId = getMaxId();
        String newID = String.valueOf(lastId + 1);

        final ArtistListInPost newArtistList = ArtistListInPost.create(newID, postID, artistEmail);

        if (exists(newArtistList.getId())) throw new IllegalStateException("Post id already exists.");

        return Transactions.persist(newArtistList);
    }
    private int getMaxId(){
        List<ArtistListInPost> list = getList();
        int max = 1;
        for (int i = list.size()-1; i >= 0; i--) {
            int num = Integer.parseInt(list.get(i).getId());
            if (num > max){
                max = num;
            }
        }
        return max;
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

    public List<String> getPostIdsFromMail(String artistEmail, List<Post> posts) {
        List<ArtistListInPost> list = getArtistsFromMail(artistEmail);
        List<String> finalList = new ArrayList<>();
        for (Post post : posts) {
            boolean current = false;
            for (ArtistListInPost artistListInPost : list) {
                if (post.getId().equals(artistListInPost.getPostID())) {
                    current = true;
                    break;
                }
            }
            if (!current) {
                finalList.add(post.getId());
            }
        }
        return finalList;
    }
    private List<ArtistListInPost> getArtistsFromMail(String artistEmail) {
        return entityManager.createQuery("SELECT u FROM ArtistListInPost u WHERE u.artistEmail LIKE :artistEmail", ArtistListInPost.class)
                .setParameter("artistEmail", artistEmail)
                .getResultList();
    }
}
