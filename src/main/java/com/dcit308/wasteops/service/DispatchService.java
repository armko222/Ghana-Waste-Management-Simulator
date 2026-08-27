package com.dcit308.wasteops.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.dcit308.wasteops.algorithms.optimisation.GreedyDispatch;
import com.dcit308.wasteops.algorithms.optimisation.KnapsackDP;
import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.db.ServiceRequestRepository;
import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.scheduling.FifoDispatcher;
import com.dcit308.wasteops.scheduling.PriorityDispatcher;
import com.dcit308.wasteops.scheduling.UrgencyDispatcher;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.structures.QueueADT;

/**
 * Service layer for Issue #13 dispatch and optimisation operations.
 *
 * <p>
 * Exposes:
 * <ul>
 *     <li>FIFO dispatch</li>
 *     <li>Urgency dispatch</li>
 *     <li>Priority dispatch</li>
 *     <li>Greedy budget-constrained selection</li>
 *     <li>Dynamic-programming budget-constrained selection</li>
 * </ul>
 *
 * <p>
 * The service obtains pending requests from ServiceRequestRepository.
 * The dispatch rules themselves remain inside the scheduling package.
 *
 * <p>
 * This separation is intentional: a future graphical Java UI can call
 * this service directly without depending on ConsoleMenu.
 *
 * Owned by Issue #13.
 */
public class DispatchService {

    private final ServiceRequestRepository repository;

    private final FifoDispatcher fifoDispatcher;
    private final UrgencyDispatcher urgencyDispatcher;
    private final PriorityDispatcher priorityDispatcher;
    private final GreedyDispatch greedyDispatch;
    private final KnapsackDP knapsackDP;

    /**
     * Main constructor used by the application.
     *
     * @param databaseManager application's shared database manager
     */
    public DispatchService(DatabaseManager databaseManager) {

        this.repository = new ServiceRequestRepository(databaseManager);

        this.fifoDispatcher =
                new FifoDispatcher(new SimpleQueue<>());

        this.urgencyDispatcher =
                new UrgencyDispatcher();

        /*
         * PriorityDispatcher receives its reference time when the
         * dispatch operation is performed because the reference should
         * be based on the actual pending dataset.
         */
        this.priorityDispatcher = null;

        this.greedyDispatch =
                new GreedyDispatch();

        this.knapsackDP =
                new KnapsackDP();
    }

    /**
     * Constructor retained for unit testing and dependency injection.
     */
    public DispatchService(FifoDispatcher fifoDispatcher,
                           UrgencyDispatcher urgencyDispatcher,
                           PriorityDispatcher priorityDispatcher,
                           GreedyDispatch greedyDispatch,
                           KnapsackDP knapsackDP) {

        this.repository = null;
        this.fifoDispatcher = fifoDispatcher;
        this.urgencyDispatcher = urgencyDispatcher;
        this.priorityDispatcher = priorityDispatcher;
        this.greedyDispatch = greedyDispatch;
        this.knapsackDP = knapsackDP;
    }

    /**
     * Returns the currently pending requests.
     *
     * This is useful for the console now and the graphical UI later.
     */
    public List<ServiceRequest> getPendingRequests() {

        if (repository == null) {
            throw new IllegalStateException(
                    "This DispatchService instance has no repository."
            );
        }

        return repository.findPending();
    }

    /**
     * FIFO dispatch.
     *
     * Rule:
     *     time_submitted ascending.
     *
     * No urgency, priority, category, or other factor is considered.
     */
    public ServiceRequest dispatchNextFifo() {

        List<ServiceRequest> pending = getPendingRequests();

        if (pending.isEmpty()) {
            return null;
        }

        fifoDispatcher.loadPending(pending);

        return fifoDispatcher.getNextRequest();
    }

    /**
     * Urgency dispatch.
     *
     * Rule:
     *     urgency descending.
     *
     * Tie-break:
     *     earlier time_submitted first.
     */
    public ServiceRequest dispatchNextByUrgency() {

        List<ServiceRequest> pending = getPendingRequests();

        return urgencyDispatcher.getNextRequest(pending);
    }

    /**
     * Compatibility overload useful for tests and future callers that
     * already have a pending list.
     */
    public ServiceRequest dispatchNextByUrgency(
            List<ServiceRequest> pendingRequests) {

        return urgencyDispatcher.getNextRequest(pendingRequests);
    }

