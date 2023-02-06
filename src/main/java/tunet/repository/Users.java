package tunet.repository;

import tunet.Util.Base64Parser;
import tunet.model.RegistrationUserForm;
import tunet.model.User;
import tunet.persistence.Transactions;
import tunet.persistence.EntityManagers;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Optional;

public class Users {
    private final EntityManager entityManager;
    public Users(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    public User createUser(RegistrationUserForm signUpValues) {
        final User newUser = User.create(signUpValues.getEmail(),signUpValues.getUsername(), signUpValues.getPassword(), signUpValues.isArtist());

        if (exists(newUser.getEmail())) throw new IllegalStateException("User already exists.");

        return Transactions.persist(newUser);
    }

    public boolean exists(String email) {
        return findByEmail(email).isPresent();
    }

    public Optional<User> findByEmail(String email) {
//        return Transactions.tx(() ->
////                        EntityManagers.currentEntityManager()
//                        EntityManagers.currentEntityManager()
//                .createQuery("SELECT u FROM User u WHERE u.email LIKE :email", User.class)
//                .setParameter("email", email).getResultList()).stream()
//                .findFirst();
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email LIKE :email", User.class)
                .setParameter("email", email).getResultList().stream()
                .findFirst();
    }

    public String getProfPicFromMail(String mail) throws IOException {
        Optional<User> user = findByEmail(mail);
        if (user.isEmpty()) return null;
        return Base64Parser.convertToBase64(user.get().getProfilePictureUrl());
    }

    public User updateRating(String email, int rating) {
        User user = findByEmail(email).get();
        String prevRating = user.getRating();
        String [] str = prevRating.split("-");
        int sumOfStars = Integer.parseInt(str[0]);
        int amountOfRatingsGiven = Integer.parseInt(str[1]);
        sumOfStars += rating;
        amountOfRatingsGiven += 1;
//        return Transactions.updateRating(user, sumOfStars + "-" + amountOfRatingsGiven);
        return updateRating(user, sumOfStars + "-" + amountOfRatingsGiven);
    }
    private User updateRating(User user, String rating) {
        user.setRating(rating);
        return user;
    }
}
