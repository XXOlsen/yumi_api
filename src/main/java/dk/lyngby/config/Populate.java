package dk.lyngby.config;


import dk.lyngby.model.Diary;
import dk.lyngby.model.Diarypage;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Set;

public class Populate {
    public static void main(String[] args) {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        Set<Diarypage> calPages = getCalPages();
        Set<Diarypage> hilPages = getHilPages();

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Diary ?? = new Diary("Hotel California", "California", Diary.HotelType.LUXURY);
            california.setRooms(calRooms);
            hilton.setRooms(hilRooms);
            em.persist(california);
            em.persist(hilton);
            em.getTransaction().commit();
        }
    }

    @NotNull
    private static Set<Page> getCalRooms() {
        Page r100 = new Page(100, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r101 = new Page(101, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r102 = new Page(102, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r103 = new Page(103, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r104 = new Page(104, new BigDecimal(3200), Page.RoomType.DOUBLE);
        Page r105 = new Page(105, new BigDecimal(4500), Page.RoomType.SUITE);

        Page[] roomArray = {r100, r101, r102, r103, r104, r105};
        return Set.of(roomArray);
    }

    @NotNull
    private static Set<Page> getHilRooms() {
        Page r111 = new Page(111, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r112 = new Page(112, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r113 = new Page(113, new BigDecimal(2520), Page.RoomType.SINGLE);
        Page r114 = new Page(114, new BigDecimal(2520), Page.RoomType.DOUBLE);
        Page r115 = new Page(115, new BigDecimal(3200), Page.RoomType.DOUBLE);
        Page r116 = new Page(116, new BigDecimal(4500), Page.RoomType.SUITE);

        Page[] roomArray = {r111, r112, r113, r114, r115, r116};
        return Set.of(roomArray);
    }
}
