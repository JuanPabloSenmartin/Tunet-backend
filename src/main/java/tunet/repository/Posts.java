package tunet.repository;
import tunet.model.Post;
import tunet.model.PostForm;
import tunet.persistence.Transactions;
import tunet.persistence.EntityManagers;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;


public class Posts {
    private final EntityManager entityManager;


    public Posts(EntityManager entityManager) {
        this.entityManager = entityManager;

    }

    public Post createPost(PostForm form) {
        int lastId = getLastId();
        String newID = String.valueOf(lastId + 1);
        final Post newPost = Post.create(newID, form.getLocalEmail(), form.getDescription(), form.getTitle(), form.getDate());

        if (exists(newPost.getId())) throw new IllegalStateException("Post id already exists.");

        return Transactions.persist(newPost);
    }
    private int getLastId(){
        List<Post> list = getPostList();
        if (list.isEmpty()) return 1;
        Post post = list.get(list.size()-1);
        return Integer.parseInt(post.getId());
    }

    private List<Post> getPostList() {
        return entityManager.createQuery("SELECT u FROM Post u", Post.class)
                .getResultList();
    }

    public boolean exists(String id) {
        return findByPostID(id).isPresent();
    }

    public Optional<Post> findByPostID(String id) {
        return Transactions.tx(() -> EntityManagers.currentEntityManager()
                .createQuery("SELECT u FROM Post u WHERE u.id LIKE :id", Post.class)
                .setParameter("id", id).getResultList()).stream()
                .findFirst();
    }

    public List<Post> listFromMail(String localEmail) {
        return entityManager.createQuery("SELECT u FROM Post u WHERE u.localEmail LIKE :localEmail", Post.class)
                .setParameter("localEmail", localEmail)
                .getResultList();
    }

    public List<Post> listAllPosts() {
        return entityManager.createQuery("SELECT u FROM Post u", Post.class)
                .getResultList();
    }
}
