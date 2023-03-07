package com.codingrecipe.crboard.service;

import com.codingrecipe.crboard.dto.BoardDTO;
import com.codingrecipe.crboard.entity.BoardEntity;
import com.codingrecipe.crboard.entity.BoardFileEntity;
import com.codingrecipe.crboard.repository.BoardFileRepository;
import com.codingrecipe.crboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    // private final로 선언하지 않으면 auto-increment로 정의한 id가 생성되지 않는 오류가 발행하는 것을 알았음(2022/03/02)
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public void save(BoardDTO boardDTO) throws IOException {
        // 파일 첨부 여부에 따라 로직 분리
        if(boardDTO.getBoardFile().isEmpty()) {
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            /*  Single File 첨부시 프로세스
             *  1. DTO에 담긴 파일을 꺼냄
             *  2. 파일의 이름 가져옴
             *  3. 서버 저장용 이름으로 변경 
             *  4. 저장 경로 설정
             *  5. 해당 경로에 파일 저장
             *  6. board_table에 해당 데이터 save 처리
             *  7. board_file_table에 해당 데이터 save 처리
             */
            // Multi file 저장을 위해서는 부모 BoardEntity 저장이 선행되어야 하기 때문에 앞으로 이동함
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long saveId = boardRepository.save(boardEntity).getId();    // 6.
            // 파일을 첨부한 부모엔티티의 id를 통해 이를 BoardEntity 객체로 생성함
            BoardEntity board = boardRepository.findById(saveId).get();

            // MultipartFile boardFile = boardDTO.getBoardFile();  // 1. --> Single File 첨부시
            // Multi File 첨부시, for loop를 통해 MultipartFile 형의 List 객체에 담긴 객체를 저장함
            for(MultipartFile boardFile : boardDTO.getBoardFile()) {
                String originalFileName = boardFile.getOriginalFilename();  // 2.
                String storedFileName = System.currentTimeMillis() + "_" + originalFileName;    // 3.
                String savePath ="C:/springboot_img/" + storedFileName; // 4.
                boardFile.transferTo(new File(savePath));   // 5.

                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(originalFileName, storedFileName, board);
                boardFileRepository.save(boardFileEntity);  // 7.
            }
        }

    }

    // boardEntity를 통해 자식 클래스인 boardFileEntity에 접근하고 있으므로, 해당 annotation을 반드시 붙여야 함
    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<BoardDTO>();
        for(BoardEntity boardEntity: boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    // 해당 메소드를 통해 DB Transaction을 처리하는 동안, error가 발생하게 되면 전체를 rollback시킴
    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }
    
    // boardEntity를 통해 자식 클래스인 boardFileEntity에 접근하고 있으므로, 해당 annotation을 반드시 붙여야 함
    @Transactional
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if(optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            // toBoardDTO를 통해 자식 객체인 boardFileEntity에 접근하고 있음
            return BoardDTO.toBoardDTO(boardEntity);
        } else {
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateBoard(boardDTO);
        // toUpdateBoard() 메소드를 통해 id값이 존재하는 것을 인지하고 해당 id정보를 Update함
        boardRepository.save(boardEntity);

        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3;  // 한 페이지에 보여줄 게시글 개수
        // 한 페이지당 3개씩 글을 보여주고 id 기준으로 내림차순 정렬
        // page 위치에 있는 값은 0부터 시작
        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부
        // 목록 : id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle(), board.getBoardHits(),board.getCreatedTime()));
        return boardDTOS;
    }
}
