package com.wellcom.domain.SharingRoom;

import com.wellcom.domain.Item.Item;
import com.wellcom.domain.Item.ItemStatus;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Record.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="sharing_room")
public class SharingRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch=FetchType.LAZY)
    private Member member;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 500)
    private String contents;

    @Builder.Default
    private int curPeople=0;

    @Column(nullable = false)
    private int cntPeople;

    @OneToMany(mappedBy ="sharingroom")
    @Builder.Default
    private List<Record> records = new ArrayList<>();

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    private Item item;

    @Builder.Default
    private String delYn="N";

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime updatedTime;

    public void updateSharingRoom(String title, String contents, int cntPeople){
        this.title = title;
        this.contents = contents;
        this.cntPeople = cntPeople;
    }
    public void deleteSharingRoom() {
        this.delYn = "Y";
    }
}
