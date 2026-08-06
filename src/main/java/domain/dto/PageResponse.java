package domain.dto;

import java.util.List;

public class PageResponse<T> {
    public List<T> content;
    public long totalElements;
    public int totalPages;
    public int page;
    public int size;

    public PageResponse() {}

    public PageResponse(List<T> content, long totalElements, int totalPages, int page, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
    }
}
