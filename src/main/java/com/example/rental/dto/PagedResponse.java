package com.example.rental.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PagedResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PagedResponse<T> from(Page<T> result) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setItems(result.getContent());
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalItems(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());
        response.setHasNext(result.hasNext());
        response.setHasPrevious(result.hasPrevious());
        return response;
    }

    public List<T> getItems() { return items; }
    public void setItems(List<T> items) { this.items = items; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }

    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}
