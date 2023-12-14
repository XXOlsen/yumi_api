package dk.lyngby.dto;

import dk.lyngby.model.Diary;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class DiaryDTO {

    private Integer diaryId;
    private String diaryName;
    private Integer diaryPage;
    private Set<PageDTO> pages = new HashSet<>();

    public DiaryDTO(Diary diary) {
        this.diaryId = diary.getDiaryId();
        this.diaryName = diary.getDiaryName();
        this.diaryPage = diary.getDiaryPage();
        if (diary.getPages() != null)
        {
            diary.getPages().forEach( page -> pages.add(new PageDTO(page)));
        }
    }

    public DiaryDTO(String diaryName, Integer diaryPage) {
        this.diaryName = diaryName;
        this.diaryPage = diaryPage;
    }

    public static List<DiaryDTO> toDiaryDTOList(List<Diary> diaries) {
        return diaries.stream().map(DiaryDTO::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof DiaryDTO diaryDto)) return false;

        return getDiaryId().equals(diaryDto.getDiaryId());
    }

    @Override
    public int hashCode()
    {
        return getDiaryId().hashCode();
    }

}
