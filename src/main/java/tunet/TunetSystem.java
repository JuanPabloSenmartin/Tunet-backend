package tunet;

import tunet.model.EditProfileForm;
import tunet.model.RegistrationUserForm;
import tunet.model.User;
import tunet.persistence.EntityManagers;
import tunet.persistence.Transactions;
import tunet.repository.Users;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TunetSystem {

    private final EntityManagerFactory factory;

    private TunetSystem(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public static TunetSystem create(String persistenceUnitName) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory(persistenceUnitName);
        EntityManagers.setFactory(factory);
        return new TunetSystem(factory);
    }

    public User registerUser(RegistrationUserForm form) {
        return runInTransaction(datasource -> {
            final Users users = datasource.users();
            return users.exists(form.getEmail()) ? null : users.createUser(form);
        });
    }
    public void editProfile(EditProfileForm form) {
        User user = findUserByEmail(form.getEmail()).get();
        Transactions.update(user, form);
        user.printUser();
    }

    public Optional<User> findUserByEmail(String email) {
        return runInTransaction(
                ds -> ds.users().findByEmail(email)
        );
    }

    public List<User> listUsers() {
        return runInTransaction(
                ds -> ds.users().list()
        );
    }

    public boolean validPassword(String password, User foundUser) {
        // Super dummy implementation. Zero security
        return foundUser.getPassword().equals(password);
    }


    private <E> E runInTransaction(Function<MySystemRepository, E> closure) {
        final EntityManager entityManager = factory.createEntityManager();
        final MySystemRepository ds = MySystemRepository.create(entityManager);

        try {
            entityManager.getTransaction().begin();
            final E result = closure.apply(ds);
            entityManager.getTransaction().commit();
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public Map<String, String> getProfileData(User user) {
        Map<String, String> map = new HashMap<>();
        if (user.isArtist()){
            map.put("email", user.getEmail());
            map.put("videoUrl", user.getArtistVideoUrl());
            map.put("username", user.getUsername());
            map.put("description", user.getDescription());
            map.put("pictureUrl", user.getPictureUrl());
            map.put("profilePictureUrl", user.getProfilePictureUrl());
            map.put("location", user.getLocation());
            map.put("phoneNumber", user.getPhoneNumber());
        }
        else {
            map.put("email", user.getEmail());
            map.put("username", user.getUsername());
            map.put("description", user.getDescription());
            map.put("pictureUrl", user.getPictureUrl());
            map.put("profilePictureUrl", user.getProfilePictureUrl());
            map.put("location", user.getLocation());
            map.put("phoneNumber", user.getPhoneNumber());
        }
        return map;
    }


}
