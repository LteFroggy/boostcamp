package com.example.backendproject.board.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.PrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.backendproject.board.elasticsearch.dto.BoardEsDocument;
import com.example.backendproject.board.elasticsearch.repository.BoardEsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardEsService {

    // ElasticSearch에 명령을 전달하는 공식 자바 API
    private final ElasticsearchClient elasticsearchClient;

    private final BoardEsRepository boardEsRepository;

    // 데이터를 저장하는 메서드
    @Transactional
    public void save(BoardEsDocument document) {
        boardEsRepository.save(document);
    }

    // 데이터 삭제 메서드
    public void deleteById(String id) {
        boardEsRepository.deleteById(id);
    }

    // 검색과 동시에 페이징을 수행하는 메서드. 페이지 번호와 페이지 크기를 받아 ES에서 검색한다
    // 검색된 정보는 Paging 정보와 함께 반환하도록 하기 위한 반환형
    public Page<BoardEsDocument> search(String keyword, int page, int size) {

        try {
            // ElasticSearch에서 페이징을 위한 시작 위치를 계산하는 변수
            int from = page * size;

            // ElasticSearch에서 사용할 검색 조건을 담는 쿼리
            Query query;

            // 키워드 입력 안했으면, 전체 문서를 불러오라는 쿼리
            if (keyword == null || keyword.isEmpty()) {
                // 전체 문서를 가져오는 쿼리를 생성하는 람다 함수
                query = MatchAllQuery.of(m -> m)._toQuery();
                // MatchAllQuery는 ES에서 조건 없이 모든 문서 검색 시에 사용하는 쿼리
            }

            // 검색어가 있을 시에는 검색을 수행하도록 한다.
            else {
                // BoolQuery는 복수 조건을 조합할 때 사용되는 쿼리
                // 이 쿼리 안에서 여러 개의 조건을 나열한다
                // 예를 들어, 백엔드라는 키워드가 들어왔을 때 이 키워드를 어떻게 분석해 데이터를 보여줄 것인가를 작성한다.
                query = BoolQuery.of(b -> {

                    // PrefixQuery는 접두어를 검색하는 쿼리.
                    // 이거 말고도 MatchQuery라고 해서, 값을 가지고 있으면 값을 높게 주는 것도 있다.
                    // Should는 true, false같은 게 아니라, 이걸 만족하면 값을 높게 줄게~ 에 가깝다.
                    // title Field에서 keyword라는 접두어를 찾는거라고 보면 되겠다.
                    b.should(PrefixQuery.of(p -> p.field("title").value(keyword))._toQuery());
                    // 마찬가지로 여기는 Content에서 접두어 찾기
                    b.should(PrefixQuery.of(p -> p.field("content").value(keyword))._toQuery());

                    /** Must : 모두 일치해야 함
                     * Should : 하나라도 일치하면 데이터를 보여줌 (Or)
                     * must_not 해당 조건을 만족하면 제외
                     * filter : Must와 같지만 점수 계산을 안함 (속도 빠름) */



                    return b;

                })._toQuery();
            }

            // 검색 조건을 위에서 다 작성했으므로 이제 요청을 보낼 시간
            // SearchRequest는 ElasticSearch에서 검색을 하기 위한 검색 요청 객체이다.
            // 인덱스명, 페이징 정보, 쿼리를 포함한 검색을 요청한다.
            SearchRequest request = SearchRequest.of(s -> s
                    .index("board_index")
                    .from(from)
                    .size(size)
                    .query(query)
            );

            // SearchResponse는 ElasticSearch의 검색 결과를 담고 있는 응답 객체
            SearchResponse<BoardEsDocument> response = elasticsearchClient.search(request, BoardEsDocument.class) ;
            // ElasticSearch에 명령을 전달하는 자바 API 검색요청을 담아서 응답객체로 반환


            // Elasticsearch 응답 객체(response)에서 검색된 결과들을 꺼냄 (hits는 검색된 문서 리스트를 의미)
            List<BoardEsDocument> content = response.hits() // 전체 검색 결과 중 hits 객체를 꺼냄
                .hits() // 개별 검색 결과들(Hit<BoardEsDocument>) 리스트 추출
                .stream() // 리스트를 스트림으로 변환
                .map(Hit::source) // 각 검색 결과에서 실제 문서 내용(BoardEsDocument)만 추출
                .collect(Collectors.toList()); // 추출한 내용을 리스트로 수집


            // 전체 검색 결과 수를 저장 (총 문서의 수)
            long total = response.hits().total().value();

            // PageImpl 객체를 사용해 Spring에서 사용할 수 있는 Page 객체로 변환한다.
            return new PageImpl<>(content, PageRequest.of(page, size), total);

        } catch (IOException e) {
            log.error("검색 오류 : {}", e.getMessage());
            throw new RuntimeException("검색 중 오류 발생 " + e);
        }
    }
}
