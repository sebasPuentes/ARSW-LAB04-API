package edu.eci.arsw.blueprints;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.eci.arsw.blueprints.filters.BlueprintsFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.PostgresBlueprintPersistence;
import edu.eci.arsw.blueprints.services.BlueprintsServices;

@ExtendWith(MockitoExtension.class)
class BlueprintsServicesTest {

    @Mock
    private PostgresBlueprintPersistence persistence;

    @Mock
    private BlueprintsFilter filter;

    @InjectMocks
    private BlueprintsServices services;

    private Blueprint sampleBlueprint;

    @BeforeEach
    void setUp() {
        sampleBlueprint = new Blueprint("alice", "house", List.of(new Point(1, 2), new Point(3, 4)));
    }

    @Test
    void shouldAddNewBlueprintSavesSuccessfully() throws BlueprintPersistenceException {
        when(persistence.findByAuthorAndName("alice", "house")).thenReturn(Optional.empty());

        services.addNewBlueprint(sampleBlueprint);

        verify(persistence).save(sampleBlueprint);
    }

    @Test
    void shouldAddDuplicateBlueprintThrowsException() {
        when(persistence.findByAuthorAndName("alice", "house")).thenReturn(Optional.of(sampleBlueprint));

        assertThrows(BlueprintPersistenceException.class, () -> services.addNewBlueprint(sampleBlueprint));

        verify(persistence, never()).save(any());
    }

    @Test
    void shouldGetBlueprintsByAuthorReturnsCorrectSet() throws BlueprintNotFoundException {
        Blueprint bp1 = new Blueprint("carlos", "bridge", List.of(new Point(1, 1)));
        Blueprint bp2 = new Blueprint("carlos", "tower", List.of(new Point(2, 2)));
        Set<Blueprint> expected = new HashSet<>(Set.of(bp1, bp2));
        when(persistence.findByAuthor("carlos")).thenReturn(expected);

        Set<Blueprint> result = services.getBlueprintsByAuthor("carlos");

        assertEquals(2, result.size());
        assertTrue(result.contains(bp1));
        assertTrue(result.contains(bp2));
    }

    @Test
    void shouldGetBlueprintsByUnknownAuthorThrowsException() {
        when(persistence.findByAuthor("unknown")).thenReturn(new HashSet<>());

        assertThrows(BlueprintNotFoundException.class, () -> services.getBlueprintsByAuthor("unknown"));
    }

    @Test
    void shouldGetBlueprintAppliesFilter() throws BlueprintNotFoundException {
        Blueprint filtered = new Blueprint("alice", "house", List.of(new Point(1, 2)));
        when(persistence.findByAuthorAndName("alice", "house")).thenReturn(Optional.of(sampleBlueprint));
        when(filter.apply(sampleBlueprint)).thenReturn(filtered);

        Blueprint result = services.getBlueprint("alice", "house");

        assertEquals(1, result.getPoints().size());
        verify(filter).apply(sampleBlueprint);
    }
}
