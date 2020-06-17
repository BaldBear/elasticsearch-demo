package com.erving.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author erving
 * @date 2020/6/11
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;
    private String age;
    private String message;
}
