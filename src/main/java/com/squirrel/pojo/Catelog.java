package com.squirrel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog {
    private Integer id;

    private String name;

    private Integer number;

    private Byte status;
}
