package com.santanna.serviceorder.domain.common;

import com.santanna.serviceorder.domain.exception.DomainException;

import java.util.List;

public class PaginatedResult<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public PaginatedResult(List<T> content, int pageNumber, int pageSize, long totalElements) {
        if (pageNumber < 0) {
            throw new DomainException("The page number cannot be negative.");
        }
        if (pageSize <= 0) {
            throw new DomainException("Page size must be greater than zero.");
        }

        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

}
