package com.example.backendproject.board.elasticsearch.repository;

import com.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BoardEsRepository extends ElasticsearchRepository<BoardEsDocument, String> {

    // 문서 ID 기반으로 데이터 삭제하는 메서드
    void deleteById(String id);

}
