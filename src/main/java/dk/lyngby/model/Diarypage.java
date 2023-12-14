package dk.lyngby.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "diarypage")
public class Diarypage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id", nullable = false, unique = true)
    private Integer pageId;

    @Setter
    @Column(name = "diarypage_date", nullable = false)
    private LocalDate diarydate;

    @Setter
    @Column(name = "diarypage_number", nullable = false)
    private Integer pageNumber;

    @Setter
    @Column(name = "diarypage_text", nullable = false)
    private String pageText;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "mood_type", nullable = false)
    private MoodType moodType;

    @Setter
    @ManyToOne
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    public Diarypage(Integer pageId, LocalDate diarydate, Integer pageNumber, String pageText, MoodType moodType) {
        this.pageId = pageId;
        this.diarydate = diarydate;
        this.pageNumber = pageNumber;
        this.pageText = pageText;
        this.moodType = moodType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diarypage page = (Diarypage) o;
        return Objects.equals(pageId, page.pageId) && Objects.equals(diary, page.diary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageId, diary);
    }

    public enum MoodType {
        IN_HEAVEN, HAPPY, IN_BETWEEN, SAD, IN_HELL
    }
}