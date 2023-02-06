package tunet.repository;

import tunet.Util.Base64Parser;
import tunet.model.*;
import tunet.persistence.Transactions;
import tunet.persistence.EntityManagers;
import javax.persistence.EntityManager;

import java.io.IOException;
import java.util.*;


public class ArtistLists {
    private final EntityManager entityManager;


    public ArtistLists(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public ArtistListInPost createArtistList(String postID, String artistEmail) {
        int lastId = getMaxId();
        String newID = String.valueOf(lastId + 1);

        final ArtistListInPost newArtistList = ArtistListInPost.create(newID, postID, artistEmail);

        if (exists(newArtistList.getId())) throw new IllegalStateException("id already exists.");

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
        return entityManager
                .createQuery("SELECT u FROM ArtistListInPost u WHERE u.id LIKE :id", ArtistListInPost.class)
                .setParameter("id", id).getResultList().stream()
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
    public List<ArtistListInPost> getArtistsFromMail(String artistEmail) {
        return entityManager.createQuery("SELECT u FROM ArtistListInPost u WHERE u.artistEmail LIKE :artistEmail", ArtistListInPost.class)
                .setParameter("artistEmail", artistEmail)
                .getResultList();
    }

    public String getAcceptedUser(String postId) {
        List<ArtistListInPost> list = listFromPostID(postId);
        for (ArtistListInPost a : list){
            if (a.getAccepted().equals("TRUE")) return a.getArtistEmail();
        }
        return "";
    }

    public ArtistListInPost deleteArtistList(String postID, String mail) {
        Optional<ArtistListInPost> entity = getEntity(postID, mail);
        entity.ifPresent(Transactions::remove);
        return null;
    }

    public Optional<ArtistListInPost> getEntity(String postID, String artistEmail) {
        return entityManager.createQuery("SELECT u FROM ArtistListInPost u WHERE u.artistEmail LIKE :artistEmail AND u.postID LIKE :postID", ArtistListInPost.class)
                .setParameter("artistEmail", artistEmail).setParameter("postID", postID)
                .getResultList().stream()
                .findFirst();
    }
    public List<ArtistListInfo> getArtistListData(String postID, Users users) throws IOException {
        List<ArtistListInPost> list = listFromPostID(postID);
        List<ArtistListInfo> result = new ArrayList<>();
        for (ArtistListInPost item : list){
            Optional<User> artist = users.findByEmail(item.getArtistEmail());
            if (artist.isPresent()){
                result.add(new ArtistListInfo(item.getId(), item.getArtistEmail(), artist.get().getIntRating(), Base64Parser.convertToBase64(artist.get().getProfilePictureUrl())));
            }
        }
        return result;
    }
}
