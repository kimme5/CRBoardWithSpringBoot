package com.codingrecipe.crboard.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;

/*
create table board_file_table
     (
     id                 bigint auto_increment primary key,
     created_time       datetime     null,
     updated_time       datetime     null,
     original_file_name varchar(255) null,
     stored_file_name   varchar(255) null,
     board_id           bigint       null,
     constraint FKcfxqly70ddd02xbou0jxgh4o3
     foreign key (board_id) references board_table (id) on delete cascade
     );
 */

@Entity
@Getter
@Setter
@Table(name="board_file_table")
public class BoardFileEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;
    
    // ManyToOne : 하나의 게시글에 여러개의 파일 첨부 가능
    // FetchType.EAGER : 부모 엔티티를 조회하는 경우, 자식 엔티티의 모든 데이터를 동시에 가져옴
    // FetchType.LAZY : 자식 엔티티의 정보를 추후에 필요에 따라 호출하여 사용
    // alter table board_file_table add constraint FKcfxqly70ddd02xbou0jxgh4o3 foreign key (board_id) references board_table (id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    public static BoardFileEntity toBoardFileEntity(String originalFileName, String storedFileName, BoardEntity boardEntity) throws IOException {
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setOriginalFileName(originalFileName);
        boardFileEntity.setStoredFileName(storedFileName);
        boardFileEntity.setBoardEntity(boardEntity);
        return boardFileEntity;
    }
}
