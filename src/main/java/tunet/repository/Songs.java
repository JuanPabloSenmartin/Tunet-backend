package tunet.repository;
import tunet.Util.Base64Parser;
import tunet.model.Chat;
import tunet.model.Image;
import tunet.model.Song;
import tunet.model.User;
import tunet.persistence.EntityManagers;
import tunet.persistence.Transactions;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static tunet.persistence.Transactions.tx;

public class Songs {

    private final EntityManager entityManager;

    public Songs(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Song createSong(String email, String imageUrl) {
        int lastId = getMaxId();
        String newID = String.valueOf(lastId + 1);
        final Song newSong = Song.create(newID, email, imageUrl);

        return Transactions.persist(newSong, entityManager);
    }

    public int getMaxId() {
        List<Song> list = getSongList();
        int max = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            int num = Integer.parseInt(list.get(i).getId());
            if (num > max) {
                max = num;
            }
        }
        return max;
    }
    private List<Song> getSongList() {
        return
                entityManager
                        .createQuery("SELECT u FROM Song u", Song.class)
                .getResultList();
    }

    public List<Song> getAllSongsFromEmail(String email) {
        return
                entityManager
                        .createQuery("SELECT u FROM Song u WHERE u.email LIKE :email", Song.class)
                        .setParameter("email", email).getResultList();
    }

    public Song deleteSong(String songUrl, String mail) throws IOException {
        Song song = getSong(songUrl, mail);
        if (song != null){
            Base64Parser.deletePath(song.getSongUrl());
            Transactions.remove(song, entityManager);
        }

        return null;
    }
    private Song getSong(String songUrl, String mail) throws IOException {
        List<Song> songs = getAllSongsFromEmail(mail);
        for (Song song : songs){
            if (Base64Parser.convertToBase64(song.getSongUrl()).equals(songUrl)){
                return song;
            }
        }
        return null;
    }
}
