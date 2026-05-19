package com.crm.demo.controller.api;

import com.crm.demo.controller.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Contacts")
public interface ContactApi {

    @Operation(summary = "Get contacts", description = "Returns contacts filtered by active state.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contacts returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContactResponse.class)))
            )
    })
    @GetMapping
    List<ContactResponse> getAll(
            @Parameter(description = "Whether to return active or inactive contacts", required = true)
            @RequestParam boolean active);

    @Operation(summary = "Get contact by id", description = "Returns a single contact by UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contact found",
                    content = @Content(schema = @Schema(implementation = ContactResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Contact not found")
    })

    @GetMapping("/{id}")
    ContactResponse getById(
            @Parameter(description = "Contact UUID", required = true)
            @PathVariable @NotNull UUID id);

    @Operation(summary = "Get contact activities", description = "Returns all activities for a contact.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Activities returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityResponse.class)))
            ),
            @ApiResponse(responseCode = "404", description = "Contact not found")
    })
    @GetMapping("/{id}/activities")
    List<ActivityResponse> getContactActivities(
            @Parameter(description = "Contact UUID", required = true)
            @PathVariable @NotNull UUID id);

    @Operation(summary = "Get contact activity statistics", description = "Returns aggregated activity counts for a contact.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Activity statistics returned",
                    content = @Content(schema = @Schema(implementation = ActivityStatisticResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Contact not found")
    })
    @GetMapping("/{id}/statistics")
    ActivityStatisticResponse getContactStatistics(
            @Parameter(description = "Contact UUID", required = true)
            @PathVariable @NotNull UUID id);

    @Operation(summary = "Create contact activity", description = "Creates a new activity for a contact.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Activity created",
                    content = @Content(schema = @Schema(implementation = ActivityResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "404", description = "Contact not found")
    })
    @PostMapping("/{id}/activities")
    ResponseEntity<ActivityResponse> createActivity(
            @Parameter(description = "Contact UUID", required = true)
            @PathVariable @NotNull UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Activity data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ActivityRequest.class))
            )
            @NotNull @Valid @RequestBody ActivityRequest request);

    @Operation(summary = "Create contact", description = "Creates a new contact.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Contact created",
                    content = @Content(schema = @Schema(implementation = ContactResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @PostMapping
    ResponseEntity<ContactResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Contact data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUpdateContactRequest.class))
            )
            @RequestBody @NotNull @Valid CreateUpdateContactRequest request);

    @Operation(summary = "Update contact", description = "Updates an existing contact.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contact updated",
                    content = @Content(schema = @Schema(implementation = ContactResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "404", description = "Contact not found")
    })
    @PutMapping("/{id}")
    ResponseEntity<ContactResponse> update(
            @Parameter(description = "Contact UUID", required = true)
            @PathVariable @NotNull UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated contact data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUpdateContactRequest.class))
            )
            @RequestBody @NotNull @Valid CreateUpdateContactRequest request);

    @Operation(summary = "Deactivate contact", description = "Deactivates an existing contact by changing of activate flag.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contact deactivated"),
            @ApiResponse(responseCode = "404", description = "Contact not found")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deactivate(
            @Parameter(description = "Contact UUID", required = true)
            @PathVariable @NotNull UUID id);
}
