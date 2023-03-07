package com.codingrecipe.crboard.repository;

import com.codingrecipe.crboard.dto.CommentDTO;
import com.codingrecipe.crboard.entity.BoardEntity;
import com.codingrecipe.crboard.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // select * from comment_table where board_id = ? order by id desc;
    // 해당 메소드의 대소문자 구분을 포함한 naming-rule을 반드시 지켜야 error가 발생하지 않음
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);
}
