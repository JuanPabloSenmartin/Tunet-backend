package tunet.repository;

import tunet.Util.Base64Parser;
import tunet.model.Chat;
import tunet.model.Image;
import tunet.model.User;
import tunet.persistence.EntityManagers;
import tunet.persistence.Transactions;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static tunet.persistence.Transactions.tx;

public class Images {
    private final EntityManager entityManager;

    public Images(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Image createImage(String email, String imageUrl) {
        int lastId = getMaxId();
        String newID = String.valueOf(lastId + 1);
        final Image newImage = Image.create(newID, email, imageUrl);

        return Transactions.persist(newImage);
    }

    public int getMaxId() {
        List<Image> list = getImageList();
        int max = 1;
        for (int i = list.size() - 1; i >= 0; i--) {
            int num = Integer.parseInt(list.get(i).getId());
            if (num > max) {
                max = num;
            }
        }
        return max;
    }
    private List<Image> getImageList() {
        return
                //EntityManagers.currentEntityManager()
                entityManager
                        .createQuery("SELECT u FROM Image u", Image.class)
                .getResultList();
    }

    public List<Image> getAllImagesFromEmail(String email) {
//        return Transactions.tx(() ->
//                EntityManagers.currentEntityManager()
//                        .createQuery("SELECT u FROM Image u WHERE u.email LIKE :email", Image.class)
//                        .setParameter("email", email).getResultList());
        return
                entityManager
                        .createQuery("SELECT u FROM Image u WHERE u.email LIKE :email", Image.class)
                        .setParameter("email", email).getResultList();
    }

    public Image deleteImage(String imageUrl, String mail) throws IOException {
        Image image = getImage(imageUrl, mail);
        if (image != null){
            Base64Parser.deletePath(image.getImageUrl());
            Transactions.remove(image);
        }

        return null;
    }
    private Image getImage(String imageUrl, String mail) throws IOException {
        List<Image> images = getAllImagesFromEmail(mail);
        for (Image image : images){
            if (Base64Parser.convertToBase64(image.getImageUrl()).equals(imageUrl)){
                return image;
            }
        }
        return null;
    }
}
