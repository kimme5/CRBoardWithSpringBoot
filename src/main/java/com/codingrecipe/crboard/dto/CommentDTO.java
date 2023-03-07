package com.codingrecipe.crboard.dto;

import com.codingrecipe.crboard.entity.CommentEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDTO {
    private Long id;
    private String commentWriter;
    private String commentContents;
    private Long boardId;
    private LocalDateTime commentCreatedTime;

    public static CommentDTO toCommentDTO(CommentEntity commentEntity, Long boardId) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentEntity.getId());
        commentDTO.setCommentWriter(commentEntity.getCommentWriter());
        commentDTO.setCommentContents(commentEntity.getCommentContents());
        // parameter로 boardId를 받지 않는 경우에는 아래와 같이 boardId를 얻어올 수 있음
        // commentDTO.setBoardId(commentEntity.getBoardEntity().getId());
        commentDTO.setBoardId(boardId);
        return commentDTO;
    }
}
