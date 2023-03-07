package com.codingrecipe.crboard.dto;

import com.codingrecipe.crboard.entity.BoardEntity;
import com.codingrecipe.crboard.entity.BoardFileEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// DTO(Data Transfer Object), VO, Bean...(단, Entity 클래스와는 목적이 다름)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardDTO {
    private Long id;
    private String boardWriter;
    private String boardPass;
    private String boardTitle;
    private String boardContents;
    private int boardHits;
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;
    
    // Multi file 첨부를 위해 List 사용
    private List<MultipartFile> boardFile;    // save.html -> Controller 파일 담는 용도
    private List<String> originalFileName;    // 파일의 원본 이름
    private List<String> storedFileName;      // 서버에 저장되는 파일 이름
    private int fileAttached;          // 파일 첨부여부(첨부:1, 미첨부:0)
    
    // 게시글 paging 목록
    public BoardDTO(Long id, String boardWriter, String boardTitle, int boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());

        if(boardEntity.getFileAttached() == 0) {
            boardDTO.setFileAttached(0);    // 0
        } else {
            // 파일 이름을 가져가야 함
            // originalFileName, storedFileName : board_file_table(BoardFileEntity)
            // join : select * from board_table a, board_file_table b where a.id = b.board_id and a.id = ?
            // boardEntity에서 자식 객체인 boardFileEntity의 originalFileName과 storedFileName에 접근
            /* Single 파일 첨부시 처리 내역
             * boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
             * boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
             */
            // Multi File 첨부 처리 내역
            List<String> originalFileNameList = new ArrayList<String>();
            List<String> storedFileNameList = new ArrayList<String>();
            boardDTO.setFileAttached(boardEntity.getFileAttached());    // 1

            for(BoardFileEntity boardFileEntity : boardEntity.getBoardFileEntityList()) {
                originalFileNameList.add(boardFileEntity.getOriginalFileName());
                storedFileNameList.add(boardFileEntity.getStoredFileName());
            }
            boardDTO.setOriginalFileName(originalFileNameList);
            boardDTO.setStoredFileName(storedFileNameList);


        }

        return boardDTO;
    }


}
