package ru.graviton.profiles.dto.inventory;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RedfishInventoryGroup {
    private String groupName;
    private Integer orderNum;
    private List<RedfishInventoryPoint>  inventoryPoints;
}
