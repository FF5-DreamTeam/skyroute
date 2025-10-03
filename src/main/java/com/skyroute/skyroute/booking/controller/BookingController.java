package com.skyroute.skyroute.booking.controller;

import com.skyroute.skyroute.booking.dto.BookingRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.booking.service.BookingService;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Operations related to flight bookings")
@SecurityRequirement(name = "Bearer Authentication")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;


    @Operation(
            summary = "Get all bookings (Admin only)",
            description = "Retrieve all bookings with pagination and sorting")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Bookings retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "Access denied, admin role required", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>> getAllBookingsAdmin(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-based") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of items per page (1-100)") @Min(1) @Max(10) int size,
            @RequestParam(defaultValue = "createdAt") @Parameter(description = "Field to sort by") String sortBy,
            @RequestParam(defaultValue = "DESC") @Parameter(description = "Sort direction (ASC/DESC)") String sortDirection) {
        Page<BookingResponse> bookingsResponse = bookingService.getAllBookingsAdmin(page, size, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(bookingsResponse);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get user's bookings",
            description = "Retrieve bookings for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User bookings retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/user")
    public ResponseEntity<Page<BookingResponse>> getAllBookingsUser(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (1-100)") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "ASC") String sortDirection) {
        User user = userService.getCurrentUser();
        Page<BookingResponse> bookingsResponse = bookingService.getAllBookingsUser(user, page, size, sortBy, sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(bookingsResponse);
    }

    @Operation(
            summary = "Get booking by ID",
            description = "Retrieves a specific booking by its ID. Users can only access their own bookings, admins can access any booking."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@Parameter(description = "Booking ID", required = true) @PathVariable Long id) {
        User user = userService.getCurrentUser();
        BookingResponse bookingResponse = bookingService.getBookingById(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(bookingResponse);
    }

    @Operation(
            summary = "Create a new booking",
            description = "Creates a new flight booking for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Booking created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid booking request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Flight not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Not enough seats available", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Parameter(description = "Booking request details", required = true) @Valid @RequestBody BookingRequest bookingRequest) {
        User user = userService.getCurrentUser();
        BookingResponse bookingResponse = bookingService.createBooking(bookingRequest, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    @Operation(
            summary = "Update booking status",
            description = "Updates the status of a booking. Users can only cancel their own bookings in CREATED status, admins can update any booking."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(@Parameter(description = "Booking ID", required = true) @PathVariable Long id, @Parameter(description = "New booking status", required = true) @RequestParam BookingStatus status) {
        User user = userService.getCurrentUser();
        BookingResponse bookingResponse = bookingService.updateBookingStatus(id, status, user);
        return ResponseEntity.status(HttpStatus.OK).body(bookingResponse);
    }

    @Operation(
            summary = "Confirm booking",
            description = "Confirms a booking by changing its status to CONFIRMED. Users can only confirm their own bookings."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking confirmed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@Parameter(description = "Booking ID", required = true) @PathVariable Long id) {
        User user = userService.getCurrentUser();
        BookingResponse bookingResponse = bookingService.confirmBooking(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(bookingResponse);
    }

    @Operation(
            summary = "Cancel booking",
            description = "Cancels a booking by changing its status to CANCELLED. Users can only cancel their own bookings."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking cancelled successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        User user = userService.getCurrentUser();
        bookingService.cancelBooking(id, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update passenger names",
            description = "Updates the passenger names for a booking. Users can only update bookings in CREATED status, admins can update any booking."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Passenger names updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid passenger names", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied or booking cannot be modified", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/{id}/passenger-names")
    public ResponseEntity<BookingResponse> updatePassengerNames(@Parameter(description = "Booking ID", required = true) @PathVariable Long id, @Parameter(description = "List of passenger names", required = true) @RequestBody List<String> names) {
        User user = userService.getCurrentUser();
        BookingResponse bookingResponse = bookingService.updatePassengerNames(id, names, user);
        return ResponseEntity.status(HttpStatus.OK).body(bookingResponse);
    }

    @Operation(
            summary = "Update passenger birth dates",
            description = "Updates the passenger birth dates for a booking. Users can only update bookings in CREATED status, admins can update any booking."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Passenger birth dates updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid birth dates", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied or booking cannot be modified", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/{id}/passenger-birth-dates")
    public ResponseEntity<BookingResponse> updatePassengerBirthDates(@Parameter(description = "Booking ID", required = true) @PathVariable Long id, @Parameter(description = "List of passenger birth dates (yyyy-MM-dd format)", required = true) @RequestBody List<LocalDate> birthDates) {
        User user = userService.getCurrentUser();
        BookingResponse bookingResponse = bookingService.updatePassengerBirthDates(id, birthDates, user);
        return ResponseEntity.status(HttpStatus.OK).body(bookingResponse);
    }

    @Operation(
            summary = "Delete booking",
            description = "Deletes a booking completely. Users can only delete their own bookings in CREATED status, admins can delete any booking."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied or booking cannot be deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@Parameter(description = "Booking ID", required = true) @PathVariable Long id) {
        User user = userService.getCurrentUser();
        bookingService.deleteBooking(id, user);
        return ResponseEntity.noContent().build();
    }
 }