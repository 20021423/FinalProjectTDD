package org.quyetnt.finalprojecttdd.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject<T> {
    private String status;    // Ví dụ: "success" hoặc "error"
    private String message;
    private T data;           // Nếu không có dữ liệu, data sẽ là null
}