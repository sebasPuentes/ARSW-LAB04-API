package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.filters.BlueprintsFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.PostgresBlueprintPersistence;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BlueprintsServices {

    private final BlueprintPersistence persistence;
    private final BlueprintsFilter filter;
    private final PostgresBlueprintPersistence postgresPersistence;

    public BlueprintsServices(BlueprintPersistence persistence, BlueprintsFilter filter, PostgresBlueprintPersistence postgresPersistence) {
        this.persistence = persistence;
        this.filter = filter;
        this.postgresPersistence = postgresPersistence;
    }

    public void addNewBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        //persistence.saveBlueprint(bp);
        postgresPersistence.save(bp);
    }

    public Set<Blueprint> getAllBlueprints() {
        //return persistence.getAllBlueprints();
        return new HashSet<>(postgresPersistence.findAll());
    }

    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        //return persistence.getBlueprintsByAuthor(author);
        Set<Blueprint> blueprints = postgresPersistence.findByAuthor(author);
        if (blueprints.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints for author: " + author);
        }
        return blueprints;
    }

    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        return filter.apply(postgresPersistence.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException("Blueprint not found")));
    }

    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        //persistence.addPoint(author, name, x, y);
        Blueprint bp = getBlueprint(author, name);
        bp.addPoint(new Point(x, y));
        postgresPersistence.save(bp);
    }
}
