package dk.lyngby.dao.impl;


import dk.lyngby.model.Diary;
import dk.lyngby.model.Diarypage;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PageDAO implements dk.lyngby.dao.IDao<Diarypage, Integer> {

    private static PageDAO instance;
    private static EntityManagerFactory emf;

    public static PageDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PageDAO();
        }
        return instance;
    }

    public Diary addPageToDiary(Integer diaryId, Diarypage page) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var diary = em.find(Diary.class, diaryId);
            diary.addPage(page);
            em.persist(page);
            Diary merge = em.merge(diary);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public Diarypage read(Integer integer) {
        try (var em = emf.createEntityManager()) {
            return em.find(Diarypage.class, integer);
        }
    }

    @Override
    public List<Diarypage> readAll() {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT p FROM Diarypage p", Diarypage.class);
            return query.getResultList();
        }
    }

    @Override
    public Diarypage create(Diarypage page) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(page);
            em.getTransaction().commit();
            return page;
        }
    }

    @Override
    public Diarypage update(Integer integer, Diarypage page) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            var p = em.find(Diarypage.class, integer);
            p.setPageNumber(page.getPageNumber());
            p.setDiarydate(page.getDiarydate());
            p.setPageText(page.getPageText());
            p.setMoodType(page.getMoodType());

            Diarypage merge = em.merge(p);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var page = em.find(Diarypage.class, integer);
            em.remove(page);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (var em = emf.createEntityManager()) {
            var page = em.find(Diarypage.class, integer);
            return page != null;
        }
    }

    public Function<Integer, Boolean> validateDiaryPageNumber = (pageNumber) -> {
        try (var em = emf.createEntityManager()) {
            var page = em.find(Diarypage.class, pageNumber);
            return page != null;
        }
    };

    public Boolean validateDiaryPageNumber(Integer pageNumber, Integer diaryId) {
        try (var em = emf.createEntityManager()) {
            var diary = em.find(Diary.class, diaryId);
            return diary.getPages().stream().anyMatch(r -> r.getPageNumber().equals(pageNumber));
        }
    }

}
