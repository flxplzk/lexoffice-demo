package de.nordakademie.iaa.examsurvey.controller.filtercriterion;


import com.google.common.collect.Sets;

import java.util.Set;

/**
 * UtilClass for enabling easy queries of surveys.
 *
 * @author felix plazek
 */
public class FilterCriteria {
    private final FilterType filterType;

    private FilterCriteria(FilterType type) {
        this.filterType = type;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    /**
     * @param filterExpressions to filter with
     * @return Set of supported Filters for given {@param filterExpressions}
     */
    public static Set<FilterCriteria> of(final Set<String> filterExpressions) {
        final Set<FilterCriteria> criteria = Sets.newHashSet();
        filterExpressions.forEach(filterString -> {
            final FilterType type = FilterType.of(filterString);
            if (type != null) {
                criteria.add(new FilterCriteria(type));
            }
        });
        return criteria;
    }

    /**
     * enum for supported filter types
     */
    public enum FilterType {
        OWN("own"),
        OPEN("open"),
        PARTICIPATED("participated");

        private final String target;

        FilterType(String target) {
            this.target = target;
        }

        private static FilterType of(final String filterExpression) {
            for (FilterType filterType : FilterType.values()) {
                if (filterType.target.equals(filterExpression)) {
                    return filterType;
                }
            }
            return null;
        }
    }
}
