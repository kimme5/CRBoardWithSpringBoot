package com.codingrecipe.crboard.service;

import com.codingrecipe.crboard.dto.CommentDTO;
import com.codingrecipe.crboard.entity.BoardEntity;
import com.codingrecipe.crboard.entity.CommentEntity;
import com.codingrecipe.crboard.repository.BoardRepository;
import com.codingrecipe.crboard.repository.CommentRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService  {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public Long save(CommentDTO commentDTO) {
        // 부모엔티티(BoardEntity) 조회 */
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(commentDTO.getBoardId());
        if(optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO, boardEntity);
            return commentRepository.save(commentEntity).getId();
        } else {
            return null;
        }
    }
    
    // 특정 게시글의 댓글 목록 전체 가져오기
    public List<CommentDTO> findAll(Long boardId) {
        // 댓글 전체를 가져올 게시글 id를 통해 게시글내의 정보들을 얻어와 boardEntity에 담음
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        // commentRepository에 추가로 정의한 메소드를 통해 해당 게시글에 달린 전체 댓글 정보를 담아 List에 담음
        List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        /* EntityList -> DTOList로 변환하여 담을 commentDTOList 객체 생성 */
        List<CommentDTO> commentDTOList = new ArrayList<>();
        // for loop를 통해 commentEntityList에 담겨있는 댓글 정보들을 toCommentDTO 메소드를 통해 하나씩 commentDTO 객체로 변환한 후 담음
        for (CommentEntity commentEntity : commentEntityList) {
            CommentDTO commentDTO = CommentDTO.toCommentDTO(commentEntity, boardId);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }
}
