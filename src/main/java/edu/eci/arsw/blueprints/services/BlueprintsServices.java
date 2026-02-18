package edu.eci.arsw.blueprints.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import edu.eci.arsw.blueprints.filters.BlueprintsFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.PostgresBlueprintPersistence;

@Service
public class BlueprintsServices {

    private final PostgresBlueprintPersistence persistence;
    private final BlueprintsFilter filter;

    public BlueprintsServices(BlueprintsFilter filter, PostgresBlueprintPersistence persistence) {
        this.filter = filter;
        this.persistence = persistence;
    }

    public void addNewBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (persistence.findByAuthorAndName(bp.getAuthor(), bp.getName()).isPresent()) {
            throw new BlueprintPersistenceException(
                    "Blueprint already exists: " + bp.getAuthor() + "/" + bp.getName());
        }
        persistence.save(bp);
    }

    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(persistence.findAll());
    }

    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> blueprints = persistence.findByAuthor(author);
        if (blueprints.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints for author: " + author);
        }
        return blueprints;
    }

    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        Blueprint bp = persistence.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException(
                        "Blueprint not found: " + author + "/" + name));
        return filter.apply(bp);
    }

    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        Blueprint bp = persistence.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException(
                        "Blueprint not found: " + author + "/" + name));
        bp.addPoint(new Point(x, y));
        persistence.save(bp);
    }
}
