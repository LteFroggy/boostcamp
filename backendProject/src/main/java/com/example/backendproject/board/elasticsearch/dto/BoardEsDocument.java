package com.example.backendproject.board.elasticsearch.dto;

import com.example.backendproject.board.dto.BoardDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

// 기본적으로 ES에서는 뭘 저장하면 저장한 패키지명이 같이 들어가는데, 그걸 막기 위한 어노테이션
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "board-index")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEsDocument {

    @Id
    private String id;
    private String title;
    private String content;
    private String username;
    private Long userId;
    private String created_date;
    private String updated_date;


    // BoardDTO를 Elastic 전용 DTO로 변환하는 메서드
    public static BoardEsDocument from(BoardDTO dto) {
        /// from은 static Factory Method이다.
        /// from...은 일반적으로 다른 객체를 받아서 변환하는 목적으로 사용된다
        /// 반환 타입은 BoardEsDocument, 즉 다시 말해 BoardDTO를 받아 BoardEsDocument로 만들겠다고 한 것이다.
        /// 이런 이름을 사용한 이유는, 변환 로직을 명시적으로 드러내주기 위함이다. "다른 타입에서부터 만들어진다"는 의미를 명확히 전달하기 위한 것.

        // BoardDTO를 받아서 ES DTO를 반환한다.
        return BoardEsDocument.builder()
                .id(String.valueOf(dto.getId()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .username(dto.getUsername())
                .userId(dto.getUser_id())
                .created_date(dto.getCreated_date() != null ? dto.getCreated_date().toString() : null)
                .updated_date(dto.getUpdated_date() != null ? dto.getUpdated_date().toString() : null)
                .build();
    }

}
