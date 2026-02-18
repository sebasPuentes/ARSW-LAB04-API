package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import org.springframework.stereotype.Component;

/**
 * Default filter: returns the blueprint unchanged.
 * Always registered. When a profiled filter (redundancy, undersampling) is active,
 * that filter is marked @Primary and takes precedence over this one.
 */
@Component
public class IdentityFilter implements BlueprintsFilter {
    @Override
    public Blueprint apply(Blueprint bp) { return bp; }
}
