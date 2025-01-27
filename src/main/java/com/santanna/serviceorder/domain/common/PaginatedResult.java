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
            throw new DomainException("O número da página não pode ser negativo.");
        }
        if (pageSize <= 0) {
            throw new DomainException("O tamanho da página deve ser maior que zero.");
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

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
