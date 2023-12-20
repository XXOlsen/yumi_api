package dk.lyngby.config;


import dk.lyngby.model.Diary;
import dk.lyngby.model.Diarypage;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Populate {


    public static void main(String[] args) {

        dropTables(); // Call method to drop tables

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            User user1 = new User("user1", "user1@example.com", "1234");
            User user2 = new User("user2", "user2@example.com", "1234");
            User admin = new User("admin", "admin@example.com", "4321");

            Role roleUser = new Role("ROLE_USER");
            Role roleAdmin = new Role("ROLE_ADMIN");

            Diary diaryToUser1 = new Diary("About Stefan", 150);
            Diary diaryToUser2 = new Diary("School Time", 150);

            em.persist(user1);
            em.persist(user2);
            em.persist(admin);

            em.persist(roleUser);
            em.persist(roleAdmin);

            em.persist(diaryToUser1);
            em.persist(diaryToUser2);

            user1.addRole(roleUser);
            user2.addRole(roleUser);
            admin.addRole(roleAdmin);

            user1.addDiary(diaryToUser1);
            user2.addDiary(diaryToUser2);

            Set<Diarypage> diary1Pages = getDiaryPagesForDiary1();
            diaryToUser1.setPages(diary1Pages);

            Set<Diarypage> diary2Pages = getDiaryPagesForDiary2();
            diaryToUser2.setPages(diary2Pages);

            em.getTransaction().commit();
        }
    }

    private static Set<Diarypage> getDiaryPagesForDiary1() {
        Set<Diarypage> pages = new HashSet<>();

        pages.add(new Diarypage(null, LocalDate.now(), 1, "He shines like a diamond", Diarypage.MoodType.HAPPY));
        pages.add(new Diarypage(null, LocalDate.now().minusDays(1), 2, "Feeling magical", Diarypage.MoodType.IN_HEAVEN));

        return pages;
    }

    private static Set<Diarypage> getDiaryPagesForDiary2() {
        Set<Diarypage> pages = new HashSet<>();

        pages.add(new Diarypage(null, LocalDate.now(), 1, "First day at school", Diarypage.MoodType.IN_HEAVEN));
        pages.add(new Diarypage(null, LocalDate.now().minusDays(1), 2, "Overcoming challenges", Diarypage.MoodType.HAPPY));

        return pages;
    }

    private static void dropTables() {
        String url = "jdbc:postgresql://localhost:5432/diary";
        String user = "postgres";
        String password = "postgres";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement statement = connection.createStatement();

            // Drop tables
            statement.executeUpdate("DROP TABLE IF EXISTS user_roles");
            statement.executeUpdate("DROP TABLE IF EXISTS users");
            statement.executeUpdate("DROP TABLE IF EXISTS roles");
            statement.executeUpdate("DROP TABLE IF EXISTS diarypage");
            statement.executeUpdate("DROP TABLE IF EXISTS diary");

            System.out.println("Tables dropped successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

