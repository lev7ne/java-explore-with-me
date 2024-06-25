package ru.ewm.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}
