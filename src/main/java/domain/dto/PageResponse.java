package domain.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
@RegisterForReflection
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
