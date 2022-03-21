package com.dragonslair.bcintredux.model;

import com.dragonslair.bcintredux.enums.OperationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AutomationOperation {

    private OperationStatus status = OperationStatus.NOT_STARTED;
    private String message = "";
}
