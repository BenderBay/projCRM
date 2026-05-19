package com.crm.demo.controller.api;

import com.crm.demo.controller.dto.ActivityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Activities")
public interface ActivityApi {

    @Operation(summary = "Get activity by id", description = "Returns a single activity by UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Activity found",
                    content = @Content(schema = @Schema(implementation = ActivityResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    @GetMapping("/{id}")
    ActivityResponse getById(
            @Parameter(description = "Activity UUID", required = true)
            @PathVariable @NotNull UUID id);
}
