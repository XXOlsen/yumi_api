package dk.lyngby.controller.impl;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.controller.IController;
import dk.lyngby.dao.impl.PageDAO;
import dk.lyngby.dto.DiaryDTO;
import dk.lyngby.dto.PageDTO;
import dk.lyngby.exception.Message;
import dk.lyngby.model.Diary;
import dk.lyngby.model.Diarypage;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.function.BiFunction;

public class PageController implements IController<Diarypage, Integer> {

    private PageDAO dao;

    public PageController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = PageDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // entity
        Diarypage page = dao.read(id);
        // dto
        PageDTO pageDto = new PageDTO(page);
        // response
        ctx.res().setStatus(200);
        ctx.json(pageDto, PageDTO.class);

    }

    @Override
    public void readAll(Context ctx) {
        // entity
        List<Diarypage> pages = dao.readAll();
        // dto
        List<PageDTO> pageDtos = PageDTO.toPageDTOList(pages);
        // response
        ctx.res().setStatus(200);
        ctx.json(pageDtos, PageDTO.class);

    }

    @Override
    public void create(Context ctx) {
        // request
        Diarypage jsonRequest = validateEntity(ctx);

        int diaryId = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        Boolean hasPage = validateDiaryPageNumber.apply(jsonRequest.getPageNumber(), diaryId);

        if (hasPage) {
            ctx.res().setStatus(400);
            ctx.json(new Message(400, "Page number already in use by diary"));
            return;
        }

        // entity
        Diary diary = dao.addPageToDiary(diaryId, jsonRequest);
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
        Diarypage update = dao.update(id, validateEntity(ctx));
        // dto
        PageDTO pageDto = new PageDTO(update);
        // response
        ctx.res().setStatus(200);
        ctx.json(pageDto, PageDTO.class);
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
    public boolean validatePrimaryKey(Integer integer) {return dao.validatePrimaryKey(integer);}

    // Checks if the room number is already in use by the hotel
    BiFunction<Integer, Integer, Boolean> validateDiaryPageNumber = (pageNumber, diaryId) -> dao.validateDiaryPageNumber(pageNumber, diaryId);

    @Override
    public Diarypage validateEntity(Context ctx) {
        return ctx.bodyValidator(Diarypage.class)
                .check(p -> p.getDiarydate() != null , "Not a valid date")
                .check(p -> p.getPageNumber() != null && p.getPageNumber() > 0, "Not a valid page number")
                .check(p -> p.getMoodType() != null, "Not a valid mood type")
                .get();
    }
}
