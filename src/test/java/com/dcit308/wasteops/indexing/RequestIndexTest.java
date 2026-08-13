package com.dcit308.wasteops.indexing;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.BinarySearchTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #6's RequestIndex.
 *
 * The index keys on deadline and buckets every request sharing that
 * deadline, because nothing in the schema makes deadlines unique.
 */
class RequestIndexTest {

    private static final LocalDateTime NINE_AM = LocalDateTime.parse("2026-07-01T09:00");
    private static final LocalDateTime ELEVEN_THIRTY = LocalDateTime.parse("2026-07-01T11:30");

    private RequestIndex index;

    @BeforeEach
    void setUp() {
        index = new RequestIndex(new BinarySearchTree<>());
    }

    private static ServiceRequest requestDueAt(String id, LocalDateTime deadline) {
        return new ServiceRequest(
                id, "L001", "L002", "Medical", 3,
                ServiceRequest.Priority.MEDIUM,
                deadline.minusHours(1), deadline);
    }

    @Test
    @DisplayName("a request can be found by the deadline it was indexed under")
    void findsIndexedRequest() {
        ServiceRequest request = requestDueAt("Q001", NINE_AM);
        index.indexByDeadline(request);

        List<ServiceRequest> found = index.findByDeadline(NINE_AM.toString());

        assertEquals(1, found.size());
        assertEquals("Q001", found.get(0).getRequestId());
    }

    @Test
    @DisplayName("requests sharing a deadline are all kept, in insertion order")
    void keepsEveryRequestSharingADeadline() {
        index.indexByDeadline(requestDueAt("Q001", NINE_AM));
        index.indexByDeadline(requestDueAt("Q002", NINE_AM));
        index.indexByDeadline(requestDueAt("Q003", NINE_AM));

        List<ServiceRequest> found = index.findByDeadline(NINE_AM.toString());

        assertEquals(List.of("Q001", "Q002", "Q003"),
                found.stream().map(ServiceRequest::getRequestId).toList(),
                "keying on deadline alone must not let a later request overwrite an earlier one");
    }

    @Test
    @DisplayName("distinct deadlines are kept in separate buckets")
    void separatesDistinctDeadlines() {
        index.indexByDeadline(requestDueAt("Q001", NINE_AM));
        index.indexByDeadline(requestDueAt("Q002", ELEVEN_THIRTY));

        assertEquals("Q001", index.findByDeadline(NINE_AM.toString()).get(0).getRequestId());
        assertEquals("Q002", index.findByDeadline(ELEVEN_THIRTY.toString()).get(0).getRequestId());
    }

    @Test
    @DisplayName("an absent deadline returns an empty list, never null")
    void returnsEmptyListForAbsentDeadline() {
        index.indexByDeadline(requestDueAt("Q001", NINE_AM));

        List<ServiceRequest> found = index.findByDeadline("2026-12-25T00:00");

        assertNotNull(found, "callers must be able to iterate without a null check");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("an empty index returns an empty list")
    void returnsEmptyListWhenNothingIndexed() {
        assertTrue(index.findByDeadline(NINE_AM.toString()).isEmpty());
    }

    @Test
    @DisplayName("the returned bucket cannot be mutated by callers")
    void returnedBucketIsUnmodifiable() {
        index.indexByDeadline(requestDueAt("Q001", NINE_AM));
        List<ServiceRequest> found = index.findByDeadline(NINE_AM.toString());

        assertThrows(UnsupportedOperationException.class,
                () -> found.add(requestDueAt("Q999", NINE_AM)),
                "the index must not hand out a mutable view of its own storage");
    }

    @Test
    @DisplayName("indexing the same request twice records it twice")
    void indexingTwiceRecordsTwice() {
        ServiceRequest request = requestDueAt("Q001", NINE_AM);
        index.indexByDeadline(request);
        index.indexByDeadline(request);

        assertEquals(2, index.findByDeadline(NINE_AM.toString()).size(),
                "the index does not de-duplicate — callers index each request once");
    }

    @Test
    @DisplayName("ServiceRequest rejects an urgency outside 1-5")
    void rejectsOutOfRangeUrgency() {
        assertThrows(IllegalArgumentException.class, () -> new ServiceRequest(
                "Q001", "L001", "L002", "Medical", 6,
                ServiceRequest.Priority.HIGH, NINE_AM.minusHours(1), NINE_AM));
    }
}
