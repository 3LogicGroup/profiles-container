package ru.graviton.profiles.dto.inventory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedfishInventoryPoint {
    private String name;
    private Integer orderNum;
    private String redfishPath;
    private boolean fromCollection;
    private String redfishParameter;
}
