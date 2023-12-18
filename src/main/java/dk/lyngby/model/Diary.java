package dk.lyngby.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "diary")
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id", nullable = false, unique = true)
    private Integer diaryId;

    @Setter
    @Column(name = "diary_name", nullable = false, unique = true)
    private String diaryName;

    @Setter
    @Column(name = "diary_page", nullable = false)
    private Integer diaryPage;

    @OneToOne(mappedBy = "diary", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "diary", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Diarypage> pages = new HashSet<>();

    public Diary(Integer diaryId, String diaryName, Integer diaryPage) {
        this.diaryId = diaryId;
        this.diaryName = diaryName;
        this.diaryPage = diaryPage;
    }

    public Diary(String diaryName, Integer diaryPage) {
        this.diaryName = diaryName;
        this.diaryPage = diaryPage;
    }

    public void setPages(Set<Diarypage> pages) {
        if(pages != null) {
            this.pages = pages;
            for (Diarypage page : pages) {
                page.setDiary(this);
            }
        }
    }

    public void addPage(Diarypage page) {
        if ( page != null) {
            this.pages.add(page);
            page.setDiary(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diary diary = (Diary) o;
        return Objects.equals(diaryName, diary.diaryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diaryName);
    }

}
