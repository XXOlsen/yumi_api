package dk.lyngby.controller.impl;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.controller.IController;
import dk.lyngby.dao.impl.DiaryDAO;
import dk.lyngby.dto.DiaryDTO;
import dk.lyngby.model.Diary;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DiaryController implements IController<Diary, Integer> {

    private final DiaryDAO dao;

    public DiaryController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = DiaryDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx)  {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        Diary diary = dao.read(id);
        // dto
        DiaryDTO diaryDto = new DiaryDTO(diary);
        // response
        ctx.res().setStatus(200);
        ctx.json(diaryDto, DiaryDTO.class);
    }

    @Override
    public void readAll(Context ctx) {
        // entity
        List<Diary> diaries = dao.readAll();
        // dto
        List<DiaryDTO> diaryDtos = DiaryDTO.toDiaryDTOList(diaries);
        // response
        ctx.res().setStatus(200);
        ctx.json(diaries, DiaryDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // request
        //Hotel jsonRequest = validateEntity(ctx);
        Diary jsonRequest = ctx.bodyAsClass(Diary.class);
        // entity
        Diary diary = dao.create(jsonRequest);
        // dto
        DiaryDTO diaryDto = new DiaryDTO(diary);
        // response
        ctx.res().setStatus(201);
        ctx.json(diaryDto, DiaryDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        Diary update = dao.update(id, validateEntity(ctx));
        // dto
        DiaryDTO diaryDto = new DiaryDTO(update);
        // response
        ctx.res().setStatus(200);
        ctx.json(diaryDto, Diary.class);
    }

    @Override
    public void delete(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        dao.delete(id);
        // response
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public Diary validateEntity(Context ctx) {
        return ctx.bodyValidator(Diary.class)
                .check( d -> d.getDiaryName() != null && !d.getDiaryName().isEmpty(), "Diary name must be set")
                .check( d -> d.getDiaryPage() != null, "Diary page must be set")
                .get();
    }

}
