package dk.lyngby.config;


import dk.lyngby.model.Diary;
import dk.lyngby.model.Diarypage;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Populate {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            User user1 = new User("user1", "user1@example.com", "1234");
            User user2 = new User("user2", "user2@example.com", "1234");
            User admin = new User("admin", "admin@example.com", "4321");

            em.persist(user1);
            em.persist(user2);
            em.persist(admin);

            Role roleUser = new Role("ROLE_USER");
            Role roleAdmin = new Role("ROLE_ADMIN");

            em.persist(roleUser);
            em.persist(roleAdmin);

            user1.addRole(roleUser);
            user2.addRole(roleUser);
            admin.addRole(roleAdmin);


            Diary diaryToUser1 = new Diary(1, "About Stefan", 150);
            Diary diaryToUser2 = new Diary(2, "School Time", 150);
            user1.addDiary(diaryToUser1);
            user2.addDiary(diaryToUser2);

            em.persist(diaryToUser1);
            em.persist(diaryToUser2);

            Set<Diarypage> diary1Pages = getDiaryPagesForDiary1();
            diaryToUser1.setPages(diary1Pages);

            Set<Diarypage> diary2Pages = getDiaryPagesForDiary2();
            diaryToUser2.setPages(diary2Pages);

            em.getTransaction().commit();
        }
    }

    private static Set<Diarypage> getDiaryPagesForDiary1() {
        Set<Diarypage> pages = new HashSet<>();

        pages.add(new Diarypage(null, LocalDate.now(), 1, "He shine like a diamond", Diarypage.MoodType.HAPPY));
        pages.add(new Diarypage(null, LocalDate.now().minusDays(1), 2, "Feeling magical", Diarypage.MoodType.IN_HEAVEN));

        return pages;
    }

    private static Set<Diarypage> getDiaryPagesForDiary2() {
        Set<Diarypage> pages = new HashSet<>();

        pages.add(new Diarypage(null, LocalDate.now(), 1, "First day at school", Diarypage.MoodType.IN_HEAVEN));
        pages.add(new Diarypage(null, LocalDate.now().minusDays(1), 2, "Overcoming challenges", Diarypage.MoodType.HAPPY));

        return pages;
    }

}
