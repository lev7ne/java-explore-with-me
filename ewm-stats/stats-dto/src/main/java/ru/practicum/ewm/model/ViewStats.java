package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStats {
    private String name;
    private String uri;
    private long hits;
}
