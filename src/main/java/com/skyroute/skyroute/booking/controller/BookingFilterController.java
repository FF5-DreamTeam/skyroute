package com.skyroute.skyroute.booking.controller;

import com.skyroute.skyroute.booking.dto.BookingFilterRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.service.BookingFilterService;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/bookings/filter")
@Tag(name = "Booking filter", description = "Operations to filter bookings with advanced criteria")
@SecurityRequirement(name ="Bearer Authentication")
public class BookingFilterController {
    private final BookingFilterService bookingFilterService;
    private final UserService userService;

    public BookingFilterController(BookingFilterService bookingFilterService, UserService userService) {
        this.bookingFilterService = bookingFilterService;
        this.userService = userService;
    }

    @Operation(
            summary = "Filter bookings with advanced criteria",
            description = "Filter bookings by multiple criteria. Users can only see their own bookings."
    )
    @PreAuthorize("hasRole('USER')")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking filtered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/my-bookings")
    public ResponseEntity<Page<BookingResponse>> filterMyBookings(
            @Parameter(description = "Booking status filter") @RequestParam(required = false) BookingStatus bookingStatus,
            @Parameter(description = "Booking number filter") @RequestParam(required = false) String bookingNumber,
            @Parameter(description = "Flight departure date (ISO date format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate flightDepartureDate,
            @Parameter(description = "Minimum price filter") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Origin airport city filter") @RequestParam(required = false) String originAirport,
            @Parameter(description = "Destination airport city filter") @RequestParam(required = false) String destinationAirport,
            @Parameter(description = "Passenger name filter") @RequestParam(required = false) String passengerName,
            @Parameter(description = "Filter only future flights") @RequestParam(required = false) Boolean futureFlightsOnly,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        User user = userService.getCurrentUser();
        BookingFilterRequest filterRequest = new BookingFilterRequest(
                bookingStatus, bookingNumber, null, null, flightDepartureDate, null, null, minPrice, null, null, null, null, null, null, null, null, originAirport, destinationAirport, passengerName, futureFlightsOnly,null, null, null, null
        );
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        Page<BookingResponse> bookingResponses = bookingFilterService.filterBookings(filterRequest, pageable, user);
        return ResponseEntity.status(HttpStatus.OK).body(bookingResponses);
    }

    @Operation(
            summary = "Filter bookings with advanced criteria",
            description = "Filter bookings by multiple criteria. Only Admin."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking filtered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameter", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden, role Admin required", content = @Content)
    })
    @GetMapping("/admin")
    public ResponseEntity<Page<BookingResponse>> filterAdminBookings(
            @Parameter(description = "Booking status filter") @RequestParam(required = false) BookingStatus bookingStatus,
            @Parameter(description = "Booking number filter") @RequestParam(required = false) String bookingNumber,
            @Parameter(description = "Flight departure date (ISO date format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate flightDepartureDate,
            @Parameter(description = "Minimum price filter") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price filter") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "User ID filter (admin only)") @RequestParam(required = false) Long userId,
            @Parameter(description = "User email filter (admin only)") @RequestParam(required = false) String userEmail,
            @Parameter(description = "User name filter (admin only)") @RequestParam(required = false) String userName,
            @Parameter(description = "Flight ID filter") @RequestParam(required = false) Long flightId,
            @Parameter(description = "Flight number filter") @RequestParam(required = false) String flightNumber,
            @Parameter(description = "Origin airport city filter") @RequestParam(required = false) String originAirport,
            @Parameter(description = "Destination airport city filter") @RequestParam(required = false) String destinationAirport,
            @Parameter(description = "Passenger name filter") @RequestParam(required = false) String passengerName,
            @Parameter(description = "Filter only future flights") @RequestParam(required = false) Boolean futureFlightsOnly,
            @Parameter(description = "Filter only active bookings (not cancelled)") @RequestParam(required = false) Boolean activeOnly,
            @Parameter(description = "Filter only pending bookings") @RequestParam(required = false) Boolean pendingOnly,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        User admin = userService.getCurrentUser();
        BookingFilterRequest filterRequest = new BookingFilterRequest(
                bookingStatus, bookingNumber, null, null, flightDepartureDate, null, null, minPrice, maxPrice, null, null, userId, userEmail, userName, flightId, flightNumber, originAirport, destinationAirport, passengerName, futureFlightsOnly, activeOnly, null, null, pendingOnly
        );
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        Page<BookingResponse> bookingResponses = bookingFilterService.filterBookings(filterRequest, pageable, admin);
        return ResponseEntity.status(HttpStatus.OK).body(bookingResponses);
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        return PageRequest.of(page, size, Sort.by(direction,sortBy));
    }
}
