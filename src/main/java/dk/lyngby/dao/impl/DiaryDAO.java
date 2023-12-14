package dk.lyngby.dao.impl;

import dk.lyngby.model.Diary;
import dk.lyngby.model.Diarypage;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DiaryDAO implements dk.lyngby.dao.IDao<Diary, Integer> {

    private static DiaryDAO instance;
    private static EntityManagerFactory emf;

    public static DiaryDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new DiaryDAO();
        }
        return instance;
    }

    @Override
    public Diary read(Integer integer) {
       try (var em = emf.createEntityManager())
       {
           return em.find(Diary.class, integer);
       }
    }

    @Override
    public List<Diary> readAll() {
        try (var em = emf.createEntityManager())
        {
            var query = em.createQuery("SELECT d FROM Diary d", Diary.class);
            return query.getResultList();
        }
    }

    @Override
    public Diary create(Diary diary) {
        try (var em = emf.createEntityManager())
        {
            em.getTransaction().begin();
            em.persist(diary);
            em.getTransaction().commit();
            return diary;
        }
    }

    @Override
    public Diary update(Integer integer, Diary diary) {
        try(var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            var d = em.find(Diary.class, integer);
            d.setDiaryName(diary.getDiaryName());
            d.setDiaryPage(diary.getDiaryPage());
            Diary merge = em.merge(d);
            em.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public void delete(Integer integer) {
        try(var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var diary = em.find(Diary.class, integer);
            em.remove(diary);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try(var em = emf.createEntityManager()) {
            var person = em.find(Diary.class, integer);
            return person != null;
        }
    }
}
