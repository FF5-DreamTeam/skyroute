//package com.skyroute.skyroute.booking.service;
//
//import com.skyroute.skyroute.booking.dto.BookingMapper;
//import com.skyroute.skyroute.booking.dto.BookingRequest;
//import com.skyroute.skyroute.booking.dto.BookingResponse;
//import com.skyroute.skyroute.booking.entity.Booking;
//import com.skyroute.skyroute.flight.entity.Flight;
//import com.skyroute.skyroute.flight.repository.FlightRepository;
//import com.skyroute.skyroute.flight.service.publicapi.FlightPublicService;
//import com.skyroute.skyroute.shared.exception.custom_exception.AccessDeniedException;
//import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
//import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
//import com.skyroute.skyroute.booking.repository.BookingRepository;
//import com.skyroute.skyroute.user.entity.User;
//import com.skyroute.skyroute.user.enums.Role;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Set;
//
//@Service
//public class BookingServiceImpl implements BookingService{
//    private final BookingRepository bookingRepository;
//    private final FlightPublicService flightPublicService;
//
//    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "bookingNumber", "bookingStatus", "createdAt", "flightNumber");
//
//    public BookingServiceImpl(BookingRepository bookingRepository, FlightPublicService flightPublicService, FlightRepository flightRepository, FlightPublicService flightPublicService1) {
//        this.bookingRepository = bookingRepository;
//        this.flightPublicService = flightPublicService;
//    }
//
//    @Override
//    public Page<BookingResponse> getAllBookingsAdmin(int page, int size, String sortBy, String sortDirection) {
//        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
//        return bookingRepository.findAll(pageable).map(booking -> BookingMapper.toDto(booking));
//    }
//
//    @Override
//    public Page<BookingResponse> getAllBookingsUser(User user, int page, int size, String sortBy, String sortDirection) {
//        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
//        return bookingRepository.findAllByUser(pageable, user).map(booking -> BookingMapper.toDto(booking));
//    }
//
//    @Override
//    public BookingResponse getBookingById(Long id, User user) {
//        Booking booking = findBookingById(id);
//        validateUserAccess(booking, user);
//        return BookingMapper.toDto(booking);
//    }
//
//    @Override
//    @Transactional
//    public BookingResponse createBooking(BookingRequest request, User user) {
//        Flight flight = flightPublicService.findById(request.flightId());
//        validateFlightBookingEligibility(request.flightId(), request.bookedSeats());
//        Double totalPrice = calculateTotalPrice(flight, request.bookedSeats());
//        Booking booking = BookingMapper.toEntity(request, user, flight, totalPrice);
//        flightPublicService.bookSeats(request.flightId(), request.bookedSeats());
//        Booking savedBooking = bookingRepository.save(booking);
//
//        return BookingMapper.toDto(savedBooking);
//    }
//
//    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
//        if (page < 0) throw new IllegalArgumentException("Page index must be 0 or greater");
//        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");
//
//        int maxSize = 100;
//        size = Math.min(size, maxSize);
//
//        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
//            sortBy = "createdAt";
//        }
//
//        if (!"ASC".equalsIgnoreCase(sortDirection) && !"DESC".equalsIgnoreCase(sortDirection)) {
//            sortDirection = "ASC";
//        }
//
//        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
//        return PageRequest.of(page, size, sort);
//    }
//
//    private Booking findBookingById(Long id) {
//        return bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
//    }
//
//    private void validateUserAccess(Booking booking, User user) {
//        if (user.getRole() == Role.USER && !booking.getUser().getId().equals(user.getId())) {
//            throw new AccessDeniedException("User cannot access this booking");
//            }
//    }
//
//    private void validateFlightBookingEligibility(Long flightId, int requestedSeats) {
//        if (!flightPublicService.isFlightAvailable(flightId)) {
//            throw new BusinessException("Flight not available for booking");
//        }
//
//        if(!flightPublicService.hasAvailableSeats(flightId, requestedSeats)) {
//            Flight flight = flightPublicService.findById(flightId);
//            throw new BusinessException("Not enough seats available. Requested: " + requestedSeats + ". Available: " + flight.getAvailableSeats());
//        }
//    }
//
//    private Double calculateTotalPrice(Flight flight, int bookedSeats) {
//        if (bookedSeats <= 0) {
//            throw  new IllegalArgumentException("Number of seats booked mut be positive");
//        }
//
//        return flight.getPrice() * bookedSeats;
//    }
//}
