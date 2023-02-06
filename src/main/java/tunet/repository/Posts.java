package tunet.repository;
import tunet.Util.Base64Parser;
import tunet.model.*;
import tunet.Util.LocationManager;
import tunet.persistence.Transactions;
import tunet.persistence.EntityManagers;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Posts {
    private final EntityManager entityManager;


    public Posts(EntityManager entityManager) {
        this.entityManager = entityManager;

    }

    public Post createPost(PostForm form) {
        int lastId = getMaxId();
        String newID = String.valueOf(lastId + 1);
        final Post newPost = Post.create(newID, form.getLocalEmail(), form.getDescription(), form.getTitle(), form.getDate(), getGenres(form.getGenres()));

        if (exists(newPost.getId())) throw new IllegalStateException("Post id already exists.");

        return Transactions.persist(newPost);
    }
    private String getGenres(String[] genres){
        if (genres.length == 0){ return "any";}
        String str = "";
        for (int i = 0; i < genres.length-1; i++) {
            str += genres[i] + ",";
        }
        str += genres[genres.length-1];
        return str;
    }
    private int getMaxId(){
        List<Post> list = getPostList();
        int max = 1;
        for (int i = list.size()-1; i >= 0; i--) {
            int num = Integer.parseInt(list.get(i).getId());
            if (num > max){
                max = num;
            }
        }
        return max;
    }

    private List<Post> getPostList() {
        return
//                EntityManagers.currentEntityManager()
                        entityManager
                        .createQuery("SELECT u FROM Post u", Post.class)
                .getResultList();
    }

    public boolean exists(String id) {
        return findByPostID(id).isPresent();
    }

    public Optional<Post> findByPostID(String id) {
//        return Transactions.tx(() ->
//                EntityManagers.currentEntityManager()
//                .createQuery("SELECT u FROM Post u WHERE u.id LIKE :id", Post.class)
//                .setParameter("id", id).getResultList()).stream()
//                .findFirst();
        return entityManager
                                .createQuery("SELECT u FROM Post u WHERE u.id LIKE :id", Post.class)
                                .setParameter("id", id).getResultList().stream().findFirst();
    }

    public List<Post> listFromMail(String localEmail) {
        return
//                EntityManagers.currentEntityManager()
        entityManager
                        .createQuery("SELECT u FROM Post u WHERE u.localEmail LIKE :localEmail", Post.class)
                .setParameter("localEmail", localEmail)
                .getResultList();
    }

    public List<Post> listAllPosts() {
        return
                //                EntityManagers.currentEntityManager()
                entityManager
                        .createQuery("SELECT u FROM Post u", Post.class)
                .getResultList();
    }
    public List<PostInfo> listThesePosts(List<String> postsIDs, Users users, double[] artistLocation, FilterForm form) throws IOException {
        List<Post> allPosts = listAllPosts();
        List<PostInfo> finalList = new ArrayList<>();
        for (int i = 0; i < postsIDs.size(); i++) {
            for (int j = 0; j < allPosts.size(); j++) {
                Post currentPost = allPosts.get(j);
                if (postsIDs.get(i).equals(currentPost.getId())){
                    User local = users.findByEmail(currentPost.getLocalEmail()).get();
                    int distance = -1;
                    if(local.getCoordinates().length != 0 && artistLocation.length != 0) distance = LocationManager.getDistance(local.getCoordinates(), artistLocation);
                    if (passesFilter(currentPost, local, form, distance)){
                        finalList.add(new PostInfo(currentPost.getId(), currentPost.getDate(), currentPost.getDescription(), currentPost.getLocalEmail(), currentPost.getTitle(), currentPost.getGenres(), local.getIntRating(), Base64Parser.convertToBase64(local.getProfilePictureUrl()), "" + distance, null));
                    }
                }

            }
        }
        return finalList;
    }
    private boolean passesFilter(Post post, User user, FilterForm form, int distance){
        //date
        if (post.getConvertedDate().isBefore(LocalDate.now())) return false;
        //distance
        if (distance != -1 && (distance > form.getRange()[1] || distance < form.getRange()[0])) return false;
        //rating
        if (form.getRating() != null && (user.getIntRating() != form.getRating())) return false;
        //genres
        return passesGenreFilter(post, form);
    }
    private boolean passesGenreFilter(Post post, FilterForm form) {
        String[] postGenres = post.getGenresArray();
        String[] filterGenres = form.getGenres();

        if (postGenres.length == 0 || filterGenres.length == 0) return true;
        for (String filterGenre : filterGenres) {
            String fg = filterGenre.toLowerCase();
            if (fg.equals("any")) return true;
            for (String postGenre : postGenres) {
                if (postGenre.equals("any")) return true;
                if (fg.equals(postGenre)) return true;
            }
        }
        return false;
    }

    public List<PostInfo> listFilteredPostsInDiscover(List<Post> posts, Users users, FilterForm form) throws IOException {
        List<PostInfo> finalList = new ArrayList<>();
        for (Post post : posts){
            User local = users.findByEmail(post.getLocalEmail()).get();
            int distance = form.getLocation() != null ? LocationManager.getDistance(local.getCoordinates(), form.getLocation()) : -1;
            if (passesFilter(post, local, form, distance)){
                finalList.add(new PostInfo(post.getId(), post.getDate(), post.getDescription(), post.getLocalEmail(), post.getTitle(), post.getGenres(), local.getIntRating(), Base64Parser.convertToBase64(local.getProfilePictureUrl()), "" + distance, null));
            }
        }
        return finalList;
    }
}
