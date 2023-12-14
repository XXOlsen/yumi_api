package dk.lyngby.dto;

import dk.lyngby.model.Diarypage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
public class PageDTO {
    private LocalDate diarydate;
    private Integer pageNumber;
    private String pageText;
    private Diarypage.MoodType moodType;

    public PageDTO(Diarypage page) {
        this.diarydate = page.getDiarydate();
        this.pageNumber = page.getPageNumber();
        this.pageText = page.getPageText();
        this.moodType = page.getMoodType();
    }

    public static List<PageDTO> toPageDTOList(List<Diarypage> pages) {
        return List.of(pages.stream().map(PageDTO::new).toArray(PageDTO[]::new));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof PageDTO pageDto)) return false;

        if (getDiarydate() != null ? !getDiarydate().equals(pageDto.getDiarydate()) : pageDto.getDiarydate() != null)
            return false;
        if (getPageNumber() != null ? !getPageNumber().equals(pageDto.getPageNumber()) : pageDto.getPageNumber() != null)
            return false;
        if (getPageText() != null ? !getPageText().equals(pageDto.getPageText()) : pageDto.getPageText() != null)
            return false;
        return getMoodType() == pageDto.getMoodType();
    }

//    @Override
//    public int hashCode()
//    {
//        int result = getPageNumber() != null ? getPageNumber().hashCode() : 0;
//        result = 31 * result + (getRoomPrice() != null ? getRoomPrice().hashCode() : 0);
//        result = 31 * result + (getRoomType() != null ? getRoomType().hashCode() : 0);
//        return result;
//    }
}
