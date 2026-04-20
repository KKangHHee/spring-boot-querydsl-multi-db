package com.example.demo.global.response;

import java.util.List;
import org.springframework.data.domain.Page;

public class CommonResponse {

  public record ApiResponse<T>(boolean success, String message, T data) {

    private static <T> ApiResponse<T> of(boolean success, String message, T data) {
      return new ApiResponse<>(success, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
      return of(true, message, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
      return of(true, message, data);
    }

    public static <T> ApiResponse<T> fail(String message) {
      return of(false, message, null);
    }
  }

  public record PageResponse<T>
      (
          List<T> content,
          int page,
          int size,
          long totalElements,
          int totalPages,
          boolean last
      ) {

    public static <T> PageResponse<T> of(Page<T> page) {
      return new PageResponse<>(
          page.getContent(),
          page.getNumber(),
          page.getSize(),
          page.getTotalElements(),
          page.getTotalPages(),
          page.isLast()
      );
    }
  }
}
