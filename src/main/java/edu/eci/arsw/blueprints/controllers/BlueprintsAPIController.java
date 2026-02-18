package edu.eci.arsw.blueprints.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) {
        this.services = services;
    }

    @Operation(summary = "Get all blueprints", description = "Returns every blueprint stored in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Blueprints retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponseLAB<Set<Blueprint>>> getAll() {
        Set<Blueprint> blueprints = services.getAllBlueprints();
        return ResponseEntity.ok(ApiResponseLAB.ok(blueprints));
    }

    @Operation(summary = "Get blueprints by author", description = "Returns all blueprints belonging to a given author")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Blueprints found"),
            @ApiResponse(responseCode = "404", description = "No blueprints found for the author")
    })
    @GetMapping("/{author}")
    public ResponseEntity<ApiResponseLAB<Set<Blueprint>>> byAuthor(@PathVariable String author)
            throws BlueprintNotFoundException {
        Set<Blueprint> blueprints = services.getBlueprintsByAuthor(author);
        return ResponseEntity.ok(ApiResponseLAB.ok(blueprints));
    }

    @Operation(summary = "Get a specific blueprint", description = "Returns a single blueprint by author and name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Blueprint found"),
            @ApiResponse(responseCode = "404", description = "Blueprint not found")
    })
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<ApiResponseLAB<Blueprint>> byAuthorAndName(
            @PathVariable String author, @PathVariable String bpname) throws BlueprintNotFoundException {
        Blueprint bp = services.getBlueprint(author, bpname);
        return ResponseEntity.ok(ApiResponseLAB.ok(bp));
    }

    @Operation(summary = "Create a new blueprint", description = "Persists a new blueprint with author, name and points")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Blueprint created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<ApiResponseLAB<Blueprint>> add(@Valid @RequestBody NewBlueprintRequest req)
            throws BlueprintPersistenceException {
        Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
        services.addNewBlueprint(bp);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseLAB.created(bp));
    }

    @Operation(summary = "Add a point to a blueprint", description = "Appends a new point to an existing blueprint")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Point added successfully"),
            @ApiResponse(responseCode = "404", description = "Blueprint not found")
    })
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<ApiResponseLAB<Void>> addPoint(
            @PathVariable String author, @PathVariable String bpname,
            @RequestBody Point p) throws BlueprintNotFoundException {
        services.addPoint(author, bpname, p.x(), p.y());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponseLAB.accepted(null));
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid List<Point> points
    ) {}
}