    /**
     * Priority dispatch.
     *
     * Rule:
     *     HIGH -> MEDIUM -> LOW
     *
     * Tie-break within each tier:
     *     urgency descending
     *
     * Final tie-break:
     *     time_submitted ascending.
     */
    public ServiceRequest dispatchNextByPriority() {

        List<ServiceRequest> pending = getPendingRequests();

        if (pending.isEmpty()) {
            return null;
        }

        LocalDateTime referenceTime =
                pending.stream()
                        .map(ServiceRequest::getTimeSubmitted)
                        .min(LocalDateTime::compareTo)
                        .orElseThrow();

        PriorityQueueADT<ServiceRequest> queue =
                new SimplePriorityQueue<>();

        PriorityDispatcher dispatcher =
                new PriorityDispatcher(queue, referenceTime);

        dispatcher.loadPending(pending);

        return dispatcher.getNextRequest();
    }

    /**
     * Greedy budget-constrained request selection.
     */
    public GreedyDispatch.Selection selectByGreedy(
            List<ServiceRequest> candidates,
            int budget) {

        validateBudget(budget);

        return greedyDispatch.selectRequests(candidates, budget);
    }

    /**
     * Dynamic-programming budget-constrained request selection.
     */
    public KnapsackDP.Selection selectByDp(
            List<ServiceRequest> candidates,
            int budget) {

        validateBudget(budget);

        return knapsackDP.selectRequests(candidates, budget);
    }

    /**
     * Convenience method for the console and future UI.
     *
     * Uses the currently pending requests as candidates.
     */
    public GreedyDispatch.Selection selectPendingByGreedy(int budget) {

        return selectByGreedy(getPendingRequests(), budget);
    }

    /**
     * Convenience method for the console and future UI.
     *
     * Uses the currently pending requests as candidates.
     */
    public KnapsackDP.Selection selectPendingByDp(int budget) {

        return selectByDp(getPendingRequests(), budget);
    }

    private void validateBudget(int budget) {

        if (budget < 0) {
            throw new IllegalArgumentException(
                    "Budget cannot be negative: " + budget
            );
        }
    }

    /**
     * Small FIFO queue used only as an adapter for the FIFO dispatcher.
     *
     * The actual queue contract remains QueueADT, so Issue #4's real
     * implementation can replace this adapter without changing the
     * dispatch rule.
     */
    private static class SimpleQueue<T> implements QueueADT<T> {

        private final List<T> items = new ArrayList<>();

        @Override
        public void enqueue(T value) {
            items.add(value);
        }

        @Override
        public T dequeue() {

            if (items.isEmpty()) {
                throw new NoSuchElementException(
                        "Cannot dequeue from an empty queue"
                );
            }

            return items.remove(0);
        }

        @Override
        public T peekFront() {

            if (items.isEmpty()) {
                throw new NoSuchElementException(
                        "Cannot peek at an empty queue"
                );
            }

            return items.get(0);
        }

        @Override
        public boolean isEmpty() {
            return items.isEmpty();
        }

        @Override
        public boolean isFull() {
            return false;
        }

        @Override
        public int size() {
            return items.size();
        }
    }

    /**
     * Small priority queue adapter used until Issue #5's final
     * PriorityQueueADT implementation is confirmed.
     *
     * It obeys the same interface as the real heap and therefore keeps
     * the dispatch logic independent of the underlying implementation.
     */
    private static class SimplePriorityQueue<T>
            implements PriorityQueueADT<T> {

        private static class Entry<T> {

            private final int priority;
            private final T value;

            private Entry(int priority, T value) {
                this.priority = priority;
                this.value = value;
            }
        }

        private final List<Entry<T>> entries =
                new ArrayList<>();

        @Override
        public void insert(int priority, T value) {
            entries.add(new Entry<>(priority, value));
        }

        @Override
        public T extractMin() {

            if (entries.isEmpty()) {
                throw new NoSuchElementException(
                        "Cannot extract from an empty priority queue"
                );
            }

            int bestIndex = 0;

            for (int i = 1; i < entries.size(); i++) {

                if (entries.get(i).priority
                        < entries.get(bestIndex).priority) {

                    bestIndex = i;
                }
            }

            return entries.remove(bestIndex).value;
        }

        @Override
        public T peekMin() {

            if (entries.isEmpty()) {
                throw new NoSuchElementException(
                        "Cannot peek at an empty priority queue"
                );
            }

            int bestIndex = 0;

            for (int i = 1; i < entries.size(); i++) {

                if (entries.get(i).priority
                        < entries.get(bestIndex).priority) {

                    bestIndex = i;
                }
            }

            return entries.get(bestIndex).value;
        }

        @Override
        public boolean isEmpty() {
            return entries.isEmpty();
        }

        @Override
        public int size() {
            return entries.size();
        }
    }
}