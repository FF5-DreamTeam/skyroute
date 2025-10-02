package com.skyroute.skyroute.booking;

import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.specification.BookingSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BookingSpecificationUnitTest {

    @Mock
    private Root<Booking> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Nested
    class HasStatusTest {

        @Test
        void hasStatus_shouldReturnPredicate_whenStatusProvided() {
            BookingStatus status = BookingStatus.CREATED;
            when(criteriaBuilder.equal(any(), eq(status))).thenReturn(predicate);
            when(root.get("bookingStatus")).thenReturn(null);
            Specification<Booking> specification = BookingSpecification.hasStatus(status);
            Predicate result = specification.toPredicate(root, query, criteriaBuilder);

            assertNotNull(result);

            verify(criteriaBuilder).equal(any(), eq(status));
        }
    }


}
