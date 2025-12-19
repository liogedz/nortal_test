# ToDo list Nortal LEAP 2026 Coding Assignment

1) **Prevent double loans and respect queues**

- [x] A book already on loan must not be loaned again.
- [x] If a reservation queue exists, only the head of the queue should receive the book (no line-jumping via direct borrow).
- [x] Returns should only succeed when initiated by the current borrower.

2) **Reservation lifecycle**

- [x] Members may reserve a loaned book; duplicate reservations by the same member should be rejected.
- [x] Reserving an available book should immediately loan it to the reserver (if eligible).
- [x] Returning a book must hand it to the next eligible reserver in order; skip ineligible/missing members and
  continue. Surface who received it.
- [x] Keep the queue consistent when handoffs happen.


3) Borrow-limit enforcement & clarity

- [x] Enforce the existing max-loan cap cleanly and efficiently (limit is already defined in code).
- [x] Refactor the current approach so the rule is obvious and not needlessly expensive.

4) `gradle` tests

- [x] - Run `./gradlew test` and `./gradlew spotlessApply` before sharing and ensure they pass.

