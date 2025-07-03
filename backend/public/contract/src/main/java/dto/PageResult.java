package dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> content;
    private int number;         // 현재 페이지 번호
    private int size;           // 페이지 사이즈
    private long totalElements; // 전체 요소 수
    private int totalPages;     // 전체 페이지 수
    private boolean first;      // 첫 페이지 여부
    private boolean last;       // 마지막 페이지 여부
}