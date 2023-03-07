package com.codingrecipe.crboard.controller;

import com.codingrecipe.crboard.dto.BoardDTO;
import com.codingrecipe.crboard.dto.CommentDTO;
import com.codingrecipe.crboard.service.BoardService;
import com.codingrecipe.crboard.service.CommentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO: " + boardDTO);
        boardService.save(boardDTO);
        return "index";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id
                         , Model model
                         , @PageableDefault(page=1) Pageable pageable) {
        /*
         * 해당 게시글의 조회수를 1 올리고
         * 게시글 데이터를 가져와서 detail.html에 출력
         */
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);

        /* 댓글 목록 가져오기 */
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList", commentDTOList);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("updateBoard", boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO upBoard = boardService.update(boardDTO);
        model.addAttribute("board", upBoard);
        return "detail";
//        return "redirect:/board/" + boardDTO.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    // /board/paging?page=1
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
//        pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);
        // page 개수가 20개
        // page size = 3이고, 현재 사용자가 3페이지를 조회
        // 1 2 3
        // 현재 사용자가 7페이지를 조회
        // 7 8 9
        int blockLimit = 3; // 화면에 보여주는 페이징 수
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 13 16...
//        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit -1 : boardList.getTotalPages(); // 3 6 9 12 15 18...
        int endPage = Math.min((startPage + blockLimit - 1), boardList.getTotalPages()); // 3 6 9 12 15 18...

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "paging";
    }


}
