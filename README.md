# 개발 환경
----------------------------------------------------
1. IDE : IntelliJ IDEA Community
2. Spring Boot 2.7.9
3. JDK11
4. Maria DB
5. Spring Data JPA
6. Thymeleaf
7. lombok

# 게시판 주요 기능
----------------------------------------------------
1. 글 쓰기(/board/write)
2. 글 목록(/board/)
3. 글 조회(/board/{id})
4. 글 수정(/board/update/{id})
   - 상세 화면에서 수정 버튼 클릭
   - 서버에서 해당 게시글의 정보를 가지고 수정 화면 출력
   - 제목, 내용 수정 입력받아서 서버로 요청
   - 수정 처리
5. 글 삭제(/board/delete/{id})
6. 페이징 처리(/board/paging)
   - /board/paging?page=2
   - /board/paging/2
   - 게시글 14
     - 한페이지에 5개씩 display => paging 수 = 3
     - 한페이지에 3개씩 display => paging 수 = 5
7. 파일 첨부하기
   - 단일 파일 첨부
   - 다중 파일 첨부
   - 파일 첨부와 관련하여 추가될 부분들
     - save.html
     - BoardDTO
     - BoardService.save()
     - BoardEntity
     - BoardEntity, BoardFileRepository 추가
     - detail.html
   - github에 올려놓은 소스를 보고 어떤 부분이 바뀌는지 잘 살펴보세요.
   - board_table(부모) - board_file_table(자식)
   - create table board_table
     (
     id             bigint auto_increment primary key,
     created_time   datetime     null,
     updated_time   datetime     null,
     board_contents varchar(500) null,
     board_hits     int          null,
     board_pass     varchar(255) null,
     board_title    varchar(255) null,
     board_writer   varchar(20)  not null,
     file_attached  int          null
     );
   - create table board_file_table
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
8. 댓글 처리하기
   - 글 상세 페이지에서 댓글 입력(ajax)
     - ajax 다뤄보기 재생 목록
   - 상세 조회할 때 기존에 작성된 댓글 목록이 보임
   - 댓글을 입력하면 기존 댓글 목록에 새로 작성한 댓글 추가
   - 댓글용 테이블 필요

# mysql Database 계정 생성 및 권한 부여
----------------------------------------------------
create database db_codingrecipe;
create user user_codingrecipe@localhost indentified by '1234';
grant all privileges on db_codingrecipe.* to user_codingrecipe@localhost;